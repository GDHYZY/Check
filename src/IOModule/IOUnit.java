package IOModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import PicVectorModule.PicVector;
import TextVectorModule.TextVector;
import BaseUtil.*;
import CompareModule.CompareUnit;
import DateBaseModule.DBUnit;

public class IOUnit {
	/* 单文档直接对比数据库
	 * 输入： 路径， 文件名
	 * 输出： true代表存在， false代表不存在
	 */
	
	public String getWord(String path) {
		InputStream in = null;
		String text = "";
		File file = new File(path);
		if (! file.exists()){
			System.out.println(path + " is not exists!");
			return "";
		}
		try {
			in = new FileInputStream(file);
			if (!in.markSupported()) {
				in = new PushbackInputStream(in, 8);
			}
			String[] sp = path.split("\\.");
			if (sp[sp.length - 1].equals("doc")) {
				WordExtractor extractor = new WordExtractor(in);
				text = extractor.getText().toString();
			} else {
				XWPFDocument document = new XWPFDocument(in);
				XWPFWordExtractor extractor = new XWPFWordExtractor(document);
				text = extractor.getText().toString();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text.trim();
	}
	
	//读取输入的文档
	public void readWord(String path,String[] name)
	{		
		ReportData[] reports = new ReportData[name.length];
		Thread[] thread = new Thread[name.length *  2];
		
		try {
			for(int i=0;i<name.length;i++)
			{
				//name 的命名格式为 学号+姓名
				String text = "";
				String AbsolutePath=path+"\\"+name[i];
			
				String[] sp = name[i].split("\\.");

				reports[i] = new ReportData();
				reports[i].Title = name[i];
//				reports[i].StuNum = value[0];
//				reports[i].StuName = value[1];
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				reports[i].Date = df.format(new Date());
				
				//获取文本
				text = getWord(AbsolutePath);
				reports[i].WordNum = text.length();
				reports[i].Path = AbsolutePath;
				
				PicVector pv = null;
				if (sp[sp.length - 1].equals("doc")){
					FileInputStream in = new FileInputStream(new File(AbsolutePath));
					HWPFDocument doc = new HWPFDocument(in);
					pv = new PicVector(doc, i, reports);
				}else {
					FileInputStream fis = new FileInputStream(new File(AbsolutePath));  
				    XWPFDocument document = new XWPFDocument(fis);  
				    List<XWPFPictureData> picList = document.getAllPictures();  
				    pv = new PicVector(i,reports,picList);
				}
				
				TextVector tv = new TextVector(text, i, reports);
				thread[i] = new Thread(tv);
				thread[i+name.length] = new Thread(pv);
			}
			//限制5个线程在跑，等待线程执行完毕
			for (int i = 0; i < name.length * 2; ){
				int j = i, k = i;
				while(j < i + 5 && j < name.length*2){
					thread[j++].start();
				}
				while(k < i + 5 && k < name.length*2){
					thread[k++].join();
				}
				i = j;
			}
			//清空线程
			for(int j = 0; j < name.length * 2; j++){
				thread[j] = null;
			}		
			
			GlobalData.getSingleton().m_InputData.addAll(new ArrayList<ReportData>(Arrays.asList(reports)));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	//保存对比结果
	public void Save() throws IOException, WriteException {
		ArrayList<ExportData> output = GlobalData.getSingleton().m_ExportData;
		if (output == null || output.isEmpty()){
			return ;
		}
		
		for (int i = 0; i < output.size(); ++i){
			ExportData export = output.get(i);
			System.out.println(export.m_Target.Title +": 总相似度"+String.format("%.2f", (export.m_Similarity*100))
					+"%");
//			String text1 = getWord(export.m_Target.Path);
			
			for (int j = 0; j < export.m_Sample.size(); ++j){
				System.out.println("     与"+export.m_Sample.get(j).m_Sampledata.Title+
						"的相似度为:"+String.format("%.2f", (export.m_Sample.get(j).m_Similarity*100))+"%");
//				String text2 = getWord(export.m_Sample.get(j).m_Sampledata.Path);
				
				ArrayList<SimilarityParagraph> parlist = export.m_Sample.get(j).m_ParagraphSimilarity;
				for (int k = 0; k < parlist.size(); ++k){
					System.out.println("          第"+parlist.get(k).m_TargetParagraph+"与第"+
							parlist.get(k).m_SampleParagraph+"相似度为"+String.format("%.2f", (100*parlist.get(k).m_Similarity))+"%  有"+
							parlist.get(k).m_WordNumber+"个词相同");
//					int[] pos = export.m_Target.ParagraphMsg.get(parlist.get(k).m_TargetParagraph);
//					String tmp1 = text1.substring(pos[0], pos[1]).trim();
//					pos = export.m_Sample.get(j).m_Sampledata.ParagraphMsg.get(parlist.get(k).m_SampleParagraph);
//					String tmp2 = text2.substring(pos[0],pos[1]).trim();
					
//					System.out.print("          {");
//					for (String s: parlist.get(k).m_SimilarityText){
//						System.out.print(s+",");
//					}
//					System.out.println("}");
					
//					for (String s: parlist.get(k).m_SimilarityText){
//						tmp1 = tmp1.replace(s, "`");
//						tmp2 = tmp2.replace(s,"`");
//					}
					
//					System.out.println(">"+tmp1+"\n>"+tmp2);
//					System.out.println("-----------------------------------------------------------------");
				}
			}
		}
		System.out.println();
		
//		ArrayList TotalNameList = (ArrayList) object[0];
//		ArrayList TotalSimilaity = (ArrayList) object[1];
//
//		OutputStream os = new FileOutputStream(path);
//		WritableWorkbook wwb = Workbook.createWorkbook(os);
//		WritableSheet ws = wwb.createSheet("sheet1", 0);
//
//		Label numlabel = new Label(0, 0, "学号");  
//		Label namelabel = new Label(1, 0, "姓名");
//		Label Titlelabel = new Label(2, 0, "论文题目");
//		Label Similarity = new Label(5, 0, "相似度");
//		ws.addCell(numlabel);
//		ws.addCell(namelabel);
//		ws.addCell(Titlelabel);
//		ws.addCell(Similarity);
//
//		int k = 1;
//		for (int i = 0; i < TotalNameList.size(); i++) 
//		{
//			Label namelabel2 = new Label(0, k, (String) TotalNameList.get(i));
//			ws.addCell(namelabel2);
//			k++;
//		}
//		int n=2;
//		for(int j=0;j<TotalSimilaity.size();j++)
//		{
//			Label persimilarity=new Label(5,n,(String)TotalSimilaity.get(j)); 
//			ws.addCell(persimilarity);
//			n=n+2;
//		}
//		wwb.write();
//		wwb.close();
//		os.close();
	}
	
}

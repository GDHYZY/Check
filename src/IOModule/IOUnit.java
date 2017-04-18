package IOModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import TextVectorModule.TextVector;
import BaseUtil.*;
import DateBaseModule.DBUnit;

public class IOUnit {
	/* 单文档直接对比数据库
	 * 输入： 路径， 文件名
	 * 输出： true代表存在， false代表不存在
	 */
//	private Boolean _SingleWord(String path, String name){
//		return false;
//	}
	
	
	//读取输入的文档
	public void readWord(String path,String[] name)
	{
		InputStream  in = null;		
		
		ReporteDate[] reportes = new ReporteDate[name.length];
		Thread[] thread = new Thread[name.length];
		
		try {
			for(int i=0;i<name.length;i++)
			{
				//name 的命名格式为 学号+姓名
				String text = "";
				String AbsolutePath=path+"\\"+name[i];
				in = new FileInputStream(new File(AbsolutePath));
				String[] sp = name[i].split("\\.");
				String[] value = sp[0].split("-");
				
				reportes[i] = new ReporteDate();
				reportes[i].StuNum = value[0];
				reportes[i].StuName = value[1];
				
				//获取文本
				if (!in.markSupported()) {  
                    in = new PushbackInputStream(in,8);
                }  
				if(sp[sp.length-1].equals("doc")){
					WordExtractor extractor = new WordExtractor(in);
					text = extractor.getText();
				}
				else{
					XWPFDocument document = new XWPFDocument(in);  
		            XWPFWordExtractor extractor =new XWPFWordExtractor(document);  
		            text = extractor.getText();   
				}

				reportes[i].WordNum = text.length();
				TextVector tv = new TextVector(text, i, reportes);
				thread[i] = new Thread(tv);
				thread[i].start();
			}
			//等待线程执行完毕
			for(int j = 0; j < name.length; j++){
				thread[j].join();
			}		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//对比
		DBUnit db;
		try {
			db = new DBUnit();
			db.CreateDataBase("ReportesCheck");
			db.CreateReporteTable();
			for (int k = 0; k < name.length; k++){
				db.AddReportItems(reportes[k]);					
				db.CreateParagraphTable(reportes[k]);
				db.AddParagraphItems(reportes[k]);					
			}
			db.CloseDataBase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	//保存对比结果
	public void Save(Object[] object,String path) throws IOException, WriteException {
		ArrayList TotalNameList = (ArrayList) object[0];
		ArrayList TotalSimilaity = (ArrayList) object[1];

		OutputStream os = new FileOutputStream(path);
		WritableWorkbook wwb = Workbook.createWorkbook(os);
		WritableSheet ws = wwb.createSheet("sheet1", 0);

		Label numlabel = new Label(0, 0, "学号");  
		Label namelabel = new Label(1, 0, "姓名");
		Label Titlelabel = new Label(2, 0, "论文题目");
		Label Similarity = new Label(5, 0, "相似度");
		ws.addCell(numlabel);
		ws.addCell(namelabel);
		ws.addCell(Titlelabel);
		ws.addCell(Similarity);

		int k = 1;
		for (int i = 0; i < TotalNameList.size(); i++) 
		{
			Label namelabel2 = new Label(0, k, (String) TotalNameList.get(i));
			ws.addCell(namelabel2);
			k++;
		}
		int n=2;
		for(int j=0;j<TotalSimilaity.size();j++)
		{
			Label persimilarity=new Label(5,n,(String)TotalSimilaity.get(j)); 
			ws.addCell(persimilarity);
			n=n+2;
		}
		wwb.write();
		wwb.close();
		os.close();
	}
}

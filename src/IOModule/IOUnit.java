package IOModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
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
			LogUnit.getSingleton().writeLog("找不到路径"+path);
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
		LogUnit.getSingleton().writeLog("开始读取文件");
		try {
			for(int i=0;i<name.length;i++)
			{
				//name 的命名格式为 学号+姓名
				String text = "";
				String AbsolutePath=path+"\\"+name[i];
			
				String[] sp = name[i].split("\\.");

				reports[i] = new ReportData();
				reports[i].Title = name[i];
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
					thread[k].join();
					if (k >= name.length)
						LogUnit.getSingleton().writeLog(reports[k-name.length].Title+"读取完毕");
					++k;
				}
				i = j;
			}
			//清空线程
			for(int j = 0; j < name.length * 2; j++){
				thread[j] = null;
			}		
			GlobalData.getSingleton().m_ExportData.clear();
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
	
	//保存单个对比结果
	public void Export(ExportData exportdata, String Path){
		CreateDoc(exportdata, Path);
	}
	
	//保存对比结果
	public void Export(String Path) throws IOException, WriteException {
		ArrayList<ExportData> output = GlobalData.getSingleton().m_ExportData;
		if (output == null || output.isEmpty()){
			return ;
		}
		
		LogUnit.getSingleton().writeLog("开始导出文件");
		for (int i = 0; i < output.size(); ++i){
			ExportData export = output.get(i);
			System.out.println(export.m_Target.Title +": 总相似度"+String.format("%.2f", (export.m_Similarity*100))
					+"% 共有"+ export.m_WorNumber + "个词相似");
			if (export.m_Similarity != 0.0)
				CreateDoc(export, Path+"\\"+export.m_Target.Title);
			
			for (int j = 0; j < export.m_Sample.size(); ++j){
				if (export.m_Sample.get(j).m_Similarity != 0)
				System.out.println("     与"+export.m_Sample.get(j).m_Sampledata.Title+
						"的相似度为:"+String.format("%.2f", (export.m_Sample.get(j).m_Similarity*100))
						+"% 共有"+export.m_Sample.get(j).m_WordNumber+" 个词相似");
				else {
					System.err.println("     与"+export.m_Sample.get(j).m_Sampledata.Title+
							"的相似度为:"+String.format("%.2f", (export.m_Sample.get(j).m_Similarity*100))
							+"% 共有"+export.m_Sample.get(j).m_WordNumber+" 个词相似");
				}
			}
		}
		System.out.println();
	}
	
	public void CreateDoc(ExportData export, String path){
		String left = "</w:t></w:r><w:r><w:rPr><w:rFonts w:hint=\"eastAsia\"/><w:b w:val=\"0\"/><w:bCs w:val=\"0\"/>"
				+ "<w:sz w:val=\"21\"/><w:szCs w:val=\"21\"/><w:highlight w:val=\"none\"/><w:lang w:val=\"en-US\" w:eastAsia=\"zh-CN\"/><w:color w:val=\"FF0000\"/></w:rPr><w:t>";
		String right = "</w:t></w:r><w:r><w:rPr><w:rFonts w:hint=\"eastAsia\"/><w:b w:val=\"0\"/><w:bCs w:val=\"0\"/>"
				+ "<w:sz w:val=\"21\"/><w:szCs w:val=\"21\"/><w:highlight w:val=\"none\"/><w:lang w:val=\"en-US\" w:eastAsia=\"zh-CN\"/></w:rPr><w:t>";
		
		Map<String, Object> dataMap = new HashMap<String, Object>();  
        dataMap.put("name", export.m_Target.Title.substring(0, export.m_Target.Title.lastIndexOf('.')));  
        dataMap.put("date", export.m_Target.Date); 
        dataMap.put("database", GlobalData.getSingleton().m_DataBase.m_CurrentDataBase);
        
        //总体结论
        dataMap.put("similarity", String.format("%.2f", export.m_Similarity*100)+"%");  
        
        //相似报告
        List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
        //相似片段
        List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
        int len = export.m_Sample.size();
        
        String text1 = getWord(export.m_Target.Path);
        
        for (int i = 0; i < len; ++i){
        	Map<String, Object> map1 = new HashMap<String, Object>();
        	map1.put("No", i+1);
        	map1.put("Sim", String.format("%.2f", export.m_Sample.get(i).m_Similarity*100)+"%");
        	map1.put("Title", export.m_Sample.get(i).m_Sampledata.Title.substring(0, 
        			export.m_Sample.get(i).m_Sampledata.Title.lastIndexOf('.')));
        	map1.put("Status", export.m_Sample.get(i).m_PictureSimilarity? "是" : "否");
        	map1.put("time", export.m_Sample.get(i).m_Sampledata.Date);
        	list1.add(map1);
        	
        	String text2 = getWord(export.m_Sample.get(i).m_Sampledata.Path);
        	
        	ArrayList<SimilarityParagraph> parlist = export.m_Sample.get(i).m_ParagraphSimilarity;
        	int len2 = export.m_Sample.get(i).m_ParagraphSimilarity.size();
        	for (int j = 0; j < len2; ++j){
        		int[] pos = export.m_Target.ParagraphMsg.get(parlist.get(j).m_TargetParagraph);
    			String tmp1 = text1.substring(pos[0], pos[1]).trim();
    			pos = export.m_Sample.get(i).m_Sampledata.ParagraphMsg.get(parlist.get(j).m_SampleParagraph);
    			String tmp2 = text2.substring(pos[0],pos[1]).trim();
        		Map<String, Object> map2 = new HashMap<String, Object>();
        		
        		for (String s : parlist.get(j).m_SimilarityText){
        			tmp1 = tmp1.replace(s, left+s.trim()+right);
        			tmp2 = tmp2.replace(s, left+s.trim()+right);
        		}
        		
        		map2.put("paragraphtext", tmp1);
        		map2.put("sourcemessage", export.m_Sample.get(i).m_Sampledata.Title+"-"+export.m_Sample.get(i).m_Sampledata.Date);
        		map2.put("sourceparagraphtext", tmp2);
        		list2.add(map2);
        	}
        }
        dataMap.put("table1", list1);
        dataMap.put("table2", list2);
       
        try {
			new DocumentHandler().createDoc(dataMap, path);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
        LogUnit.getSingleton().writeLog("导出"+export.m_Target.Title);
	}
	
}

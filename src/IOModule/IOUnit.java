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
import java.util.Arrays;
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
				String[] value = sp[0].split("-");

				reports[i] = new ReportData();
				reports[i].StuNum = value[0];
				reports[i].StuName = value[1];
				
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
			GlobalData.getSingleton().reInitData();
			GlobalData.getSingleton().m_InputData = new ArrayList<ReportData>(Arrays.asList(reports));
			
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
		
		//对比
//		CompareUnit cp = new CompareUnit();
//		cp.Compare(reports);
	}
}

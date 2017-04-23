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
import jxl.write.biff.RowsExceededException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import TextVectorModule.TextVector;
import BaseUtil.*;
import CompareModule.CompareUnit;
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
		
		ReportData[] reports = new ReportData[name.length];
		Thread[] thread = new Thread[name.length];
		
		try {
			for(int i=0;i<name.length;i++)
			{
				//name 的命名格式为 学号+姓名
				String text = "";
				String AbsolutePath=path+"\\"+name[i];
				in = new FileInputStream(new File(AbsolutePath));
				String[] sp = name[i].split("\\.");
				String[] value = sp[0].split("_");

				reports[i] = new ReportData();
				reports[i].StuNum = value[0];
				reports[i].StuName = value[1];
				
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
				text = text.trim();
				reports[i].WordNum = text.length();
				reports[i].Path = AbsolutePath;
				TextVector tv = new TextVector(text, i, reports);
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
		CompareUnit cp = new CompareUnit();
		cp.Compare(reports);
	
	}
	
	//读取图片
//	public void readImg(String path, String[] name){
//		InputStream  in = null;	
//		for (int i = 0; i < name.length; ++i){
//			String AbsolutePath=path+"\\"+name[i];
//			in = new FileInputStream(new File(AbsolutePath));
//			String[] sp = name[i].split("\\.");
//			if(sp[sp.length-1].equals("doc")){
//				_readImgFromDoc(path, name[i]);
//			}
//			else{
//				_readImgFromDocx(path,name[i]); 
//			}	
//		}
//	}
	
}

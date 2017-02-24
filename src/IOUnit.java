import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;

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


public class IOUnit {

	public String[] readWord(String path,String name[])
	{
		InputStream  in = null;
		String[] text = new String[name.length];

		try {
			for(int i=0;i<name.length;i++)
			{
				String AbsolutePath=path+"\\"+name[i];
				in = new FileInputStream(new File(AbsolutePath));
				String[] sp = name[i].split("\\.");
				
				if (!in.markSupported()) {  
                    in = new PushbackInputStream(in,8);
                }  
				if(sp[sp.length-1].equals("doc")){
					WordExtractor extractor = new WordExtractor(in);
					text[i] = extractor.getText();
				}
				else{
					XWPFDocument document = new XWPFDocument(in);  
		            XWPFWordExtractor extractor =new XWPFWordExtractor(document);  
		            text[i] = extractor.getText();   
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return text;
	}
	
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

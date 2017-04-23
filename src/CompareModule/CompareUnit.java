package CompareModule;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import BaseUtil.ReportData;
import DateBaseModule.DBUnit;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class CompareUnit {
	private double m_Level = 0.5;		//重复率设置
	
	public void Compare(ReportData[] datas){
		//1. 对比pichash
		
		//2. 对比texthash
		ArrayList<ReportData[]> needcheck = ReportsSimilarity(datas);
		//3. 将超出阈值的报告对比paragraphhash，找到相似段落
		System.out.println();
		ParagraphSimilarity(needcheck);
		//4. 文章段落强行比对，找出相似句子。
	}
	
	// 余弦定理求两向量相似度
	public double getSimilarity(Map<String, Integer> vc1,
			Map<String, Integer> vc2) {
		double res = 0.0;
		if (vc1 != null && vc2 != null) {

			Map<String, int[]> AlgorithmMap = new HashMap<String, int[]>();

			Iterator<Map.Entry<String, Integer>> it = vc1.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = it.next();
				String key = entry.getKey();
				int value = entry.getValue();
				int[] values = new int[2];
				if (vc2.get(key) != null) {
					values[0] = value;
					values[1] = vc2.get(key);
				} else {
					values[0] = value;
					values[1] = 0;
				}
				AlgorithmMap.put(key, values);
			}

			it = vc2.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = it.next();
				String key = entry.getKey();
				int value = entry.getValue();
				int[] values = new int[2];
				if (vc1.get(key) == null) {
					values[0] = 0;
					values[1] = value;
					AlgorithmMap.put(key, values);
				}
			}

			Iterator<String> iterator = AlgorithmMap.keySet().iterator();
			double sqdoc1 = 0;
			double sqdoc2 = 0;
			double denominator = 0;
			while (iterator.hasNext()) {
				int[] c = AlgorithmMap.get(iterator.next());
				denominator += c[0] * c[1];
				sqdoc1 += c[0] * c[0];
				sqdoc2 += c[1] * c[1];
			}
			res = denominator / Math.sqrt(sqdoc1 * sqdoc2);
		}
		return res;
	}

	private ArrayList<String> getSameWords(Map<String, Integer> vc1,
			Map<String, Integer> vc2) {
		ArrayList<String> res = new ArrayList<String>();
		Iterator<Map.Entry<String, Integer>> it = vc1.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			String key = entry.getKey();
			int value = entry.getValue();
			if (vc2.get(key) != null) {
				res.add(key);
			}
		}
		return res;
	}
	
	// 获取段落相似程度
	private void ParagraphSimilarity(ArrayList<ReportData[]> reportlist){
		int listlen = reportlist.size();
		for (int i = 0; i < listlen; i++){
			int alen = reportlist.get(i)[0].ParagraphNum;
			int blen = reportlist.get(i)[1].ParagraphNum;
			for (int j = 0; j < alen ; j++){
				ReportData r1 = (ReportData)reportlist.get(i)[0];
				Map<String, Integer> vc1 = r1.toHashMap(r1.ParagraphHash.get(j));
				for (int k = 0;k < blen; k++){
					ReportData r2 = (ReportData)reportlist.get(i)[1];
					Map<String, Integer> vc2 = r2.toHashMap(r2.ParagraphHash.get(k));
					double result = getSimilarity(vc1, vc2);
					if (result > m_Level){
						ArrayList<String> stringlist = getSameWords(vc1, vc2);
						if (stringlist.size() > 3)
							System.out.println(r1.StuName+"-para "+j+" and "+r2.StuName+"-para "+k+":"+stringlist.toString());
					}
				}
			}
		}
	}
	
	// 获取报告的相似程度
	private ArrayList<ReportData[]> ReportsSimilarity(ReportData[] reports){
		int len = reports.length;
		
		ArrayList<ReportData[]> res = new ArrayList<ReportData[]>();
		int flag = 1; 
		for (int i = 0; i < len; i++) {
			flag = 1;
			for (int j = i+1; j < len; j++) {		
				double result = getSimilarity(reports[i].toHashMap(reports[i].TextHash),
						reports[j].toHashMap(reports[j].TextHash));
				if (result > m_Level){  //说明重复率超过标准
					res.add(new ReportData[] {reports[i], reports[j]});
					System.out.println(reports[i].StuNum+reports[i].StuName+" 和  "+reports[j].StuNum+reports[j].StuName+ "的相似度为" + result);
					flag = 0;
				}
			}
//			if (flag == 1) {
//				try {
//					DBUnit db;
//					db = new DBUnit();
//					db.CreateDataBase("ReportesCheck");
//					db.CreateReporteTable();
//
//					db.AddReportItems(reports[i]);
//					db.CreateParagraphTable(reports[i]);
//					db.AddParagraphItems(reports[i]);
//
//					db.CloseDataBase();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		}
		return res;
	}
	
	private void print(ReportData[] datas){
		ReportData r1 = datas[0];
		ReportData r2 = datas[1];
		
		
	}
	
	public Object[] CalculateSimilarityAlgrithm(ReportData[] reports)
			throws IOException, RowsExceededException, WriteException {

		ArrayList TotalNameList = new ArrayList(new ArrayList()), TotalSimilaity = new ArrayList(
				new ArrayList());

		for (int i = 0; i < reports.length; i++) {
			for (int j = i+1; j < reports.length; j++) {
				
				double result = getSimilarity(reports[i].toHashMap(reports[i].TextHash),
						reports[j].toHashMap(reports[j].TextHash));
				System.out.println(reports[i].StuNum+reports[i].StuName+" 和  "+reports[j].StuNum+reports[j].StuName+ "的相似度为" + result);

//				if (result > 0.50) {
//					java.text.NumberFormat percentFormat = java.text.NumberFormat
//							.getPercentInstance();
//					percentFormat.setMaximumFractionDigits(3);
//					String pesimilarity = percentFormat.format(result);
//					System.out.println(pesimilarity);
//
//					TotalNameList.add(name[i]);
//					TotalNameList.add(name[j]);
//					TotalSimilaity.add(pesimilarity);
//				}
			}
		}
		Object[] o = { TotalNameList, TotalSimilaity };
		return o;
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

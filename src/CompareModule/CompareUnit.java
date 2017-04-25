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
import IOModule.IOUnit;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class CompareUnit {
	private double m_Level = 0.65;		//重复率设置
	private ArrayList<ReportData> checkout = null;
	
	public void Compare(ReportData[] datas){
		//1. 对比pichash
		
		//2. 对比texthash
		ArrayList<ReportData[]> needcheck = ReportsSimilarity(datas);
		//3. 将超出阈值的报告对比paragraphhash，找到相似段落
//		ParagraphSimilarity(needcheck);
		//4. 文章段落强行比对，找出相似句子。
		
		//1. 对比上传文件集
			//1.1 对比图片 和 文本， 得到 低于阈值的文本集ReportData[]  与   高于阈值的文本集ArrayList<ReportData[]>
		
		//2. 对比数据库集
			//2.1 将低于阈值的文本集 与 数据库集 进行对比，得到 低于阈值的文本集合ReportData[] 与 高于阈值的文本集ArrayList<ReportData[]>
			//2.2 分别合并ReportData[]  和    ArrayList<ReportData[]>
			//2.3 将 ReportData[] 插入到数据库  
		//3. 输出结果
	}
	
	//根据图片平均相似度和文本相似度求总相似度
	private double getTotalSimilarity(double text, double pic){
		if (pic == 0.0){
			return text;
		}
		return (text+pic)/2;
	}
	
	//获取两个数二进制位数不同的个数
	public int countBitDiff(int m, int n) {  
        int ans = m^n;  
        int count = 0;  
        while(ans != 0){  
            ans &= (ans -1);  
            count++;  
        }  
        return count;  
    } 
	
	//求图片哈希的汉明距离
	private double getHammingDistance(int[] a, int[] b){
		double hamming = 0.0;
		for (int i = 0; i < a.length; ++i){
			int num = countBitDiff(a[i], b[i]);
			hamming += num;
		}
		hamming = hamming / (a.length) / Integer.SIZE;   // 求汉明距离
		return hamming;
	}
	
	//获取图片平均相似度
	private double getPicSimilarity(ReportData rp1, ReportData rp2){
		String s1 = rp1.PicHash;
		String s2 = rp2.PicHash;
		ArrayList<int[]> ph1 = rp1.toPicHashList(s1);
		ArrayList<int[]> ph2 = rp2.toPicHashList(s2);
		
	}
	
	// 余弦定理求两向量相似度
	private double getSimilarity(Map<String, Integer> vc1,
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
		if (vc1 ==null || vc2 == null)
			return null;
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
			ReportData r1 = (ReportData)reportlist.get(i)[0];
			ReportData r2 = (ReportData)reportlist.get(i)[1];
			for (int j = 0; j < alen ; j++){
				Map<String, Integer> vc1 = r1.toHashMap(r1.ParagraphHash.get(j));
				Map<String, Integer> vc2 = null;
				double result = 0.0;
				int pos = 0;
				for (int k = 0;k < blen; k++){
					vc2 = r2.toHashMap(r2.ParagraphHash.get(k));
					double tmp = getSimilarity(vc1, vc2);
					if (tmp > result){
						result = tmp;
						pos = k;
					}
				}
				vc2 = r2.toHashMap(r2.ParagraphHash.get(pos));
				if (result > m_Level){
					ArrayList<String> stringlist = getSameWords(vc1, vc2);
					IOUnit IO = new IOUnit();
					String text1 = IO.getWord(r1.Path);
					text1 = text1.substring(r1.ParagraphMsg.get(j)[0], r1.ParagraphMsg.get(j)[1]).trim();
					String text2 = IO.getWord(r2.Path);
					text2 = text2.substring(r2.ParagraphMsg.get(pos)[0], r2.ParagraphMsg.get(pos)[1]).trim();
					for (String s : stringlist){
						text1 = text1.replaceAll(s, "*");
						text2 = text2.replaceAll(s, "*");
					}
					System.out.println(r1.StuNum+r1.StuName+" Paragraph"+j+":"+ text1);
					System.out.println("and\n"+r2.StuNum+r2.StuName+" Paragraph"+pos+":"+ text2);
					System.out.println("result:"+result +":" +stringlist.toString());
				}
			}
		}
	}
	
	// 获取报告的相似程度
	private ArrayList<ReportData[]> ReportsSimilarity(ReportData[] reports){
		int len = reports.length;
		
		ArrayList<ReportData[]> res = new ArrayList<ReportData[]>();
		for (int i = 0; i < len; i++) {
	
			for (int j = i+1; j < len; j++) {		
				double result = getSimilarity(reports[i].toHashMap(reports[i].TextHash),
						reports[j].toHashMap(reports[j].TextHash));
				if (result > m_Level){  //说明重复率超过标准
					res.add(new ReportData[] {reports[i], reports[j]});
					System.out.println(reports[i].StuNum+reports[i].StuName+" 和  "+reports[j].StuNum+reports[j].StuName+ "的相似度为" + result);
				}
			}
		}
		return res;
	}
	
	private void print(ReportData[] datas){
		ReportData r1 = datas[0];
		ReportData r2 = datas[1];
		
		
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

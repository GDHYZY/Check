package CompareModule;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mysql.fabric.xmlrpc.base.Array;

import BaseUtil.ExportData;
import BaseUtil.GlobalData;
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
	private double m_PicLevel = 0.95;	//图片重复率
	private ArrayList<ReportData> m_Checkout = null;		//查重通过的报告
	private Map<ReportData, ArrayList<ReportData>> m_Nopass = null;
	private ArrayList<ReportData[]> m_Picnopass = null;	//图片有非常相似的两个报告
	
	public CompareUnit() {
		// TODO Auto-generated constructor stub
		m_Checkout = new ArrayList<ReportData>();
		m_Nopass = new HashMap<ReportData, ArrayList<ReportData>>();
		m_Picnopass =  new ArrayList<ReportData[]>();
	}
	
	@SuppressWarnings("null")
	public void Compare(){
		//1. 对比上传文件集
			//1.1 对比图片 和 文本， 得到 低于阈值的文本集ArrayList<ReportData>  与   高于阈值的文本集ArrayList<ReportData[]>
		ArrayList<ReportData> datas = GlobalData.getSingleton().m_InputData;
		if (datas == null || datas.isEmpty()){
			return;
		}
		
		Map<ReportData, ArrayList<ReportData>> needcheck = ReportsSimilarity(datas);
		if (needcheck != null || !needcheck.isEmpty())
			m_Nopass.putAll(needcheck);
		//2. 对比数据库集
			//2.1 将低于阈值的文本集 与 数据库集 进行对比，得到 低于阈值的文本集合ArrayList<ReportData> 与 高于阈值的文本集ArrayList<ReportData[]>
		ReportData[] dbdatas = getDataFromDataBase();
		CompareWithDatabase(datas.toArray(new ReportData[datas.size()]), dbdatas);
	
			//2.3 将 ReportData[] 插入到数据库
//		SaveDatatoDataBase(m_Checkout.toArray(new ReportData[m_Checkout.size()]));
		//3. 处理相似度高于阈值的报告对
		ParagraphSimilarity(needcheck);
	}
	
	// 将文档集与数据库集进行对比
	private void CompareWithDatabase(ReportData[] needcheck, ReportData[] dbdatas){
		if (needcheck == null || dbdatas == null)
			return ;
		int len1 = needcheck.length;
		int len2 = dbdatas.length;
		int[] mp = new int[len1];
		
		for (int i = 0; i < len1; i++) {
			ArrayList<ReportData> tmp = m_Nopass.get(needcheck[i]);
			for (int j = 0; j < len2; j++) {		
				double result = getSimilarity(needcheck[i].toHashMap(needcheck[i].TextHash),
						dbdatas[j].toHashMap(dbdatas[j].TextHash));
				double picresult = getPicSimilarity(needcheck[i], dbdatas[j]);
				if (picresult > m_PicLevel){ //说明有非常相似的图片
					m_Picnopass.add(new ReportData[] {needcheck[i], dbdatas[j]});
					continue;
				}
				
				if (result > m_Level){  //说明重复率超过标准	
					if (tmp == null){
						tmp = new ArrayList<ReportData>();
					}
					tmp.add(dbdatas[j]);
					mp[i] = 1;
					System.out.println(needcheck[i].StuNum+needcheck[i].StuName+" 和  "+dbdatas[j].StuNum+dbdatas[j].StuName+ "的相似度为" + result);
				}
				//排除掉待测集中认为是无需查重的但与数据库集有重复的文档
				if (mp[i]==1 && m_Checkout.contains(needcheck[i])){		
					m_Checkout.remove(needcheck[i]);
				}
			}
			if (tmp != null){
				m_Nopass.put(needcheck[i], tmp);
			}
		}
		for (int j = 0; j < len1 ;++j){
			if(mp[j] == 0 && !m_Checkout.contains(needcheck[j])){
				m_Checkout.add(needcheck[j]);
			}
		}
		return ;
	}
	
	private boolean SaveDatatoDataBase(ReportData[] reports){
		DBUnit db;
		try {
			db = new DBUnit();
			db.CreateandConnectDataBase("reportscheck");
			db.CreateReporteTable();
			for (ReportData rd : reports){
				db.AddReportItems(rd);
				db.CreateParagraphTable(rd);
				db.AddParagraphItems(rd);
			}
			db.CloseDataBase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	//从数据库获取样本
	private ReportData[] getDataFromDataBase(){
		DBUnit db;
		ReportData[] rt = null;
		try {
			db = new DBUnit();
			db.CreateandConnectDataBase("reportscheck");
			rt = db.QueryReports();
			db.CloseDataBase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rt;
	}
	
	//获取两个数二进制位数不同的个数
	private int countBitDiff(int m, int n) {  
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
		int len = a.length -1;
		for (int i = 0; i < len ; ++i){
			int num = countBitDiff(a[i], b[i]);
			hamming += num;
		}
		hamming = hamming /(len * Integer.SIZE - Math.min(a[len], b[len]));   // 求汉明距离
		return hamming;
	}
	
	//获取图片最大相似度
	private double getPicSimilarity(ReportData rp1, ReportData rp2){
		String s1 = rp1.PicHash;
		String s2 = rp2.PicHash;
		ArrayList<int[]> ph1 = rp1.toPicHashList(s1);
		ArrayList<int[]> ph2 = rp2.toPicHashList(s2);
		
		if (ph1 == null || ph2 == null){
			return 0;
		}
		double max = 0.0;
		int len = ph1.size();
		for (int i = 0 ; i < len; ++i){
			int[] a = ph1.get(i);
			for (int j = 0; j < len; ++j){
				int[] b = ph2.get(j);
				double hamming =1 - getHammingDistance(a,b);
				max = max > hamming ? max : hamming;
			}
		}
		return max;
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
					IOUnit IO = new IOUnit();
					String text1 = IO.getWord(r1.Path).substring(r1.ParagraphMsg.get(j)[0], r1.ParagraphMsg.get(j)[1]);
					String text2 = IO.getWord(r2.Path).substring(r2.ParagraphMsg.get(pos)[0], r2.ParagraphMsg.get(pos)[1]);
					ArrayList<String> stringlist = getSameWords(text1, text2);
					if (stringlist == null || stringlist.isEmpty()){
						
					}
				}
			}
		}
	}
	
	// 获取报告的相似程度
	private Map<ReportData, ArrayList<ReportData>> ReportsSimilarity(ArrayList<ReportData> reports){
		if (reports == null || reports.size() <= 0){
			return null;
		}
		int len = reports.size();
		int[] mp = new int[len];
		Map<ReportData, ArrayList<ReportData>> res = new HashMap<ReportData, ArrayList<ReportData>>();
		for (int i = 0; i < len; i++) {
			ReportData r1 = reports.get(i);
			ArrayList<ReportData> sample = new ArrayList<ReportData>();
			for (int j = 0; j < len; j++) {	
				if (i == j)
					continue;
				ReportData r2 = reports.get(j);
				double result = getSimilarity(r1.toHashMap(r1.TextHash),
						r2.toHashMap(r2.TextHash));
				double picresult = getPicSimilarity(reports.get(i), reports.get(j));
				if (picresult > m_PicLevel){ //说明有非常相似的图片
					m_Picnopass.add(new ReportData[] {r1, r2});
					m_Picnopass.add(new ReportData[] {r2, r1});
					continue;
				}
				
				if (result > m_Level){  //说明重复率超过标准
					sample.add(r2);
					mp[i] = mp[j] = 1;
					System.out.println(r1.StuNum+r1.StuName+" 和  "+r2.StuNum+r2.StuName+ "的相似度为" + result);
				}
			}
			res.put(r1, sample);
		}
		for (int j = 0;j < len;j++){
			if(mp[j] == 0){
				m_Checkout.add(reports.get(j));
			}
		}
		
		return res.isEmpty()? null : res;
	}
	
	private ArrayList<String> getSameWords(String str1, String str2){
		int[] res = new int[3];
		ArrayList<String> result = new ArrayList<String>();
		int samenumber = 0;
		while(true){
			res = LongestCommonSubsequence(str1.toCharArray(), str2.toCharArray());
			if (res[2] < 5)	//最长公共子串长度低于5个
			{
				break;
			}
			String s = str1.substring(res[0],res[0]+res[3]);
			str1 = str1.substring(0,res[0]) + str1.substring(res[0]+res[3]);
			str2 = str2.substring(0,res[0]) + str2.substring(res[0]+res[3]);
			samenumber += res[3];
			result.add(s);
		}
		return result.isEmpty() ? null: result;
	}
	
	//求两个文本的最长公共字串 返回值 int[] {第一段的开始， 第二段的开始， 公共长度}
	private int[] LongestCommonSubsequence(char[] str1, char[] str2){
		int size1 = str1.length;
        int size2 = str2.length;
        int[] res = new int[3];
        if (size1 == 0 || size2 == 0) 
        	return res;
 
        int start1 = -1;
        int start2 = -1;
        int longest = 0;
 
        int comparisons = 0;
 
        for (int i = 0; i < size1; ++i)
        {
            int m = i;
            int n = 0;
            int length = 0;
            while (m < size1 && n < size2)
            {
                ++comparisons;
                if (str1[m] != str2[n])
                {
                    length = 0;
                }
                else
                {
                    ++length;
                    if (longest < length)
                    {
                        longest = length;
                        start1 = m - longest + 1;
                        start2 = n - longest + 1;
                    }
                }
                ++m;
                ++n;
            }
        }
 
        for (int j = 1; j < size2; ++j)
        {
            int m = 0;
            int n = j;
            int length = 0;
            while (m < size1 && n < size2)
            {
                ++comparisons;
                if (str1[m] != str2[n])
                {
                    length = 0;
                }
                else
                {
                    ++length;
                    if (longest < length)
                    {
                        longest = length;
                        start1 = m - longest + 1;
                        start2 = n - longest + 1;
                    }
                }
                ++m;
                ++n;
            }
        }
        System.out.printf("from %d of str1 and %d of str2, compared for %d times\n", start1, start2, comparisons);
        res[0] = start1;
        res[1] = start2;
        res[2] = longest;
        return res;
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

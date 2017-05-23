package CompareModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import BaseUtil.ExportData;
import BaseUtil.GlobalData;
import BaseUtil.LogUnit;
import BaseUtil.ReportData;
import BaseUtil.Sample;
import BaseUtil.SimilarityParagraph;
import IOModule.IOUnit;


public class CompareUnit implements Runnable {
	private double m_Level = 0.65;		//重复率设置
	private double m_PicLevel = 0.95;	//图片重复率
	private Map<ReportData, ArrayList<ReportData>> m_Nopass = null;
	private ArrayList<ReportData[]> m_Picnopass = null;	//图片有非常相似的两个报告
	
	private void Clear(){
		m_Nopass = null;
		m_Picnopass = null;
	}
	
	public CompareUnit() {
		// TODO Auto-generated constructor stub
		m_Nopass = new HashMap<ReportData, ArrayList<ReportData>>();
		m_Picnopass =  new ArrayList<ReportData[]>();
	}
	
	@SuppressWarnings("null")
	public void Compare(){
		LogUnit.getSingleton().writeLog("开始检查相似度");
		ArrayList<ReportData> datas = GlobalData.getSingleton().m_InputData;
		if (datas == null || datas.isEmpty()){
			return;
		}
		
		Map<ReportData, ArrayList<ReportData>> needcheck = ReportsSimilarity(datas);
		if (needcheck != null || !needcheck.isEmpty())
			m_Nopass.putAll(needcheck);
		ReportData[] dbdatas = getDataFromDataBase();
		CompareWithDatabase(datas.toArray(new ReportData[datas.size()]), dbdatas);

		ParagraphSimilarity();	
		
		//排序完毕，按照相似度由高到低排序
		Comparator<ExportData> comparator = new Comparator<ExportData>() {
			@Override
			public int compare(ExportData e1, ExportData e2) {
				if (e2.m_Similarity == e1.m_Similarity)
					return 0;
				return e2.m_Similarity > e1.m_Similarity ? 1 : -1;
			}
		};
		Collections.sort(GlobalData.getSingleton().m_ExportData, comparator);
		
		LogUnit.getSingleton().writeLog("检查相似度结束");
		Clear();
	}
	
	// 将文档集与数据库集进行对比
	private void CompareWithDatabase(ReportData[] needcheck, ReportData[] dbdatas){
		if (needcheck == null || dbdatas == null)
			return ;
		int len1 = needcheck.length;
		int len2 = dbdatas.length;
		
		for (int i = 0; i < len1; i++) {
			ArrayList<ReportData> tmp = m_Nopass.get(needcheck[i]);
			for (int j = 0; j < len2; j++) {	
				if (!GlobalData.getSingleton().m_noCheckPicture){
					double picresult = getPicSimilarity(needcheck[i], dbdatas[j]);
					if (picresult > m_PicLevel){ //说明有非常相似的图片
						m_Picnopass.add(new ReportData[] {needcheck[i], dbdatas[j]});
					}					
				}
				
				double result = getSimilarity(needcheck[i].toHashMap(needcheck[i].TextHash),
						dbdatas[j].toHashMap(dbdatas[j].TextHash));
				if (result > m_Level){  //说明重复率超过标准	
					if (tmp == null){
						tmp = new ArrayList<ReportData>();
					}
					tmp.add(dbdatas[j]);
				}
			}
			if (tmp != null){
				m_Nopass.put(needcheck[i], tmp);
			} else {
				LogUnit.getSingleton().writeLog(needcheck[i].Title+"对比完毕");
			}
		}
		return ;
	}
	
	//从数据库获取样本
	private ReportData[] getDataFromDataBase(){
		ReportData[] rt = null;
		try {
			rt = GlobalData.getSingleton().m_DataBase.QueryReports();
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
		int len1 = ph1.size();
		int len2 = ph2.size();
		for (int i = 0 ; i < len1; ++i){
			int[] a = ph1.get(i);
			for (int j = 0; j < len2; ++j){
				int[] b = ph2.get(j);
				double hamming = 1 - getHammingDistance(a,b);
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
			//向量余弦计算相似度
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
	
	private boolean PicContant(ReportData target, ReportData sample){
		if (m_Picnopass == null || m_Picnopass.isEmpty())
			return false;
		int len = m_Picnopass.size();
		for (int i = 0; i < len; ++i){
			if (m_Picnopass.get(i)[0].equals(target)){
				if (m_Picnopass.get(i)[1].equals(sample)){
					return true;
				}
			}
		}
		return false;
	}
	
	// 获取段落相似程度, 并填充输出内容
	private void ParagraphSimilarity(){
		if (m_Nopass == null || m_Nopass.isEmpty())
			return;
		Iterator<Map.Entry<ReportData, ArrayList<ReportData>>> it = m_Nopass.entrySet().iterator();
		GlobalData.getSingleton().m_ExportData.clear();
		while(it.hasNext()){
			Map.Entry<ReportData, ArrayList<ReportData>> entry = it.next();
			ReportData target = entry.getKey();
			ArrayList<ReportData> samples = entry.getValue();

			ExportData export = new ExportData();
			export.m_Target = target;
			
			ArrayList<Sample> samplelist = new ArrayList<Sample>();
			int len = samples.size();
			for (int i = 0 ; i < len; ++i){
				ReportData tmp = samples.get(i);
				Sample sample = new Sample();
				sample.m_Sampledata = tmp;
				if (!GlobalData.getSingleton().m_noCheckPicture && PicContant(target, tmp)){
					sample.m_PictureSimilarity = true;
				}
				ArrayList<SimilarityParagraph> simpara = getParagraphSimilarity(target, tmp);
				ArrayList<Integer> stringposlist = new ArrayList<Integer>();
				if (simpara == null){
					continue;
				}
				sample.m_ParagraphSimilarity = simpara;
				int textwords = 0;
				for (SimilarityParagraph sim: simpara){
					textwords += sim.m_WordNumber;
					ArrayList<Integer> poslist = sim.m_SimilarityposList;
					stringposlist = getSameWordPosArray(stringposlist, poslist);
				}
				
				sample.m_WordNumber = textwords;
				sample.m_Similarity = (double)textwords / target.WordNum;
				sample.m_SimilarityPosList = stringposlist;
				samplelist.add(sample);
			}
			if (!samplelist.isEmpty()){
				export.m_Sample = samplelist;
				ArrayList<Integer> targetSameposList = new ArrayList<Integer>();
				for (Sample _sample : samplelist){
					targetSameposList = getSameWordPosArray(targetSameposList, _sample.m_SimilarityPosList);
				}
				int targetsamelen = targetSameposList.size();
				int wordnumber = 0;
				for (int j = 0; j < targetsamelen - 1; j+=2){
					wordnumber += (targetSameposList.get(j+1) - targetSameposList.get(j));
				}
				export.m_WorNumber = wordnumber;
				export.m_Similarity = (double)wordnumber / target.WordNum;
			}
			GlobalData.getSingleton().m_ExportData.add(export);
			LogUnit.getSingleton().writeLog(target.Title+"对比完毕");
		}
	}
	
	private ArrayList<Integer> getSameWordPosArray(ArrayList<Integer> a, ArrayList<Integer> b){
		if (a == null || a.isEmpty()){
			return new ArrayList<Integer>(b);
		} else if (b == null || b.isEmpty()){
			return new ArrayList<Integer>(a);
		}
		ArrayList<Integer> res = new ArrayList<Integer>();
		int alen = a.size();
		int blen = b.size();
		int pos1 = 0, pos2 = 0;		
		while(pos1 < alen-1 && pos2 < blen-1){
			int a1 = a.get(pos1), a2 = a.get(pos1+1);
			int b1 = b.get(pos2), b2 = b.get(pos2+1);
			if (a1 > b2){
				res.add(b1);
				res.add(b2);
				pos2 += 2;
			} else if (b1 > a2){
				res.add(a1);
				res.add(a2);
				pos1 +=2;
			} else {
				if (a1 > b1 && a2 < b2) {
					pos1 += 2;
				} else if (b1 > a1 && b2 < a2) {
					pos2 += 2;
				} else {
					res.add(Math.min(a1, b1));
					res.add(Math.max(a2, b2));
					pos1 += 2;
					pos2 += 2;
				}
			}
		}
		while(pos1 < alen){res.add(a.get(pos1++));}
		while(pos2 < blen){res.add(b.get(pos2++));}
		
		for (int i = 2; i < res.size(); ){
			if (res.get(i) <= res.get(i-1)){
				res.remove(i);
				res.remove(i-1);
			} else {
				i+=2;
			}
		}
		return res;
	}
	
	
	@SuppressWarnings("null")
	private ArrayList<SimilarityParagraph> getParagraphSimilarity(
			ReportData target, ReportData sample) {
		int targetlen = target.ParagraphNum;
		int samplelen = sample.ParagraphNum;
		ArrayList<SimilarityParagraph> res = new ArrayList<SimilarityParagraph>();

		for (int j = 1; j <= targetlen; j++) {
			SimilarityParagraph simpar = new SimilarityParagraph();

			Map<String, Integer> vc1 = target.toHashMap(target.ParagraphHash
					.get(j));
			Map<String, Integer> vc2 = null;
			double result = 0.0;
			int pos = 0;
			for (int k = 1; k <= samplelen; k++) {
				vc2 = sample.toHashMap(sample.ParagraphHash.get(k));
				double tmp = getSimilarity(vc1, vc2);
				if (tmp > result) {
					result = tmp;
					pos = k;
				}
			}
			vc2 = sample.toHashMap(sample.ParagraphHash.get(pos));

			if (result > m_Level) {
				IOUnit IO = new IOUnit();
				simpar.m_TargetParagraph = j;
				simpar.m_SampleParagraph = pos;

				String text1 = IO.getWord(target.Path).substring(
						target.ParagraphMsg.get(j)[0],
						target.ParagraphMsg.get(j)[1]).trim();
				String text2 = IO.getWord(sample.Path).substring(
						sample.ParagraphMsg.get(pos)[0],
						sample.ParagraphMsg.get(pos)[1]).trim();
				int len1 = text1.length();
				String tmp = new String(text1);
				// 需要获取段落相似词和位置图和总字数
				ArrayList<String> stringlist = new ArrayList<String>();
				ArrayList<Integer> stringposlist = new ArrayList<Integer>();
				int samenumber = 0;
				Map<String,Integer> sametextpos = new HashMap<String, Integer>();
				while(true){
					int[] LCSres = LongestCommonSubsequence(text1, text2);
					if (LCSres[2] < 8)	//连续7个词做为标准  
					{
						break;
					}
					String s = text1.substring(LCSres[0],LCSres[0]+LCSres[2]);
					text1 = text1.substring(0,LCSres[0]) + text1.substring(LCSres[0]+LCSres[2]);
					text2 = text2.substring(0,LCSres[1]) + text2.substring(LCSres[1]+LCSres[2]);
					samenumber += LCSres[2];
					int begin = tmp.indexOf(s);
					int end = begin + LCSres[2];
					if (stringlist.contains(s)){
						int step = sametextpos.get(s);
						begin = tmp.indexOf(s, step+1);
						end = begin + LCSres[2];
					} 
					sametextpos.put(s, begin);
					stringlist.add(s);
					stringposlist.add(target.ParagraphMsg.get(j)[0]+ begin);
					stringposlist.add(target.ParagraphMsg.get(j)[0]+ end);
				}
				Collections.sort(stringposlist);
				if (!stringlist.isEmpty()) {
					simpar.m_SimilarityText.addAll(stringlist);
					simpar.m_WordNumber = samenumber;
					simpar.m_Similarity = (double)samenumber / len1;
					simpar.m_SimilarityText = stringlist;
					simpar.m_SimilarityposList = stringposlist;
					res.add(simpar);
				}
			}
		}
		return res.isEmpty() ? null : res;
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
				if (i == j){
					continue;					
				}
				ReportData r2 = reports.get(j);
				
				//检测图片相似度
				if (! GlobalData.getSingleton().m_noCheckPicture){
					double picresult = getPicSimilarity(reports.get(i), reports.get(j));
					if (picresult > m_PicLevel){ //说明有非常相似的图片
						m_Picnopass.add(new ReportData[] {r1, r2});
						m_Picnopass.add(new ReportData[] {r2, r1});
					}
				}
				
				double result = getSimilarity(r1.toHashMap(r1.TextHash),
						r2.toHashMap(r2.TextHash));
				
				if (result > m_Level){  //说明重复率超过标准
					sample.add(r2);
					mp[i] = mp[j] = 1;
					System.out.println(r1.Title+" 和  "+r2.Title+ "的相似度为" + result);
				}
			}
			res.put(r1, sample);
		}

		return res.isEmpty()? null : res;
	}
	
	//求两个文本的最长公共字串 返回值 int[] {第一段的开始， 第二段的开始， 公共长度} 复杂度O(n)
	private int[] LongestCommonSubsequence(String str1, String str2){
		int size1 = str1.length();
        int size2 = str2.length();
        int[] res = new int[3];
        if (size1 == 0 || size2 == 0) 
        	return res;
        int start1 = -1;
        int start2 = -1;
        int longest = 0;
        
        for (int i = 0; i < size1; ++i)
        {
            int m = i;
            int n = 0;
            int length = 0;
            while (m < size1 && n < size2)
            {
                if (str1.charAt(m) != str2.charAt(n))
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
                if (str1.charAt(m) != str2.charAt(n))
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
        res[0] = start1;
        res[1] = start2;
        res[2] = longest;
        return res;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Compare();
	}
	
}
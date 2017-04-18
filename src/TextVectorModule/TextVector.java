package TextVectorModule;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import BaseUtil.ReporteDate;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class TextVector implements Runnable {
	private int m_Index = 0;
	private int m_Paragraph = 0; //段落
	private int m_Front = 0, m_Last = 0;
	private String m_Sentence = null;
	private String m_Doc = "";
	private ReporteDate m_Reporte = null;
	
	//判断无意义字符
	private boolean isMeanless(char ch){
		return (ch==' ' || ch=='\t' || ch=='\n' || ch=='.' || ch=='。' || ch=='、' || ch==';' ||
				ch==',' || ch=='，' || ch=='\t' || ch=='\r');
	}
	
	//获取GB2312码
//	public static short getGB2312Id(char ch) {
//		try {
//			byte[] buffer = Character.toString(ch).getBytes("GB2312");
//			if (buffer.length != 2) {
//				return -1;
//			}
//			int b0 = (int) (buffer[0] & 0x0FF) - 161; 
//			int b1 = (int) (buffer[1] & 0x0FF) - 161; 
//			return (short) (b0 * 94 + b1);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return -1;
//	}
	
	
	public Object[] CalculateSimilarityAlgrithm(String text[],String name[]) throws IOException,
	RowsExceededException, WriteException {
		
		ArrayList TotalNameList = new ArrayList(new ArrayList()), 
		TotalSimilaity = new ArrayList(new ArrayList());

		for (int i = 0; i < text.length; i++) 
		{
			for (int j = 1; j <text.length; j++) {
				if (i >= j)
					continue;
				double result= getSimilarity(_BuildWordsVector(text[i]), _BuildWordsVector(text[j]));
						System.out.println(text[i] + "和" + text[j] + "的相似度为"
								+ result);

						if (result > 0.50) 
						{
							java.text.NumberFormat percentFormat = java.text.NumberFormat
									.getPercentInstance();
							percentFormat.setMaximumFractionDigits(3);
							String pesimilarity = percentFormat.format(result);
							System.out.println(pesimilarity);

							TotalNameList.add(name[i]);
							TotalNameList.add(name[j]);
							TotalSimilaity.add(pesimilarity);
						}
			}
		}
		Object[] o = { TotalNameList,TotalSimilaity };
		return o;

	}
	
	//合并两个向量
	public static Map<Object, Integer> MergeVector(Map<Object, Integer> vc1,Map<Object, Integer> vc2){
		if(vc1 == null)
			return vc2;
		else if(vc2 == null){
			return vc1;
		}
		else{
			Map<Object,Integer> res = new HashMap<Object, Integer>();
			Iterator<Map.Entry<Object, Integer>> it = vc1.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<Object, Integer> entry = it.next();
				Object key = entry.getKey();
				int value = entry.getValue();
				if(vc2.get(key) != null){
					value += vc2.get(key);
				}
				res.put(key, value);
			}
			it = vc2.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<Object, Integer> entry = it.next();
				Object key = entry.getKey();
				int value = entry.getValue();
				if(vc1.get(key) != null){
					continue;
				}
				res.put(key, value);
			}
			return res;
		}
	}
	
	//余弦定理求两向量相似度
	public static double getSimilarity(Map<Object, Integer> vc1,Map<Object, Integer> vc2){

		double res = 0.0;
		
		if (vc1!=null && vc2!=null) {

			Map<Object, int[]> AlgorithmMap = new HashMap<Object, int[]>();

			Iterator<Map.Entry<Object, Integer>> it = vc1.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<Object, Integer> entry = it.next();
				Object key = entry.getKey();
				int value = entry.getValue();
				int[] values = new int[2];
				if(vc2.get(key)!= null){
					values[0] = value;
					values[1] = vc2.get(key);
				}else{
					values[0] = value;
					values[1] = 0;
				}
				AlgorithmMap.put(key, values);
			}
			
			it = vc2.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<Object, Integer> entry = it.next();
				Object key = entry.getKey();
				int value = entry.getValue();
				int[] values = new int[2];
				if(vc1.get(key) == null){
					values[0] = 0;
					values[1] = value;
					AlgorithmMap.put(key, values);
				}
			}
			
			Iterator<Object> iterator = AlgorithmMap.keySet().iterator();
			double sqdoc1 = 0;
			double sqdoc2 = 0;
			double denominator = 0;	
			while(iterator.hasNext()){
				int[] c = AlgorithmMap.get(iterator.next());
				denominator += c[0]*c[1];
				sqdoc1 += c[0]*c[0];
				sqdoc2 += c[1]*c[1];
			}
			res = denominator / Math.sqrt(sqdoc1*sqdoc2);
		} 
		return res;
	}
	
	//下一段
	private void nextSentence(String text){
		while(!isEndofSentence(text.charAt(m_Last))){
			++m_Last;
		}
	}
	
	//判断句子结束
	private boolean isEndofSentence(char ch){
		return ( ch=='.' || ch=='。' || ch=='、' || ch==';' || ch=='\n');
	}
	
	private Map<Object, Integer> nextParagraph(String text){
		Map<Object, Integer> res = new HashMap<Object, Integer>();
		while(m_Last < text.length() && text.charAt(m_Last) !='\n'){
			nextSentence(text);
			m_Sentence = text.substring(m_Front,m_Last);
			res = MergeVector(res, _BuildWordsVector(m_Sentence));
			m_Front = m_Last+1;
			m_Last+=1;
		}
		m_Last+=1;
		//排除空白行，将每一段的向量保存起来
		m_Paragraph+=1;
		m_Reporte.ParagraphHash.put(m_Paragraph, res.toString());
		m_Reporte.PargraphNum = m_Paragraph;						
		return res;
	}
	
	//建立文档的词频空间向量
	private Map<Object, Integer> _BuildWordsVector(String doc){
		Map<Object, Integer> WordsVector = null;
		if(doc != null && doc.trim().length() > 0){
			WordsVector = new HashMap<Object, Integer>();
			for(int i = 0 ; i < doc.length(); ++i){
				char d = doc.charAt(i);
				if(!isMeanless(d)){
//					int index = getGB2312Id(d);
					Integer iv = WordsVector.get(d);
					if(iv != null){
						iv+=1;
					}else{
						iv = 1;
					}
					WordsVector.put(d, iv);
				}
			}
		}
		return WordsVector;
	}
	
	public TextVector(){
	}
	
	public TextVector(String doc, int index, ReporteDate[] reportes){
		assert(! doc.isEmpty());
		assert(index >= 0);
		assert(reportes != null);
		m_Reporte = reportes[index];
		m_Index = index;
		m_Doc = doc;
		m_Paragraph = 0;
		m_Front = m_Last = 0;
		m_Sentence = "";
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub		
		Map<Object, Integer> res = new HashMap<Object, Integer>();
		while(m_Last < m_Doc.length()){
			res = MergeVector(res, nextParagraph(m_Doc));
		}
		m_Reporte.TextHash = res.toString();
	}
}

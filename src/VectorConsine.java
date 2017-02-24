import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class VectorConsine {
	
	//判断汉字
	public static boolean isChinese(char ch){
		return (ch >= 0x4E00 && ch <= 0x9FA5);
	}
	
	//获取GB2312码
	public static short getGB2312Id(char ch) {
		try {
			byte[] buffer = Character.toString(ch).getBytes("GB2312");
			if (buffer.length != 2) {
				return -1;
			}
			int b0 = (int) (buffer[0] & 0x0FF) - 161; 
			int b1 = (int) (buffer[1] & 0x0FF) - 161; 
			return (short) (b0 * 94 + b1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	//建立文档的词频空间向量
	public static Map<Object, Integer> buildWordsVector(String doc){
		Map<Object, Integer> WordsVector = null;
		if(doc != null && doc.trim().length() > 0){
			WordsVector = new HashMap<Object, Integer>();
			for(int i = 0 ; i < doc.length(); ++i){
				char d = doc.charAt(i);
//				if(isChinese(d)){
//					int index = getGB2312Id(d);
					Integer iv = WordsVector.get(d);
					if(iv != null){
						iv+=1;
					}else{
						iv = 1;
					}
					WordsVector.put(d, iv);
//				}
			}
		}
		System.out.println(WordsVector);
		return WordsVector;
	}
	
	public Object[] CalculateSimilarityAlgrithm(String text[],String name[]) throws IOException,
	RowsExceededException, WriteException {
		
		ArrayList TotalNameList = new ArrayList(new ArrayList()), 
		TotalSimilaity = new ArrayList(new ArrayList());

		for (int i = 0; i < text.length; i++) 
		{
			for (int j = 1; j <text.length; j++) {
				if (i >= j)
					continue;
				double result= getSimilarity(buildWordsVector(text[i]), buildWordsVector(text[j]));
						System.out.println(name[i] + "和" + name[j] + "的相似度为"
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
	
	//余弦定理求两向量相似度
	public static double getSimilarity(Map<Object, Integer> vc1,Map<Object, Integer> vc2){

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
			
//			for(Map.Entry<Object,int[]> e : AlgorithmMap.entrySet()){
//				System.out.println("key："+e.getKey()+"  ["+ e.getValue()[0]+","+e.getValue()[1]+"]");
//			}
			
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
			return denominator / Math.sqrt(sqdoc1*sqdoc2);
		
		} else {
			throw new NullPointerException(
					" the Document is null or have not chars!!");
		}
	}
}

package BaseUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ReportData {
	public int ParagraphNum = 0;
	public int WordNum = 0;
	public String StuNum = "";
	public String StuName = "";
	public String TextHash = "";
	public String PicHash = "";
	public String Path = "";
	public Map<Integer, String> ParagraphHash = null;
	public Map<Integer, int[]> ParagraphMsg = null;
	
	public ReportData(){
		ParagraphHash = new HashMap<Integer, String>();
		ParagraphMsg = new HashMap<Integer, int[]>();
	}
	
	//将String 转成  词-出现次数 的 map ,用于从数据库取出哈希
	public Map<String, Integer> toHashMap(String src){
		if (src == null || src=="{}"){
			return null;
		}
		Map<String, Integer> res = new HashMap<String, Integer>();
		String tmp = src.substring(1, src.length()-1);
		String[] Item = tmp.split(", ");
		for (int i = 0; i < Item.length; ++i){
			int len = Item[i].length();
			int pos = len-1;
			while(Item[i].charAt(pos)!='=') pos--;
			String key = Item[i].substring(0,pos);
			Integer value = Integer.parseInt(Item[i].substring(pos+1, len));
			res.put(key, value);
		}
		return res;
	}
	
	//将图片的哈希String转成list, 用于从数据库取出
	public ArrayList<int[]> toPicHashList(String src){
		if (src == null || src=="[]"){
			return null;
		}
		String hash = src.substring(1, src.length()-1);
		ArrayList<int[]> res = new ArrayList<int[]>();
		String[] pichash = hash.split("\\|");
		int len = pichash.length;
		for (int i = 0; i < len; ++i){
			String picstring = pichash[i].substring(1,pichash[i].length()-1);
			String[] pix = picstring.split(",");
			int pixlen = pix.length;
			int[] pixs = new int[pixlen];
			for (int j = 0; j < pixlen; ++j){
				pixs[j] = Integer.parseInt(pix[j]);
			}
			res.add(pixs);
		}
		return res.isEmpty() ? null : res;
	}
	
	//将图片的哈希list转成string ,用来存入数据库
	public String toPicHashString(ArrayList<int[]> lists){
		if (lists == null){
			return "";
		}
		int len = lists.size();
		String res = "[";		
		for (int i = 0; i < len; ++i){
			int[] piclist = lists.get(i);
			int length = piclist.length;
			res += "[";
			for (int j = 0;j < length; ++j){
				res += piclist[j];					
				if (j+1 < length){
					res +=",";
				}
			}
			res += "]";
			if (i+1 < len){
				res+="|";
			}
		}
		res += "]";
		return res;
	}
}

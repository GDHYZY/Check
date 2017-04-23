package BaseUtil;
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
}

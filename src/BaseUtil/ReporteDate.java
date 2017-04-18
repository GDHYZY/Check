package BaseUtil;
import java.util.HashMap;
import java.util.Map;


public class ReporteDate {
	public int PargraphNum = 0;
	public int WordNum = 0;
	public String StuNum = "";
	public String StuName = "";
	public String TextHash = "";
	public String PicHash = "";
	public Map<Integer, String> ParagraphHash = null;
	
	public ReporteDate(){
		ParagraphHash = new HashMap<Integer, String>();
	}
	
	public Map<Object, Integer> toHashMap(String src){
		Map<Object, Integer> res = new HashMap<Object, Integer>();
		String tmp = src.substring(1, src.length()-1);
		String[] Item = tmp.split(",");
		for (int i = 0; i < Item.length; ++i){
			int pos = Item[i].length()-1;
			int len = pos;
			while(Item[i].charAt(pos)!='=') pos--;
			Object key = Item[i].substring(0,pos-1);
			Integer value = Integer.parseInt(Item[i].substring(pos+1, len));
			res.put(key, value);
		}
		
		return res;
	}
}

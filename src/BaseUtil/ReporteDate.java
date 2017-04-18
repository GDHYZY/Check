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
}

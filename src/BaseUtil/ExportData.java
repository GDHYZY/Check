package BaseUtil;

import java.util.ArrayList;

public class ExportData {

	public ReportData m_Target = null;			//待测报告
	public int m_WorNumber = 0;					//总相似词个数
	public double m_Similarity = 0;				//总相似度
	public ArrayList<Sample> m_Sample = null;	//相似的报告数组
	
	public ExportData(){
		m_Sample = new ArrayList<Sample>();
	}
}

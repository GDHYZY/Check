package BaseUtil;

import java.util.ArrayList;

public class ExportData {

	public ReportData m_Target = null;
	public int m_WorNumber = 0;
	public double m_Similarity = 0;
	public ArrayList<Sample> m_Sample = null;
	
	public ExportData(){
		m_Sample = new ArrayList<Sample>();
	}
}

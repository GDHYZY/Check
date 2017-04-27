package BaseUtil;

import java.util.ArrayList;


//全局参数， 单例类
public class GlobalData {
	private volatile static GlobalData instance; //声明成 volatile
    private GlobalData (){
    	reInitData();
    }    
    public static GlobalData getSingleton() {
        if (instance == null) {                         
            synchronized (GlobalData.class) {
                if (instance == null) {       
                    instance = new GlobalData();
                }
            }
        }
        return instance;
    }
    
    public ArrayList<ReportData> m_InputData = null;
    public ArrayList<ExportData> m_ExportData = null;
    
//	public ArrayList<ReportData> m_Checkout = null;		//查重通过的报告
//	public ArrayList<ReportData[]> m_Nopass = null;		//查出重复的两个报告
//	public ArrayList<ReportData[]> m_Picnopass = null;	//图片有非常相似的两个报告
	
	public void reInitData(){
		m_InputData = null;
		m_InputData = new ArrayList<ReportData>();
		m_ExportData = null;
		m_ExportData = new ArrayList<ExportData>();
		
//		m_Checkout = new ArrayList<ReportData>();
//		m_Nopass = new ArrayList<ReportData[]>();
//		m_Picnopass =  new ArrayList<ReportData[]>();
	}
}

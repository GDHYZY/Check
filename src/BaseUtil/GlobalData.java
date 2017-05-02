package BaseUtil;

import java.util.ArrayList;

import DateBaseModule.DBUnit;


//全局参数， 单例类
public class GlobalData {
	private volatile static GlobalData instance; //声明成 volatile
    private GlobalData (){
    	InitDataBase();	
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
    
    public DBUnit m_DataBase = null;
    public ArrayList<ReportData> m_InputData = null;
    public ArrayList<ExportData> m_ExportData = null;
    
    private void InitDataBase(){
    	m_DataBase = null;
    	try {
			m_DataBase = new DBUnit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	m_DataBase.InitDataBases();
    }
	
	public void reInitData(){
		m_InputData = null;
		m_InputData = new ArrayList<ReportData>();
		m_ExportData = null;
		m_ExportData = new ArrayList<ExportData>();
	}
	
	public Object[][] getCheckList(){
		Object[][] object = new Object[m_InputData.size()][5];
		for (int i = 0; i < m_InputData.size(); ++i){
			object[i][0] = m_InputData.get(i).Title;
			object[i][1] = m_InputData.get(i).ParagraphNum;
			object[i][2] = m_InputData.get(i).WordNum;
			if (m_ExportData == null || m_ExportData.isEmpty()){
				object[i][3] = "";
			} else {
				object[i][3] = m_ExportData.get(i).m_Similarity;				
			}
			object[i][4] = m_InputData.get(i).Date;
		}
		return object;
	}
	
	//获取数据库列表
	public Object[][] getDataBaseList(){
		m_DataBase.InitDataBases();
		ArrayList<String> dblist = m_DataBase.QueryDataBase();
		if (dblist == null)
			return (new Object[0][1]);
		Object[][] object = new Object[dblist.size()][1];
		for (int i = 0; i < dblist.size(); ++i){
			object[i][0] = dblist.get(i);
		}
		return object;
	}
	
	//获取数据库内容列表
	public Object[][] getDataBaseContent(){
		Object[][] object = new Object[m_InputData.size()][5];
		for (int i = 0; i < m_InputData.size(); ++i){
			object[i][0] = m_InputData.get(i).Title;
			object[i][1] = m_InputData.get(i).ParagraphNum;
			object[i][2] = m_InputData.get(i).WordNum;
			if (m_ExportData == null || m_ExportData.isEmpty()){
				object[i][3] = "";
			} else {
				object[i][3] = m_ExportData.get(i).m_Similarity;
			}
			object[i][4] = m_InputData.get(i).Date;
		}
		return object;
	}
}

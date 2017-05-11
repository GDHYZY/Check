package BaseUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    public boolean m_noCheckPicture = true;
    
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
		if (m_ExportData == null || m_ExportData.isEmpty()){
			for (int i = 0; i < m_InputData.size(); ++i){
				object[i][0] = m_InputData.get(i).Title;
				object[i][1] = m_InputData.get(i).ParagraphNum;
				object[i][2] = m_InputData.get(i).WordNum;
				object[i][3] = "";
				object[i][4] = m_InputData.get(i).Date;
			}		
		} else {
			for (int i = 0; i < m_ExportData.size(); ++i){
				object[i][0] = m_ExportData.get(i).m_Target.Title;
				object[i][1] = m_ExportData.get(i).m_Target.ParagraphNum;
				object[i][2] = m_ExportData.get(i).m_Target.WordNum;
				object[i][3] = ""+ String.format("%.2f", m_ExportData.get(i).m_Similarity*100) + "%";				
				object[i][4] = m_ExportData.get(i).m_Target.Date;
			}
		}
		
		return object;
	}
	
	//获取数据库列表
	public Object[][] getDataBaseList(){
		String oldname = m_DataBase.m_CurrentDataBase;
		m_DataBase.InitDataBases();
		ArrayList<String> dblist = m_DataBase.QueryDataBase();
		if (dblist == null)
			return (new Object[0][1]);
		Object[][] object = new Object[dblist.size()][1];
		for (int i = 0; i < dblist.size(); ++i){
			object[i][0] = dblist.get(i);
		}
		m_DataBase.CreateandConnectDataBase(oldname);
		return object;
	}
	
	//获取数据库内容列表
	public Object[][] getDataBaseContent(){
		ReportData[] tmp = m_DataBase.QueryReports();
		if (tmp == null || tmp.length <= 0){
			return new Object[0][4];
		}
		Object[][] object = new Object[tmp.length][4];
		
		for (int i = 0; i < tmp.length; ++i){
			object[i][0] = tmp[i].Title;
			object[i][1] = tmp[i].ParagraphNum;
			object[i][2] = tmp[i].WordNum;
			object[i][3] = tmp[i].Date;
		}
		return object;
	}
	
	//将报告消息存入到数据库
	public void saveIntoDataBase(){
		if (m_ExportData == null || m_ExportData.isEmpty())
			return;
		
		for (ExportData export : m_ExportData){
			if (export.m_Similarity == 0){
				m_DataBase.SavetoDataBase(export.m_Target);
			}
		}
	}

}

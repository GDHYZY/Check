package BaseUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LogUnit {
	private volatile static LogUnit instance; //声明成 volatile
	private ArrayList<String> m_Log = null;
	private int m_LogNumber = 0;
	
    private LogUnit (){
    	m_Log = null;
    	m_Log = new ArrayList<String>();
    }    
    
    public static LogUnit getSingleton() {
        if (instance == null) {                         
            synchronized (GlobalData.class) {
                if (instance == null) {       
                    instance = new LogUnit();
                }
            }
        }
        return instance;
    }
    public int getLogNumber(){
    	return m_LogNumber;
    }
	
	//写入日志
	public void writeLog(String s){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		m_Log.add(s + "..." + df.format(new Date()));
		m_LogNumber+=1;
	}
	
	//读取日志
	public ArrayList<String> readLog(){
		ArrayList<String> res = new ArrayList<String>(m_Log);
		m_Log.clear();
		return res;
	}
}

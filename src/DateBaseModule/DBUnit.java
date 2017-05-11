package DateBaseModule;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import BaseUtil.GlobalData;
import BaseUtil.LogUnit;
import BaseUtil.ReportData;



public class DBUnit {
	private String url="jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=utf-8"; //JDBC的URL 
	private String m_report = "Reports";					//报告表
	private String m_DataBase = "ReportsDataBase";			//管理数据库的名字
	public String m_CurrentDataBase = ""; 					//当前数据库的名字

	Connection m_connection;
	Statement m_statement;
	
	public DBUnit() throws Exception{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("成功加载MySQL驱动！");
            
			m_connection = DriverManager.getConnection(url, "root", "");

			InitDataBases();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("连接到数据库失败！");
			e.printStackTrace();
		} 
	}
	
	// 管理数据库的数据库
	// 初始化数据库
	public void InitDataBases() {
		try {
			m_CurrentDataBase = m_DataBase;
			
			m_statement = m_connection.createStatement();
			String sentence = "Create database if not exists " + m_DataBase
					+ " default character set=utf8;";
			m_statement.executeUpdate(sentence);

			m_statement.close();
			m_connection.close();

			url = "jdbc:mysql://localhost:3306/" + m_DataBase
					+ "?useUnicode=true&characterEncoding=utf-8";
			m_connection = DriverManager.getConnection(url, "root", "");
			m_statement = m_connection.createStatement();
			
			//创建表
			sentence = "Create table if not exists `" + m_DataBase + "`(name VARCHAR(30) primary key)";
			m_statement.executeUpdate(sentence);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 创建一个数据库
	public void CreateOneDataBase(String name){
		InitDataBases();
		try {
			String sentence = "select * from " + m_DataBase + " where name = '"
					+ name + "';";
			ResultSet rs = m_statement.executeQuery(sentence);
			if (!rs.next()) {
				sentence = "Insert into " + m_DataBase + " value('" + name
						+ "');";
				m_statement.executeUpdate(sentence);
				LogUnit.getSingleton().writeLog("创建数据库"+name);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//查看数据库
	public ArrayList<String> QueryDataBase(){
		ArrayList<String> res = new ArrayList<String>();
		String sentence = "select * from " + m_DataBase + ";";
		ResultSet rs;
		try {
			rs = m_statement.executeQuery(sentence);
			while(rs.next()){
				String s = rs.getString("name");
				res.add(s);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res.isEmpty()? null : res;
	}
	//删除数据库
	public void DeleteOneDataBase(String name){
		String sentence = "delete from " + m_DataBase + " where name='" +name+ "';";
		try {
			m_statement.executeUpdate(sentence);
			sentence =  "drop database if exists `" + name + "`;";
			m_statement.executeUpdate(sentence);
			
			LogUnit.getSingleton().writeLog("删除数据库"+name);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//清空数据库
	public void ClearDataBase(){
		String sentence = "select * from " + m_DataBase +";";
		ResultSet rs = null;
		try {
			rs = m_statement.executeQuery(sentence);
			ArrayList<String> names = new ArrayList<String>();
			while(rs.next()){
				String name = rs.getString("name");
				names.add(name);
			}
			rs.close();
			for (String s : names){
				DeleteOneDataBase(s);				
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	//具体数据库 
	//判断是否存在报告表的项
	private boolean ExistReportItem(ReportData reporte){
		String sentence = "select * from " + m_report + " where title='" + reporte.Title + "';";
		try {
			ResultSet rs = m_statement.executeQuery(sentence);
			return !rs.wasNull();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	//判断是否存在报告的段落数据库
	private boolean ExistParagraphTable(ReportData reporte){
		String sentence = "show tables like \"" + reporte.Title +"\";";
		try {
			ResultSet rs = m_statement.executeQuery(sentence);
			if (rs.next()){
				return true;
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	//创建数据库
	public boolean CreateandConnectDataBase(String name){
		try {
			m_statement = m_connection.createStatement();
			String sentence = "Create database if not exists `" + name + "` default character set=utf8;";
			m_statement.executeUpdate(sentence);
			
			m_statement.close();
			m_connection.close();
			
			url = "jdbc:mysql://localhost:3306/" + name + "?useUnicode=true&characterEncoding=utf-8";
			m_connection = DriverManager.getConnection(url,"root","");
			m_statement = m_connection.createStatement();
			m_CurrentDataBase = name;
			
			LogUnit.getSingleton().writeLog("连接到数据库"+name);
			
			CreateReporteTable();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //创建Statement对象
		
		return true;
	}
	/* 创建Reportes表
	 * Title  paragraphnum wordnumber path date texthash pichash 
	 */
	public boolean CreateReporteTable(){
		try {
			String sentence = "create table if not exists `" + m_report + 
					"` (title VARCHAR(50) primary key ,paragraphnum int,"
					+ "wordnumber int,path TEXT, date TEXT, texthash TEXT,pichash TEXT)";
			m_statement.executeUpdate(sentence);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	
	/*
	 * 创建报告段落表,表名字为学号+姓名
	 * Number,Begin, End, Hash
	 */
	public boolean CreateParagraphTable(ReportData reporte){
		String sentence;
		try {
			if (ExistParagraphTable(reporte)){
				//如果存在这个表，先删除
				sentence = "drop tables `" + reporte.Title + "`;";
				m_statement.executeUpdate(sentence);
			}
			sentence = "create table if not exists `" + reporte.Title + 
				"` (Number int primary key, Begin int, End int, Hash TEXT)";
			m_statement.executeUpdate(sentence);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	//插入报告
	public boolean AddReportItems(ReportData reporte){
		String sentence;
		try {
			if (ExistReportItem(reporte)){
				//如果存在这项，先删除再写入新的
				sentence = "delete from "+m_report+" where title='"+reporte.Title+"';";
				m_statement.executeUpdate(sentence);
			}
			sentence = "insert into "+m_report+" values('"+reporte.Title+"','" + reporte.ParagraphNum+"', '"
			+reporte.WordNum+"', '"+ reporte.Path.replaceAll("\\\\","\\\\\\\\") + "', '"+ reporte.Date
					+ "', '" +reporte.TextHash+"', '"+ reporte.PicHash+"')";
			m_statement.executeUpdate(sentence);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	//输入学号，段落号，段落hash
	public boolean AddParagraphItems(ReportData reporte){
		try {
			Iterator<Map.Entry<Integer, String>> it = reporte.ParagraphHash.entrySet().iterator();
			Iterator<Map.Entry<Integer, int[]>> msg = reporte.ParagraphMsg.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<Integer, String> entry = it.next();
				Map.Entry<Integer, int[]> entry2 = msg.next();
				int key = entry.getKey();
				String value = entry.getValue();
				int begin = entry2.getValue()[0];
				int end = entry2.getValue()[1];
				String sentence = "insert into `"+reporte.Title +"` values("+key+", "
				+ begin +", " + end +", '"+value+ "')";
				m_statement.executeUpdate(sentence);			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void DeleteReportItem(String title){
		String sentence = "delete from " + m_report + " where title='" +title+ "';";
		try {
			m_statement.executeUpdate(sentence);
			sentence =  "drop tables `" + title + "`;";
			m_statement.executeUpdate(sentence);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//取出所有的报告  title paragraphnum wordnumber path  date texthash pichash  Number,Begin, End, Hash
	public ReportData[] QueryReports(){
		ArrayList<ReportData> reports = new ArrayList<ReportData>();
		String sentence =  "select * from "+ m_report + ";";
		ResultSet resultset = null;
		try {
			resultset = m_statement.executeQuery(sentence);
			while (resultset.next()){
				ReportData res = new ReportData();
				res.Title = resultset.getString("title");
				res.ParagraphNum = resultset.getInt("paragraphnum");
				res.WordNum = resultset.getInt("wordnumber");
				res.Path = resultset.getString("path");
				res.Date = resultset.getString("date");
				res.TextHash = resultset.getString("texthash");
				res.PicHash = resultset.getString("pichash");
				reports.add(res);
			}
			resultset.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < reports.size(); ++i){
			if (ExistParagraphTable(reports.get(i))){
				resultset = _QueryParagraphHash(reports.get(i));
				try {
					while (resultset.next()){
						reports.get(i).ParagraphHash.put(resultset.getInt("Number"), resultset.getString("Hash"));
						reports.get(i).ParagraphMsg.put(resultset.getInt("Number"), 
								new int[] {resultset.getInt("Begin"), resultset.getInt("End")});
					}
					resultset.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		if (reports.size() <=0 )
			return null;
		ReportData[] ret = new ReportData[reports.size()];
		reports.toArray(ret);
		return ret;
	}
	

	
	private ResultSet _QueryParagraphHash(ReportData rd){
		String sentence = "select * from `"+ rd.Title + "` ;";
		ResultSet result = null;
		try {
			result = m_statement.executeQuery(sentence);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	//保存数据
	public void SavetoDataBase(ReportData data){
		AddReportItems(data);
		CreateParagraphTable(data);
		AddParagraphItems(data);
		LogUnit.getSingleton().writeLog(data.Title+"已经存入数据库");
	}
	
	
	//关闭数据库
	public void CloseDataBase(){
        try {
			m_statement.close();
			m_connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

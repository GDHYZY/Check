package DateBaseModule;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mysql.jdbc.ResultSetMetaData;

import BaseUtil.ReportData;
import BaseUtil.ReportData;



public class DBUnit {
	String checkTable="show tables like \"userbrandtime\"";  
	String url="jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=utf-8"; //JDBC的URL 
	String m_report = "Reports";

	Connection m_connection;
	Statement m_statement;
	
	public DBUnit() throws Exception{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("成功加载MySQL驱动！");
            
			m_connection = DriverManager.getConnection(url, "root", "");

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("连接到数据库失败！");
			e.printStackTrace();
		} 
	}
	//判断是否存在报告表的项
	private boolean ExistReportItem(ReportData reporte){
		String sentence = "select * from " + m_report + " where id='" + reporte.StuNum + "';";
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
		String sentence = "show tables like \"" + reporte.StuNum + reporte.StuName +"\";";
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
	public boolean CreateDataBase(String name){
		try {
			m_statement = m_connection.createStatement();
			String sentence = "Create database if not exists " + name + " default character set=utf8;";
			m_statement.executeUpdate(sentence);
			
			m_statement.close();
			m_connection.close();
			
			url = "jdbc:mysql://localhost:3306/" + name + "?useUnicode=true&characterEncoding=utf-8";
			m_connection = DriverManager.getConnection(url,"root","");
			m_statement = m_connection.createStatement();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //创建Statement对象
		
		return true;
	}
	/* 创建Reportes表
	 * id  name  paragraphnum wordnumber path texthash pichash 
	 */
	public boolean CreateReporteTable(){
		try {
			String sentence = "create table if not exists " + m_report + 
					" (id varchar(11) primary key,name varchar(10) not null,paragraphnum int,"
					+ "wordnumber int,path TEXT,texthash TEXT,pichash TEXT)";
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
				sentence = "drop tables " + reporte.StuNum + reporte.StuName + ";";
				m_statement.executeUpdate(sentence);
			}
			sentence = "create table if not exists " + reporte.StuNum + reporte.StuName + 
				" (Number int primary key,Begin int,End int,Hash TEXT)";
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
				sentence = "delete from "+m_report+" where id='"+reporte.StuNum+"';";
				m_statement.executeUpdate(sentence);
			}
			System.out.println(reporte.Path);
			sentence = "insert into "+m_report+" values('"+reporte.StuNum+"','" + reporte.StuName+"', '"
					+reporte.ParagraphNum+"', '"+reporte.WordNum+"', '"+ reporte.Path + "', '"
					+reporte.TextHash+"', '"+ reporte.PicHash+"')";
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
				String sentence = "insert into "+reporte.StuNum+reporte.StuName+" values("+key+", "
				+ begin +", " + end +", '"+value+ "')";
				m_statement.executeUpdate(sentence);			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private ResultSet _QueryReports(String id){
		String sentence = "select * from "+ m_report + " where id='" + id +"';";
		ResultSet result = null;
		try {
			result = m_statement.executeQuery(sentence);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	//查询数据
	//通过学号获取报告hash
	public ReportData QueryReports(String id){
		ReportData res = new ReportData();
		ResultSet result = _QueryReports(id);
		try {
			while (result.next())
			{
				res.StuNum = result.getString(0);
				res.StuName = result.getString(1);
				res.ParagraphNum = result.getInt(2);
				res.WordNum = result.getInt(3);
				res.Path = result.getString(4);
				res.TextHash = result.getString(5);
				res.PicHash = result.getString(6);
			}
			result.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (ExistParagraphTable(res)){
			result = _QueryParagraphHash(res);
			try {
				while (result.next()){
					res.ParagraphHash.put(result.getInt(0), result.getString(4));
					res.ParagraphMsg.put(result.getInt(0), new int[] {result.getInt(2), result.getInt(3)});
				}
				result.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return res;
	}
	
	private ResultSet _QueryParagraphHash(ReportData rd){
		String sentence = "select * from "+ rd.StuNum + rd.StuName+ " ;";
		ResultSet result = null;
		try {
			result = m_statement.executeQuery(sentence);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
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

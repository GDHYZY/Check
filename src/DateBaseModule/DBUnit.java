package DateBaseModule;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

import BaseUtil.ReporteDate;



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
	 * id  name  paragraphnum wordnumber hash
	 */
	public boolean CreateReporteTable(){
		try {
			String sentence = "create table if not exists " + m_report + 
					" (id varchar(11) primary key,name varchar(10) not null,paragraphnum int,"
					+ "wordnumber int,hash TEXT)";
			m_statement.executeUpdate(sentence);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/*
	 * 创建报告段落表,表名字为学号+姓名
	 * Number, hash
	 */
	public boolean CreateParagraphTable(ReporteDate reporte){
		String sentence;
		try {
			if (ExistParagraphTable(reporte)){
				//如果存在这个表，先删除
				sentence = "drop tables " + reporte.StuNum + reporte.StuName + ";";
				m_statement.executeUpdate(sentence);
			}
			sentence = "create table if not exists " + reporte.StuNum + reporte.StuName + 
				" (Number int primary key, hash TEXT)";
			m_statement.executeUpdate(sentence);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	//判断是否存在报告表的项
	private boolean ExistReportItem(ReporteDate reporte){
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
	private boolean ExistParagraphTable(ReporteDate reporte){
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
	
	//插入报告
	public boolean AddReportItems(ReporteDate reporte){
		String sentence;
		try {
			if (ExistReportItem(reporte)){
				//如果存在这项，先删除再写入新的
				sentence = "delete from "+m_report+" where id='"+reporte.StuNum+"';";
				m_statement.executeUpdate(sentence);
			}
			sentence = "insert into "+m_report+" values('"+reporte.StuNum+"','" + reporte.StuName+"', '"
					+reporte.PargraphNum+"', '"+reporte.WordNum+"', '"+reporte.TextHash+"')";
			m_statement.executeUpdate(sentence);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	//输入学号，段落号，段落hash
	public boolean AddParagraphItems(ReporteDate reporte){
		try {
			Iterator<Map.Entry<Integer, String>> it = reporte.ParagraphHash.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<Integer, String> entry = it.next();
				int key = entry.getKey();
				String value = entry.getValue();
				String sentence = "insert into "+reporte.StuNum+reporte.StuName+" values("+key+", '"+value+ "')";
				m_statement.executeUpdate(sentence);			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	//查询数据
	//通过学号获取报告hash
	public String QueryReports(String id){
		String res = "";
		String sentence = "select hash from "+ m_report + " where id=" + id +";";
		try {
			ResultSet result = m_statement.executeQuery(sentence);
			while (result.next())
			{
			    res = result.getString(0);
			}
			result.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	public String QueryParagraphHash(String id, int number){
		String res = "";
		String sentence = "select hash from "+ m_report + " where id=" + id +" and number=" + number + ";";
		ResultSet result;
		try {
			result = m_statement.executeQuery(sentence);
			while (result.next())
			{
				res = result.getString(0);
			}
			result.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}
	
	
	//添加数据
	
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
	//Delete
	
	//Update
}

package DateBaseModule;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
	public boolean CreateandConnectDataBase(String name){
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
			sentence = "insert into "+m_report+" values('"+reporte.StuNum+"','" + reporte.StuName+"', '"
					+reporte.ParagraphNum+"', '"+reporte.WordNum+"', '"+ reporte.Path.replaceAll("\\\\","\\\\\\\\") 
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
	//取出所有的报告  id  name  paragraphnum wordnumber path texthash pichash  Number,Begin, End, Hash
	public ReportData[] QueryReports(){
		ArrayList<ReportData> reports = new ArrayList<ReportData>();
		String sentence =  "select * from "+ m_report + ";";
		ResultSet resultset = null;
		try {
			resultset = m_statement.executeQuery(sentence);
			while (resultset.next()){
				ReportData res = new ReportData();
				res.StuNum = resultset.getString("id");
				res.StuName = resultset.getString("name");
				res.ParagraphNum = resultset.getInt("paragraphnum");
				res.WordNum = resultset.getInt("wordnumber");
				res.Path = resultset.getString("path");
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
	
	//查询数据
	//通过学号获取报告hash
	public ReportData QueryOneReports(String id){
		ReportData res = new ReportData();
		ResultSet result = _QueryReports(id);
		try {
			while (result.next())
			{
				res.StuNum = result.getString("id");
				res.StuName = result.getString("name");
				res.ParagraphNum = result.getInt("paragraphnum");
				res.WordNum = result.getInt("wordnumber");
				res.Path = result.getString("path");
				res.TextHash = result.getString("texthash");
				res.PicHash = result.getString("pichash");
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
					res.ParagraphHash.put(result.getInt("Number"), result.getString("Hash"));
					res.ParagraphMsg.put(result.getInt("Number"), 
							new int[] {result.getInt("Begin"), result.getInt("End")});
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class DBUnit {
	String checkTable="show tables like \"userbrandtime\"";  
	
	public DBUnit() throws Exception{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("成功加载MySQL驱动！");
            
            String url="jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=utf-8"; //JDBC的URL    
            Connection conn;

            conn = DriverManager.getConnection(url, "root", "");
            
            Statement stmt = conn.createStatement(); //创建Statement对象
            
            stmt.executeUpdate("Create database hello default character set=utf8;");
            
            stmt.close();
            conn.close();
            url = "jdbc:mysql://localhost:3306/hello?useUnicode=true&characterEncoding=utf-8";
            conn = DriverManager.getConnection(url,"root","");
            stmt = conn.createStatement();
            
          //创建表test
            stmt.executeUpdate("create table test(id int primary key, name varchar(80))");
             
            //添加数据
            stmt.executeUpdate("insert into test values(1, '张三')");
            stmt.executeUpdate("insert into test values(2, '李四')");
             
            //查询数据
            ResultSet result = stmt.executeQuery("select * from test");

            while (result.next())
            {
                System.out.println(result.getInt("id") + " " + result.getString("name"));
            }
             
            //关闭数据库
            result.close();
            stmt.close();
            conn.close();
            
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("连接到数据库失败！");
			e.printStackTrace();
		} 
	}
	//ConnectDB
	
	//BuildDB
	
	//BuildTable
	
	//AddItem
	
	//Delete
}

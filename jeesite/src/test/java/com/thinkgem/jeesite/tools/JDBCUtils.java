package com.thinkgem.jeesite.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

public class JDBCUtils {
	public static String url = "jdbc:oracle:thin:@192.168.1.120:1521:A2S";
	public static String user = "thinkgem";
	public static String password = "admin";
	public static Connection conn;
	public static Statement st;
	public static ResultSet rs;
	static{
		System.out.println("****JDBC工具类*****");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url,user,password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String userNmae = "Zhangliuna";
		String sql = "select password from sys_user where login_name = ?";
		 try {
			java.sql.PreparedStatement psd= conn.prepareStatement(sql);
			psd.setString(1, userNmae);
			rs = psd.executeQuery();
			while(rs.next()){
				System.out.println("***");
				System.out.println(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static ResultSet query(String sql){
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static void update(String sql) throws SQLException{
		Statement st = conn.createStatement();
		st.executeUpdate(sql);
		st.close();
	}
	
	public static void close(){
		try {
			rs.close();
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
}

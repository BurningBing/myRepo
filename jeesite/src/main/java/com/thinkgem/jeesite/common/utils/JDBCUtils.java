package com.thinkgem.jeesite.common.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {
	public static String url = "jdbc:oracle:thin:@111.227.212.6:1521:FCMS";
	public static String user = "ats";
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
	
	public static ResultSet queryByParameters(String sql,String...parameters){
		try {
			System.out.println(conn.isClosed());
			PreparedStatement pst = conn.prepareStatement(sql);
			for(int i=0;i<parameters.length;i++){
				pst.setString(i+1, parameters[i]);
			}
			rs = pst.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	
	
}

package servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DailyEmails {
	
	static String url = "jdbc:mysql://localhost:3306/LighthouseDB"; 
	static String username = "root";
	static String password = "password";
    
    public static void sendDailyEmails()  {
    	System.out.println("Connecting to database...");
    	ResultSet rs = null;
    	Connection conn = null;
    	PreparedStatement statement = null;
	    try {
	    	conn = DriverManager.getConnection(url, username, password);
	    	System.out.println("Database connected.");
	        statement = conn.prepareStatement("");  // TODO enter query
	        rs = statement.executeQuery();
	    } catch (SQLException e) {
	        throw new IllegalStateException("Cannot connect to database.", e);
	    } finally {
	        try { statement.close(); } catch (Exception e) { /* ignored */ }
	        try { conn.close(); } catch (Exception e) { /* ignored */ }
	        try { rs.close(); } catch (Exception e) { /* ignored */ }
	    }
    }
	

}

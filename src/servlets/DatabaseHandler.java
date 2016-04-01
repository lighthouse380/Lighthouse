package servlets;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DatabaseHandler {
	
	static String url = "jdbc:mysql://localhost:3306/LighthouseDB"; 
	static String username = "root";
	static String password = "password";
    
	
	
	
	public static void printUsers() {
    	System.out.println("Connecting to database...");
    	ResultSet rs = null;
    	Connection conn = null;
    	PreparedStatement statement = null;
	    try {
	    	conn = DriverManager.getConnection(url, username, password);
	    	System.out.println("Database connected.");
	        statement = conn.prepareStatement("SELECT * from User");
	        rs = statement.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getString("email") + " " + rs.getString("user_id"));
			}
	    } catch (SQLException e) {
	        throw new IllegalStateException("Cannot connect to database.", e);
	    } 
	}
    
    public static void addUser(String userEmail)  {
    	System.out.println("Connecting to database...");
    	ResultSet rs = null;
    	Connection conn = null;
    	PreparedStatement statement = null;
	    try {
	    	conn = DriverManager.getConnection(url, username, password);
	    	System.out.println("Database connected.");
	        statement = conn.prepareStatement("call sp_add_user(?);");
	        statement.setString(1, userEmail);
	        rs = statement.executeQuery();
	    } catch (SQLException e) {
	        throw new IllegalStateException("Cannot connect to database.", e);
	    } 
    }
    
    private static String convertDate(Date javaDate) {
    	java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String mySQLdate = sdf.format(javaDate);
    	return mySQLdate;
    }

    public static boolean checkSubscription(Movie movie, String userEmail) throws SQLException{
    	boolean subscribed = false;
    	System.out.println("Connecting to database...");
    	ResultSet rs = null;
    	Connection conn = null;
    	PreparedStatement statement = null;
	    try {
	    	conn = DriverManager.getConnection(url, username, password);
	    	System.out.println("Database connected.");
//	    	System.out.println("Java date:" + movie.getReleaseDate());
	    	String convertedDate = convertDate(movie.getReleaseDate());
//	    	System.out.println("MySQL date:" + convertedDate);
	        statement = conn.prepareStatement("SELECT fn_check_subscription(?, ?, ?);");
	        statement.setString(1, userEmail);
	        statement.setString(2, movie.getTitle());
	        statement.setString(3, convertedDate);
	        rs = statement.executeQuery();
	        if (rs.next()) {
	        	if (rs.getBoolean(1)) {
	        		subscribed = true;
	        		//System.out.println("Subscribed");
	        	} else {
	        		//System.out.println("Not subscribed");
	        	}
		    } else {
		    	System.out.println("Subscription information not found.");
		    }
	    } catch (SQLException e) {
	        throw new IllegalStateException("Cannot connect to database.", e);
	    } 
    	return subscribed;
    }
	
	public static void addSubscription(Movie movie, String userEmail) {	
//    	System.out.println("Connecting to database...");
	    try (Connection conn = DriverManager.getConnection(url, username, password)) {		    	
//	    	System.out.println("Database connected.");
	    	String convertedDate = convertDate(movie.getReleaseDate());
	        PreparedStatement statement = conn.prepareStatement("call sp_add_subscription(?, ?, ?);");
	        statement.setString(1, userEmail);
	        statement.setString(2, movie.getTitle());
	        statement.setString(3, convertedDate);   
	        ResultSet rs = statement.executeQuery();
	        rs = statement.executeQuery("SELECT * from Subscription");
		    while (rs.next()) {
		    	System.out.print(rs.getString("movie_id") + "   ");
		    	System.out.println(rs.getString("user_id"));
		    }
	    } catch (SQLException e) {
	        throw new IllegalStateException("Cannot connect to database.", e);
	    }
	}
	

	void removeSubscription(Movie movie, String userEmail) {
		// TODO Stored procedure does not exist in the database yet.
    	System.out.println("Connecting to database...");
	    try (Connection conn = DriverManager.getConnection(url, username, password)) {		    	
	    	System.out.println("Database connected.");
	    	String convertedDate = convertDate(movie.getReleaseDate());
	        PreparedStatement statement = conn.prepareStatement("call sp_remove_subscription(?, ?, ?);");
	        statement.setString(1, userEmail);
	        statement.setString(2, movie.getTitle());
	        statement.setString(3, convertedDate);   
	        ResultSet rs = statement.executeQuery();
	        rs = statement.executeQuery("SELECT * from Subscription");
		    while (rs.next()) {
		    	System.out.print(rs.getString("movie_id") + "   ");
		    	System.out.println(rs.getString("user_id"));
		    }
	    } catch (SQLException e) {
	        throw new IllegalStateException("Cannot connect to database.", e);
	    }
	}

	public static ArrayList<Alert> getTodaysAlerts() {
		ArrayList<Alert> alerts = null;
		System.out.println("Connecting to database...");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement statement = null;
	    try {
	    	conn = DriverManager.getConnection(url, username, password);
	    	System.out.println("Database connected.");
	        statement = conn.prepareStatement("");  // TODO enter query and process alerts
	        rs = statement.executeQuery();
	        String currTitle = null;
	        if (rs.next()) {
	        	currTitle = rs.getString("title");
	        }
			while (rs.next()) {
				rs.getString("title");
			}
	    } catch (SQLException e) {
	        throw new IllegalStateException("Cannot connect to database.", e);
	    } 
	    return alerts;
	}

}

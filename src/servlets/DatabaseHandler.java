package servlets;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;

public class DatabaseHandler {
	
	static String url = "jdbc:mysql://104.197.167.29:3306/lighthousedb";
	static String username = "root";
	static String password = "password";
    
    
    void addUser(String userEmail) throws SQLException {
    	System.out.println("Connecting to database...");
	    try (Connection conn = DriverManager.getConnection(url, username, password)) {		    	
	    	System.out.println("Database connected.");
	        PreparedStatement statement = conn.prepareStatement("call sp_add_user('" + userEmail + "');");
	        ResultSet rs = statement.executeQuery();
	        rs = statement.executeQuery("SELECT * from User");
		    while (rs.next()) {
		    	System.out.println(rs.getString("email"));
		    }
	    } catch (SQLException e) {
	        throw new IllegalStateException("Cannot connect to database.", e);
	    }
    }

	void addMovie(Movie movie) {
		
	}
	
	
	void addSubscription(Movie movie, String userEmail) {	
    	System.out.println("Connecting to database...");
	    try (Connection conn = DriverManager.getConnection(url, username, password)) {		    	
	    	System.out.println("Database connected.");
	        PreparedStatement statement = conn.prepareStatement("call sp_add_subscription('" + userEmail 
	        		+ "', '" + movie.getTitle() + "', '" + movie.getReleaseDate().toString() + ");");
	        ResultSet rs = statement.executeQuery();
	        rs = statement.executeQuery("SELECT * from Subscription");
		    while (rs.next()) {
		    	System.out.println(rs.getString("movie_id"));
		    }
	    } catch (SQLException e) {
	        throw new IllegalStateException("Cannot connect to database.", e);
	    }
	}
	

	void removeSubscription(Movie movie, String userID) {
		
		
	}

	

}

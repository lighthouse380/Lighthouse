package servlets;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.appengine.api.users.User;

public class DatabaseHandler {
	
    String url = "jdbc:mysql://localhost:3306/lighthousedb";
    String username = "java";
    String password = "password";	
	
    DatabaseHandler() {
        System.out.println("Connecting to database...");
        try (java.sql.Connection connection = DriverManager.getConnection(url, username, password)) {
        	System.out.println("Database connected.");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to database.", e);
        }
    }
    
    void addUser(User user) {
    	
    }
    
    
    
	void addMovie(Movie movie) {
		
	}
	
	boolean checkMovie(Movie movie) {
		boolean haveMovie = false;

		return haveMovie;
	}
	
	
	void addSubscription(Movie movie, String userID) {	
		if (!checkMovie(movie)) {
			addMovie(movie);
		}
		
		
	}
	

	void removeSubscription(Movie movie, String userID) {
	}

	

}

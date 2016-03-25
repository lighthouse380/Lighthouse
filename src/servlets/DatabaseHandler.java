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
    
    public static boolean checkSubscription(Movie movie, String userEmail){
    	return false;
    }
    
	static void addMovie(Movie movie) {
		
	}
	
	static boolean checkMovie(Movie movie) {
		boolean haveMovie = false;

		return haveMovie;
	}
	
	
	public static boolean addSubscription(Movie movie, String userEmail) {	
		if (!checkMovie(movie)) {
			addMovie(movie);
		}
		
		return true;
	}
	

	void removeSubscription(Movie movie, String userID) {
	}

	

}

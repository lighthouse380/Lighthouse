package servlets;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {
	
    String url = "jdbc:mysql://localhost:3306/javabase";
    String username = "java";
    String password = "password";	
	
	
	void addMovie(Movie movie) {
		
	}
	
	boolean checkMovie(Movie movie) {
		boolean haveMovie = false;
        System.out.println("Connecting to database...");

        try (java.sql.Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected.");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to database.", e);
        }
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

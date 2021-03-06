/* 
 * Class: 			DatabaseHandler
 * Author:			Carrick Bartle
 * Date Created:	03-21-2016
 * Purpose:			Sends queries about subscriptions, alerts, and users to the database and receives output 
 * 					for processing.
 * 
 * */
package utilities;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

public class DatabaseHandler {
	
	private static final Logger log = Logger.getLogger(DatabaseHandler.class.getName());

	static final String USERNAME = "root";
	static final String PASSWORD = "password";
	static final String GOOGLE_URL = "jdbc:google:mysql://lighthouse-1243:lighthousedb1/LighthouseDB?useUnicode=yes&characterEncoding=UTF-8";
	static final String GOOGLE_CLASS = "com.mysql.jdbc.GoogleDriver";
	static final String LOCAL_URL = "jdbc:mysql://localhost:3306/LighthouseDB";

	static Connection conn = null;
	static String url = null;
	
	static {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			// Running from Google App Engine.
			url = GOOGLE_URL;
			try {
				Class.forName(GOOGLE_CLASS);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			// Running locally.
			url = LOCAL_URL;
		}		
	}
	
	public static PreparedStatement getStatement(String query) throws SQLException {
		/* 
		 * Method Name:		getStatement()
		 * Author:			Carrick Bartle
		 * Date Created:	04-01-2016
		 * Purpose:			Sends a particular query to the Cloud SQL or a local Lighthouse database
		 * 					and returns a PreparedStatement for that query.
		 * Input: 			The query in string form.
		 * Return:			A PreparedStatement for the query that can then be executed.			
		 * */
		
		// Connect to a Lighthouse database and create a prepared statement with the given query.
    	PreparedStatement statement = null;
	    try {
            // Form connection to the database.
			conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
			statement = conn.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    return statement;
	}
    
    public static void addUser(String userEmail) throws SQLException  {	 
		/* 
		 * Method Name: 	addUser()
		 * Author:			Carrick Bartle
		 * Date Created:	03-24-2016
		 * Purpose:			Adds a given user to the User table of the Lighthouse database.
		 * Input: 			A string containing the user's email address.
		 * Return:			N/A			
		 * */
    	
		// Submit query to the database to add the given user. 
		PreparedStatement ps = getStatement("call sp_add_user(?);");
        ps.setString(1, userEmail);
		ps.executeQuery();			
		conn.close();
		conn = null;
    }
    
    public static void deleteUser(String userEmail) throws SQLException {
		/* 
		 * Method Name: 	addUser()
		 * Author:			Carrick Bartle
		 * Date Created:	04-14-2016
		 * Purpose:			Deletes the given user (and their subscriptions and alerts) from the Lighthouse database.
		 * Input: 			A string containing the user's email address.
		 * Return:			N/A	
		 * */
    	
		// Submit query to the database to delete the given user. 
		PreparedStatement ps = getStatement("call sp_delete_user(?);");
        ps.setString(1, userEmail);
		ps.executeQuery();			
		conn.close();
		conn = null;
    }
    
	
	public static void addSubscription(Movie movie, String userEmail) throws SQLException {	
		/* 
		 * Method Name: 	addSubscription()
		 * Author:			Carrick Bartle
		 * Date Created:	03-25-2016
		 * Purpose:			Adds a subscription to the the Lighthouse database for a particular
		 * 					user and movie.
		 * Input: 			A movie object.
		 * 					A string containing the user's email address.
		 * Return:			N/A
		 * */    
		
		// Convert the movie's release date into a MySQL-friendly string in the form of a date.
    	String convertedDate = Util.convertDate(movie.getReleaseDate());
    	
    	// Send a query to the database to add a subscription to a movie for the user. 
        PreparedStatement statement = getStatement("call sp_add_subscription(?, ?, ?, ?, ?);");
        statement.setString(1, userEmail);
        statement.setString(2, movie.getMovieDBID());
        statement.setString(3, movie.getTitle());
        statement.setString(4, convertedDate);  
        statement.setString(5, movie.getImgUrl());
        statement.executeQuery();
		conn.close();
		conn = null;
	}
	
	public static HashSet<Movie> getSubscriptions(String userEmail) {
		/* 
		 * Method Name: 	getSubscriptions()
		 * Author:			Carrick Bartle
		 * Date Created:	04-01-2016
		 * Purpose:			Fetches all the movies a given user is subscribed to and returns as a HashSet.
		 * Input: 			A string containing the user's email address.
		 * Return:			HashSet of all the movies a given user is subscribed to.
		 * */   
		
		HashSet<Movie> movies = new HashSet<>();
		
			// Query the database to retrieve all the movies the user is subscribed to. 
			try {
	        PreparedStatement statement = getStatement("call sp_get_subscriptions(?);");
	        statement.setString(1, userEmail);
	        ResultSet rs = statement.executeQuery();
	        
	        // Convert the results from the database into Movie objects.
			while (rs.next()) {
				movies.add(new Movie(rs.getString("title"), rs.getDate("releaseDate"), rs.getString("imageURL"), String.valueOf(rs.getInt("movie_id")))); 
			}
			conn.close();
			conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return movies;
	}
	
	public static ArrayList<Movie> getListofSubscriptions(String userEmail) throws SQLException {
		/* 
		 * Method Name: 	getListofSubscriptions()
		 * Author:			Carrick Bartle
		 * Date Created:	04-09-2016
		 * Purpose:			Fetches all the movies a given user is subscribed to in a particular order.
		 * Input: 			A string containing the user's email address.
		 * Return:			ArrayList of all the movies a given user is subscribed to first ordered chronologically 
		 * 					for movies that have not been released yet, then in reverse chronological order for 
		 * 					released movies.
		 * */   
		
		// Query the database to retrieve all the movies the user is subscribed to. 
        PreparedStatement statement = getStatement("call sp_get_subscriptions(?);");
        statement.setString(1, userEmail);
        ResultSet rs = statement.executeQuery();
        
        // Convert the results from the database into Movie objects.
		ArrayList<Movie> movies = new ArrayList<>();
		while (rs.next()) {			
			Calendar today = Calendar.getInstance();
			today.clear(Calendar.HOUR); today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
			Date todaysDate = today.getTime();
			Movie newMovie = new Movie(rs.getString("title"), rs.getDate("releaseDate"),  rs.getString("imageURL"), rs.getString("movie_id"));
			if (rs.getDate("releaseDate").after(todaysDate)) {   
				movies.add(0, newMovie); // Add unreleased movies to the head of the list (ascending chronological order).
			} else {
				movies.add(newMovie); // Add released movies to the end of the list (descending chronological order).
			}
		}
		conn.close();
		conn = null;
        return movies;
	}
	
	public static void deleteSubscription(Movie movie, String userEmail) throws SQLException {
		/* 
		 * Method Name: 	deleteSubscription()
		 * Author:			Carrick Bartle
		 * Date Created:	03-31-2016
		 * Purpose:			Deletes a user's subscription.
		 * Input: 			A movie object.
		 * 					A string containing the user's email address.
		 * Return:			N/A
		 * */
		    	
		// Connect to database and execute query to remove movie from user's subscriptions.
        PreparedStatement statement = getStatement("call sp_delete_subscription(?, ?);"); 
        statement.setString(1, userEmail);
        statement.setString(2, movie.getMovieDBID());    
        statement.executeQuery();
		conn.close();
		conn = null;
	}

	public static ArrayList<Alert> getTodaysAlerts() throws SQLException {
		/* 
		 * Method Name: 	getTodaysAlerts()
		 * Author:			Carrick Bartle
		 * Date Created:	04-01-2016
		 * Purpose:			Fetches all the alerts from the Lighthouse database that are scheduled for today.
		 * Input: 			N/A
		 * Return:			An ArrayList of all the alerts scheduled for today.			
		 * */    
		
		ArrayList<Alert> alerts = new ArrayList<>();

		// Retrieve all of today's alerts from the database, sorted by title, then release date.
		PreparedStatement statement = getStatement("call sp_get_todays_alerts();");
        ResultSet rs = statement.executeQuery();
        
        /*
         *  Convert the results from the database into Alert objects.
         */
        String currTitle = null;
        Date currDate = null;
        ArrayList<String> emailAddresses = new ArrayList<>();
        
        // Initialize the variables with information from the first alert before entering the loop.  
        if (rs.next()) {   
    		currTitle = rs.getString("title");
    		currDate = rs.getDate("releaseDate");
    		emailAddresses.add(rs.getString("email"));
        }
        
		// Get all the subscribers to each movie and add alerts when a new movie is reached.
		while (rs.next()) {
			if (currTitle != null && !(currTitle.equals(rs.getString("title"))
								  	   && currDate.equals(rs.getDate("releaseDate")))) { // TODO make sure this works  
        		Alert newAlert = new Alert(currTitle, emailAddresses, currDate);
        		alerts.add(newAlert);
        		currTitle = rs.getString("title");
        		currDate = rs.getDate("releaseDate");
        		emailAddresses = new ArrayList<>();
        	} 
        	emailAddresses.add(rs.getString("email"));
		}
		
		// Reached the end of the results. Add the final alert.
		if (currTitle != null) { 
			Alert newAlert = new Alert(currTitle, emailAddresses, currDate);
			alerts.add(newAlert);			
		}
		conn.close();
		conn = null;
	    return alerts;
	}

	public static ArrayList<Movie> getAllMovies() throws SQLException {
		/* 
		 * Method Name: 	getAllMovies()
		 * Author:			Carrick Bartle
		 * Date Created:	04-13-2016
		 * Purpose:			Fetches every movie our users are subscribed to and returns them in an ArrayList 
		 * 					sorted by release date.
		 * Input: 			N/A
		 * Return:			An ArrayList of all the movies our users are subscribed to sorted by release date.			
		 * */    
		ArrayList<Movie> movies = new ArrayList<>();
		
		// Connect to database and execute query to get all the movies.
        PreparedStatement statement = getStatement("call sp_get_all_subscribed_movies()"); 
        ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			
			// Convert results from database into Movie objects.
			Movie newMovie = new Movie("", rs.getDate("releaseDate"), 
									   "", String.valueOf(rs.getInt("movie_id"))); 
			movies.add(newMovie);
		}
		conn.close();
		conn = null;
		
		return movies;
	}
	
	public static void updateReleaseDate(Movie movie) throws SQLException {
		/* 
		 * Method Name: 	updateReleaseDate()
		 * Author:			Carrick Bartle
		 * Date Created:	04-14-2016
		 * Purpose:			Updates a movie's release date with the latest one from The MovieDB.
		 * Input: 			Movie object.
		 * Return:			N/A			
		 * */   
		
		// Convert the movie's release date into a MySQL-friendly string in the form of a date.
    	String convertedDate = Util.convertDate(movie.getReleaseDate());
    	
		// Connect to database and execute query to update the movie's release date.
        PreparedStatement statement = getStatement("call sp_modify_release_date(?, ?)"); 
        statement.setString(1, movie.getMovieDBID()); 
        statement.setString(2, convertedDate);  
        statement.executeQuery();
		conn.close();
		conn = null;
	}
	
	public static ArrayList<String> getSubscribers(Movie movie) throws SQLException {
		/* 
		 * Method Name: 	getSubscribers()
		 * Author:			Carrick Bartle
		 * Date Created:	04-14-2016
		 * Purpose:			Fetches all the users subscribed to a given movie and returns their email addresses.
		 * Input: 			Movie object.
		 * Return:			An ArrayList of email addresses of users subscribed to the movie.
		 * */   
		ArrayList<String> subscribers = new ArrayList<>();
		
		// Fetch email addresses from the database.
        PreparedStatement statement = getStatement("call sp_get_movie_subscribers(?)");
        statement.setString(1, movie.getMovieDBID()); 
        ResultSet rs = statement.executeQuery();
        
        // Add each email address to an ArrayList.
        while (rs.next()) {
        	subscribers.add(rs.getString("email"));
        }
        conn.close();
		conn = null;
		return subscribers;
	}
	
	public static void deleteOldAlerts() throws SQLException {
		/* 
		 * Method Name: 	deleteOldAlerts()
		 * Author:			Carrick Bartle
		 * Date Created:	04-20-2016
		 * Purpose:			Deletes all old alerts from the database.
		 * Input: 			N/A
		 * Return:			N/A
		 * */ 
		
		// Trigger the database's stored procedure to delete any alerts scheduled for before today.
		PreparedStatement statement = getStatement("call sp_delete_old_alerts()"); 
        statement.executeQuery();
		conn.close();
		conn = null;	
	}

}

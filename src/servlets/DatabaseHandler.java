/* 
 * Class: 			DatabaseHandler
 * Author:			Carrick Bartle
 * Date Created:	03-21-2016
 * Purpose:			Sends queries about subscriptions, alerts, and users to the database and receives output 
 * 					for processing.
 * 
 * */
package servlets;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

public class DatabaseHandler {
	
//	private static final Logger log = Logger.getLogger(LighthouseServlet.class.getName());
	
	static String url = null;
	static {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			// Running from Google App Engine.
			url = "jdbc:google:mysql://lighthouse-1243:lighthousedb1/LighthouseDB";
		} else {
			// Running locally.
			url = "jdbc:mysql://localhost:3306/LighthouseDB";
		}
	}
	static String username = "root";
	static String password = "password";
	
	public static PreparedStatement getStatement(String query) {
		/* 
		 * Method Name:		getStatement()
		 * Author:			Carrick Bartle
		 * Date Created:	04-01-2016
		 * Purpose:			Sends a particular query to the Cloud SQL or a local Lighthouse database
		 * 					and returns a PreparedStatement for that query.
		 * Input: 			The query in string form.
		 * Return:			A PreparedStatement for the query that can then be executed.			
		 * */
//		log.warning("Hello from DatabaseHandler");
//    	System.out.println("Connecting to database...");
    	Connection conn = null;
    	PreparedStatement statement = null;
	    try {  // Connects to a Lighthouse database and creates a prepared statement with the given query.
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
				// Running from Google App Engine.
				Class.forName("com.mysql.jdbc.GoogleDriver");
			}
		    conn = DriverManager.getConnection(url, username, password);
//	    	System.out.println("Database connected.");
	        statement = conn.prepareStatement(query);
	    } catch (SQLException | ClassNotFoundException e) {
	        throw new IllegalStateException("Cannot connect to database.", e);
	    } 
	    return statement;
	}
    
	public static void printUsers() throws SQLException {
		/* 
		 * Method Name:		printUsers()
		 * Author:			Carrick Bartle
		 * Date Created:	03-24-2016
		 * Purpose:			Prints all the users in the User table of the Lighthouse database.
		 * Input: 			N/A
		 * Return:			N/A			
		 * */
		
		// Connect to database and execute query.
		PreparedStatement ps = getStatement("SELECT * from User;");  
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {   
			// Iterate through each row of the results to print each user's email address and database ID. 
			System.out.println(rs.getString("email") + " " + rs.getString("user_id"));
		}
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
    	
		// Connect to database and execute query.
		PreparedStatement ps = getStatement("call sp_add_user(?);");
        ps.setString(1, userEmail);
		ps.executeQuery();
    }
    
    private static String convertDate(Date javaDate) {
		/* 
		 * Method Name: 	convertDate()
		 * Author:			Carrick Bartle
		 * Date Created:	03-24-2016
		 * Purpose:			Takes a java.util.Date and converts it to a string that can be parsed
		 * 					as a DATE in MySQL.
		 * Input: 			A java.util.Date.
		 * Return:			A string with the given date in the form of "yyyy-MM-dd". 
		 * */ 
 
    	java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
    	String mySQLdate = sdf.format(javaDate);  // Convert date to given format.
    	return mySQLdate;
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
    	String convertedDate = convertDate(movie.getReleaseDate());
        PreparedStatement statement = getStatement("call sp_add_subscription(?, ?, ?);");
        statement.setString(1, userEmail);
        statement.setString(2, movie.getTitle());
        statement.setString(3, convertedDate);   
        statement.executeQuery();
//        System.out.println("Supposedly subscribed");
	}
	
	public static HashSet<Movie> getSubscriptions(String userEmail) throws SQLException {
		/* 
		 * Method Name: 	getSubscriptions()
		 * Author:			Carrick Bartle
		 * Date Created:	04-01-2016
		 * Purpose:			Fetches all the movies a given user is subscribed to.
		 * Input: 			A string containing the user's email address.
		 * Return:			HashSet of all the movies a given user is subscribed to
		 * */    
		HashSet<Movie> movies = new HashSet<>();
        PreparedStatement statement = getStatement("call sp_get_subscriptions(?);");
        statement.setString(1, userEmail);
        ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			movies.add(new Movie(rs.getString("title"), rs.getDate("releaseDate"), ""));
//			log.warning(rs.getString("title"));
		}
        return movies;
	}
	
	public static void deleteSubscription(Movie movie, String userEmail) throws SQLException {
		/* 
		 * Method Name: 	deleteSubscription()
		 * Author:			Carrick Bartle
		 * Date Created:	03-31-2016
		 * Purpose:			Deletes a subscription to a movie for a user.
		 * Input: 			A movie object.
		 * 					A string containing the user's email address.
		 * Return:			N/A
		 * */
    	String convertedDate = convertDate(movie.getReleaseDate());
        PreparedStatement statement = getStatement("call sp_delete_subscription(?, ?, ?);");
        statement.setString(1, userEmail);
        statement.setString(2, movie.getTitle());
        statement.setString(3, convertedDate);   
        statement.executeQuery();
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
		PreparedStatement statement = getStatement("call sp_get_todays_alerts();");
        ResultSet rs = statement.executeQuery();
        String currTitle = null;
        Date currDate = null;
        ArrayList<String> emailAddresses = new ArrayList<>();
        if (rs.next()) {
    		currTitle = rs.getString("title");
    		currDate = rs.getDate("releaseDate");
    		emailAddresses.add(rs.getString("email"));
//    		System.out.println(currTitle);
//    		System.out.println(currDate);
//    		System.out.println(rs.getString("email"));
        }
		while (rs.next()) {
			if (currTitle != null && !currTitle.equals(rs.getString("title"))) {
//				System.out.print(currTitle);
//				System.out.println(rs.getString("title"));
        		Alert newAlert = new Alert(currTitle, emailAddresses, currDate);
        		alerts.add(newAlert);
        		currTitle = rs.getString("title");
        		currDate = rs.getDate("releaseDate");
        		emailAddresses = new ArrayList<>();
//        		System.out.println(currTitle);
//        		System.out.println(currDate);
        	} 
        	emailAddresses.add(rs.getString("email"));
//    		System.out.println(rs.getString("email"));
		}
		if (currTitle != null) {
			Alert newAlert = new Alert(currTitle, emailAddresses, currDate);
			alerts.add(newAlert);			
		}
	    return alerts;
	}

	
	
//    public static boolean checkSubscription(Movie movie, String userEmail) throws SQLException{
//    	boolean subscribed = false;
////	    System.out.println("Java date:" + movie.getReleaseDate());
//    	String convertedDate = convertDate(movie.getReleaseDate());
////	   	System.out.println("MySQL date:" + convertedDate);
//        PreparedStatement statement = getStatement("SELECT fn_check_subscription(?, ?, ?);");
//        statement.setString(1, userEmail);
//        statement.setString(2, movie.getTitle());
//        statement.setString(3, convertedDate);
//        ResultSet rs = statement.executeQuery();
//        if (rs.next()) {
//        	if (rs.getBoolean(1)) {
//        		subscribed = true;
//        		//System.out.println("Subscribed");
//        	} else {
//        		//System.out.println("Not subscribed");
//        	}
//	    } else {
//	    	System.out.println("Subscription information not found.");
//	    }
//    	return subscribed;
//    }
}

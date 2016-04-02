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
	
	private static final Logger log = Logger.getLogger(LighthouseServlet.class.getName());
	
	static String url = null;
	static {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			// Running from Google App Engine.
			url = "jdbc:google:mysql://lighthouse-1243:lighthousedb1/LighthouseDB";
		} else {
			url = "jdbc:mysql://localhost:3306/LighthouseDB";
		}
	}
	static String username = "root";
	static String password = "password";
	
	public static PreparedStatement getStatement(String query) {
//		log.warning("Hello from DatabaseHandler");
//    	System.out.println("Connecting to database...");
    	Connection conn = null;
    	PreparedStatement statement = null;
	    try {
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
				// This is App Engine.
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
		PreparedStatement ps = getStatement("SELECT * from User;");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			System.out.println(rs.getString("email") + " " + rs.getString("user_id"));
		}
	}
    
    public static void addUser(String userEmail) throws SQLException  {	    
		PreparedStatement ps = getStatement("call sp_add_user(?);");
        ps.setString(1, userEmail);
		ps.executeQuery();
    }
    
    private static String convertDate(Date javaDate) {
    	java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String mySQLdate = sdf.format(javaDate);
    	return mySQLdate;
    }
	
	public static void addSubscription(Movie movie, String userEmail) throws SQLException {	
    	String convertedDate = convertDate(movie.getReleaseDate());
        PreparedStatement statement = getStatement("call sp_add_subscription(?, ?, ?);");
        statement.setString(1, userEmail);
        statement.setString(2, movie.getTitle());
        statement.setString(3, convertedDate);   
        statement.executeQuery();
//        System.out.println("Supposedly subscribed");
	}
	
	public static HashSet<Movie> getSubscriptions(String userEmail) throws SQLException {
		HashSet<Movie> movies = new HashSet<>();
        PreparedStatement statement = getStatement("call sp_get_subscriptions(?);");
        statement.setString(1, userEmail);
        ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			movies.add(new Movie(rs.getString("title"), rs.getDate("releaseDate"), ""));
			log.warning(rs.getString("title"));
		}
        return movies;
	}
	
	void deleteSubscription(Movie movie, String userEmail) throws SQLException {
    	String convertedDate = convertDate(movie.getReleaseDate());
        PreparedStatement statement = getStatement("call sp_delete_subscription(?, ?, ?);");
        statement.setString(1, userEmail);
        statement.setString(2, movie.getTitle());
        statement.setString(3, convertedDate);   
        statement.executeQuery();
	}

	public static ArrayList<Alert> getTodaysAlerts() throws SQLException {
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

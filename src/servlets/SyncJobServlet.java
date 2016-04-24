/* 
 * Class: 			SyncJobServlet
 * Author:			Carrick Bartle
 * Date Created:	04-09-2016
 * Purpose:			Scheduled every day to get the release dates for all subscribed movies from 
 * 					The Movie DB to check for any updates and email subscribers any changes.
 * 
 * */

package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import Utilities.DatabaseHandler;
import Utilities.Movie;
import Utilities.Util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SuppressWarnings("serial")
public class SyncJobServlet extends HttpServlet {
	
	private static final String API_URL = "http://api.themoviedb.org/3/movie/";
	private static final String API_KEY = "?api_key=59471fd0915a80b420b392a5db81f1c2";
	
	private static final Logger log = Logger.getLogger(SyncJobServlet.class.getName());
	
	static final String LIGHTHOUSE_EMAIL = "thelighthouse380@gmail.com"; 
	static final String LIGHTHOUSE_NAME = "Lighthouse";
	static final String EMAIL_SUBJECT = "Lighthouse Release Date Update";
	static final String EMAIL_TEXT = " has changed its release date from ";
	static final String TO = " to ";
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws MalformedURLException, IOException {
		/* 
		 * Method Name:		doGet()
		 * Author:			Carrick Bartle
		 * Date Created:	04-09-2016
		 * Purpose:			Checks all the movies that our users are subscribed to for any changes to their release dates.
		 * Input: 			HttpServletRequest and HttpServletResponse. 
		 * Return:			N/A			
		 * */

		ArrayList<Movie> movies = new ArrayList<>();
		
		// Fetch all movies in the database that have not already been released.
    	try {
			movies = DatabaseHandler.getAllMovies();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
		for (Movie movie : movies) {
			
			// API call to fetch info for a particular movie.
			String url = API_URL + movie.getMovieDBID() + API_KEY;
			
			// Get the release date from the json object returned from the API call.
	        String json = IOUtils.toString(new URL(url));
	        JsonParser parser = new JsonParser();
	        JsonElement element = parser.parse(json);
	        
	        if (element.isJsonObject()) {
	            JsonObject pages = element.getAsJsonObject();
	            String dateString = pages.get("release_date").getAsString();
	            movie.setTitle(pages.get("title").getAsString());
                Date releaseDate = null;
                
                // Parse the release date returned in the json object.
                if (!dateString.isEmpty()) {
					try {
						releaseDate = Util.parseDate(dateString, "yyyy-MM-dd");
					} catch (ParseException e) {
						e.printStackTrace();
					}
                } else { // This movie has not been given a release date.
					try {
						releaseDate = Util.parseDate("0000-00-00", "yyyy-MM-dd");
					} catch (ParseException e) {
						e.printStackTrace();
					} 
                }
                if (releaseDate == null) {
                	log.warning("Something went wrong with the release date.");
                } else {
                	
                	// Release date at The MovieDB is different from the one in our database.
	                if (!releaseDate.equals(movie.getReleaseDate())) {
	                	
	                	// Change our database to reflect the new release date.
	                	Date oldDate = movie.getReleaseDate();
	                	movie.setReleaseDate(releaseDate);  
	                	try {
							DatabaseHandler.updateReleaseDate(movie);
							
							// Send an email to all subscribers to this movie informing them of the change.
							mailUpdate(movie, oldDate);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
	                }
	            }
	        }
    	}
    	
    }
    
    
    
    void mailUpdate(Movie movie, Date oldDate) throws SQLException {
		/* 
		 * Method Name:		mailUpdate()
		 * Author:			Carrick Bartle
		 * Date Created:	04-14-2016
		 * Purpose:			To send emails to users to notify them that the release date of a movie they're 
		 * 					subscribed to has changed to a different date.
		 * Input: 			Movie object, the movie's old release date as a java.util.Date 
		 * Return:			N/A			
		 * */
    	
    	// Fetch all subscribers to a particular movie from the database.
    	ArrayList<String> subscribers = DatabaseHandler.getSubscribers(movie);
    	
    	Properties props = new Properties();
    	Session session = Session.getDefaultInstance(props, null); 
    	for (String subscriber : subscribers) {
    		try {
    			
    			// Populate an email with subject, text, email address.
	    	    Message msg = new MimeMessage(session);
	    	    msg.setFrom(new InternetAddress(LIGHTHOUSE_EMAIL, LIGHTHOUSE_NAME));
	    	    msg.setSubject(EMAIL_SUBJECT);
	    	    String oldDateStr = Util.convertDate(oldDate);
	    	    String newDateStr = Util.convertDate(movie.getReleaseDate());
	    	    msg.setText(movie.getTitle() + EMAIL_TEXT + oldDateStr + TO + newDateStr + ".");
	    	    
	    	    // Send the message to each subscriber to this movie.
	    	    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(subscriber));   
	    	    Transport.send(msg);  // Sends the email in a MimeMessage.	    	    	
	    	} catch (UnsupportedEncodingException | MessagingException e) {
	        	e.printStackTrace();
	    	}
    	}
    }
    
    
    
}

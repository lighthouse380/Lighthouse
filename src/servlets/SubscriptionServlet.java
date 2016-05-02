/* 
 * Class: 			SubscriptionServlet
 * Author:			Khajag Basmajian and Sevan Gregorian 
 * Date Created:	04-13-2016
 * Purpose:			Handle GET and POST requests from servlet subscriptions.jsp
 * 					Query The database for subscribed movies
 * 					Display page of current and expired subscriptions
 * */

package servlets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import utilities.DatabaseHandler;
import utilities.Movie;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gwt.thirdparty.guava.common.eventbus.Subscribe;

@SuppressWarnings("serial")
public class SubscriptionServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException, ServletException {

		/* 
		 * Method Name:		doGet()
		 * Author:			Khajag Basmajian and Sevan Gregorian
		 * Date Created:	04-14-2016
		 * Purpose:			Loads attributes of subscriptions.jsp and sends the page to the browser. 
		 * Input: 			HTTP request and response which include attributes 
		 * Return:			method is void, but produces the jsp page.
		 *                              If the user is subscribed to a movie, image, name, release date data
		 * 				are organized on the page in order from most recent subscriptions to latest.
		 *				Expired subscriptions are organized in order of recent to past.
		 * */
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS");
		fmt.setTimeZone(new SimpleTimeZone(0, ""));

		//Get the currently logged in user with Google's User API
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		//Generate the URL that directs the user to Google's Sign in/out page
		//it will redirect with authentication to the root url "/" after user signs in/out
		String loginUrl = userService.createLoginURL("/");
		String logoutUrl = userService.createLogoutURL("/");

		ArrayList<Movie> subscribedMovies = new ArrayList<Movie>();
		
		// set attributes for subscriptions.jsp
		req.setAttribute("user", user);
		req.setAttribute("loginUrl", loginUrl);
		req.setAttribute("logoutUrl", logoutUrl);

		//Add user's email to Database (unless it exists already)
		if (user != null) {
			try {
				DatabaseHandler.addUser(user.getEmail());
			} catch (SQLException e) {
				e.printStackTrace();
			}				
		}
		
		// Load the users subscribed movies from DB. 
		if (user != null) {
			try {
				// GetMovieIMG adds all the images of the movies to the array because 
				// the are not being stored in the DB
				subscribedMovies = this.getMovieImg(DatabaseHandler.getListofSubscriptions(user.getEmail()));
			} catch (ParseException | SQLException e) {
				e.printStackTrace();
			}

		}

		// set attributes for subscriptions.jsp		
		req.setAttribute("subscribedMovies", subscribedMovies);
		req.setAttribute("currentTime", fmt.format(new Date()));

		resp.setContentType("text/html");

		// Send req to subscriptions.jsp
		RequestDispatcher jsp = req.getRequestDispatcher("/WEB-INF/subscriptions.jsp");
		jsp.forward(req, resp);
	}

public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		/* 
		 * Method Name:		doPost()
		 * Author:			Khajag Basmajian and Sevan Gregorian
		 * Date Created:	04-14-2016
		 * Purpose:			Take homepage's POST requests which is for unsubscribing 
		 * 					from movies in the users subscriptions list.
		 * Input: 			HTTP request and response which include attributes
		 * 					(such as the movie's information for subscribing)
		 * Return:			method is void, redirects to the doGet	
		 * */
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String userEmail = user.getEmail();
		
		//get movie's title, image URL, subscription status, release date
		//from client based on the movie they are unsubscribing/subscribing to
		String movieTitle = req.getParameter("title");
		String movieImg = req.getParameter("imgUrl");
		String susbcribed = req.getParameter("subscribed");
		Date movieDate = null;
		// try to parse the movie date into correct format
		try {
			movieDate = this.parseDate(req.getParameter("releaseDate"), "yyyy-MM-dd");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Get the list of subscribed movies
		ArrayList<Movie> subscribedMovies = new ArrayList<Movie>();
		Movie x = null;
		// call getMovieImg to get all the images for the list of subscriptions
		// becuase they are not stored in the DB
		try {
			subscribedMovies = this.getMovieImg(DatabaseHandler.getListofSubscriptions(user.getEmail()));
		} catch (ParseException | SQLException e) {
			e.printStackTrace();
		}
		
		// For each movie check if the movie the user is attempting to unsubcribe from matches
		// a movie in the subscribed movies list
		for(int i = 0; i < subscribedMovies.size(); i++)
		{
			if(subscribedMovies.get(i).getTitle().equals(movieTitle) && subscribedMovies.get(i).getReleaseDate().equals(movieDate)){
				x = subscribedMovies.get(i);
			}
		}
		
		// if the movie is found delete the movie
		try {
			if (susbcribed.equalsIgnoreCase("true") && x != null){
				DatabaseHandler.deleteSubscription(x, userEmail);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect("/subscriptions");
	}

	private ArrayList<Movie> getMovieImg(ArrayList<Movie> movieList)
			throws MalformedURLException, IOException, ParseException,
			SQLException {

		/* 
		 * Method Name:		getMovies()
		 * Author:			Kahajag Basmajian and Sevan Gregorian
		 * Date Created:	04-14-2016
		 * Purpose:			Add the movie image to every movie in the list given.
		 * Input: 			ArrayList of movies.
		 * Return:			Arraylist of movies with the image urls added.
		 * */
		
		for (int i = 0; i < movieList.size(); i++) {
			
			//encode movie title for API call
	 		String movieTitle = java.net.URLEncoder.encode(movieList.get(i).getTitle(), "UTF-8");
	 		
	 		// api url to use for making calls to api
			String url = "http://api.themoviedb.org/3/search/movie?api_key=59471fd0915a80b420b392a5db81f1c2&query="
					+ movieTitle;
			String json = IOUtils.toString(new URL(url));
			JsonParser parser = new JsonParser();

			// Element is the root node of the parsed JSON
			// Using element we can access the keys/values in the JSON
			JsonElement element = parser.parse(json);

			if (element.isJsonObject()) {
				JsonObject pages = element.getAsJsonObject();
				JsonArray movies = pages.getAsJsonArray("results");
				JsonObject dataset = movies.get(0).getAsJsonObject();

				Movie x = movieList.get(i);

				// Add the image to each movie object
				if (!dataset.get("poster_path").isJsonNull()) {
					String imgUrl = dataset.get("poster_path").getAsString();
					x.setImgUrl("http://image.tmdb.org/t/p/w500/" + imgUrl);
				} 
				else
				x.setImgUrl("https://placehold.it/200x300?text=Movie"); 
				//sets to placeholder image if no movie image is provided
			}
		}

		return movieList;
	}

	  private Map<String, SimpleDateFormat> hashFormatters = new HashMap<String, SimpleDateFormat>();

	    public Date parseDate(String date, String format) throws ParseException
	    {
			/* 
			 * Method Name:		parseDate()
			 * Author:			Harout Grigoryan
			 * Date Created:	03-16-2016
			 * Purpose:			Creates Date formatted object based on given date string
			 * Input: 			String with date and desired format for Date
			 * Return:			Formatted Date object
			 * */
	    	
	        SimpleDateFormat formatter = hashFormatters.get(format);

	        if (formatter == null)
	        {
	            formatter = new SimpleDateFormat(format);
	            hashFormatters.put(format, formatter);
	        }

	        return formatter.parse(date);
	    }
}

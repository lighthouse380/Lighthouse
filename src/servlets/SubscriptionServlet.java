/* 
 * Class: 			LighthouseServlet
 * Author:			Khajag Basmajian and Sevan Gregorian 
 * Date Created:	04-13-2016
 * Purpose:			Handle GET and POST requests from servlet subscriptions.jsp
 * 					Query The database for subscribed movies
 * 
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


		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS");
		fmt.setTimeZone(new SimpleTimeZone(0, ""));

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String loginUrl = userService.createLoginURL("/");
		String logoutUrl = userService.createLogoutURL("/");


		ArrayList<Movie> subscribedMovies = new ArrayList<Movie>();
		
		req.setAttribute("user", user);
		req.setAttribute("loginUrl", loginUrl);
		req.setAttribute("logoutUrl", logoutUrl);

		if (user != null) {
			try {
				DatabaseHandler.addUser(user.getEmail());
			} catch (SQLException e) {
				e.printStackTrace();
			}				
		}
		try {
			DatabaseHandler.printUsers();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if (user != null) {
			try {
				subscribedMovies = this.getMovieImg(DatabaseHandler.getListofSubscriptions(user.getEmail()));
			} catch (ParseException | SQLException e) {
				e.printStackTrace();
			}

		}

		
		req.setAttribute("subscribedMovies", subscribedMovies);
		req.setAttribute("currentTime", fmt.format(new Date()));

		resp.setContentType("text/html");

		RequestDispatcher jsp = req.getRequestDispatcher("/WEB-INF/subscriptions.jsp");
		jsp.forward(req, resp);
	}

public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		/* 
		 * Method Name:		doPost()
		 * Author:			Harout Grigoryan
		 * Date Created:	03-16-2016
		 * Purpose:			Take homepage's POST requests which include subscribing/unsubscribing
		 * 					then use DatabaseHandler to update the DB accordingly.
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
		try {
			movieDate = this.parseDate(req.getParameter("releaseDate"), "EEE MMM dd kk:mm:ss zzz yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Movie movie = new Movie(movieTitle, movieDate, movieImg);

		try {
			if (susbcribed.equalsIgnoreCase("false")){
				DatabaseHandler.addSubscription(movie, userEmail);
			} else {
				DatabaseHandler.deleteSubscription(movie, userEmail);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect("/subscriptions");
	}

	private ArrayList<Movie> getMovieImg(ArrayList<Movie> movieList)
			throws MalformedURLException, IOException, ParseException,
			SQLException {


		for (int i = 0; i < movieList.size(); i++) {
			
			//encode movie title for API call
	 		String movieTitle = java.net.URLEncoder.encode(movieList.get(i).getTitle(), "UTF-8");
	 		
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

				if (!dataset.get("poster_path").isJsonNull()) {
					String imgUrl = dataset.get("poster_path").getAsString();
					x.setImgUrl("http://image.tmdb.org/t/p/w500/" + imgUrl);
				} 
				else
				x.setImgUrl("https://placehold.it/200x300?text=Movie"); 
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

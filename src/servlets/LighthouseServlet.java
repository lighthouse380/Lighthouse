/* 
 * Class: 			LighthouseServlet
 * Author:			Harout Grigoryan
 * Date Created:	03-14-2016
 * Purpose:			Handle GET and POST requests from home.jsp (homepage)
 * 					Query TheMovieDB based on search title
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
public class LighthouseServlet extends HttpServlet {	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		/* 
		 * Method Name:		doGet()
		 * Author:			Harout Grigoryan
		 * Date Created:	03-14-2016
		 * Purpose:			Loads attributes of home.jsp and sends the page to the browser. 
		 * Input: 			HTTP request and response which include attributes 
		 * Return:			method is void, but produces the jsp page.			
		 * */
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS");
		fmt.setTimeZone(new SimpleTimeZone(0, ""));
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String loginUrl = userService.createLoginURL("/");
		String logoutUrl = userService.createLogoutURL("/");
		
		
		ArrayList<Movie> searchResults = new ArrayList<Movie>();
		String movieTitle = req.getParameter("movie_title");

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
		
	 	if (movieTitle != null && movieTitle != ""){		
			//encode movie title for API call
	 		movieTitle = java.net.URLEncoder.encode(movieTitle, "UTF-8");
	 		
			try {
				searchResults = this.getMovies(movieTitle, user);
			} catch (ParseException | SQLException e) {
				e.printStackTrace();
			}
			//decode so search bar shows correct string
			movieTitle = java.net.URLDecoder.decode(movieTitle, "UTF-8");
		}
		
		req.setAttribute("searchResults", searchResults);
		req.setAttribute("currentTime", fmt.format(new Date()));
		req.setAttribute("movie_title", movieTitle);
		
		resp.setContentType("text/html");
		
		RequestDispatcher jsp = req.getRequestDispatcher("/WEB-INF/home.jsp");
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
		
		//Get the search entry to reload the page with the same search results
		//after they clicked sub/unsub
		String searchTitle = req.getParameter("movie_title");

		try {
			if (susbcribed.equalsIgnoreCase("false")){
				DatabaseHandler.addSubscription(movie, userEmail);
			} else {
				DatabaseHandler.deleteSubscription(movie, userEmail);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect("/?movie_title=" + java.net.URLEncoder.encode(searchTitle, "UTF-8"));
	}
	
	 private ArrayList<Movie> getMovies(String title, User user) throws MalformedURLException, IOException, ParseException, SQLException{
			
		 	/* 
			 * Method Name:		getMovies()
			 * Author:			Harout Grigoryan
			 * Date Created:	03-16-2016
			 * Purpose:			Do TheMovieDB API call based on client's search title,
			 * 					parse the JSON from the API call, construct Movie
			 * 					objects, add Movie objects to ArrayList for return
			 * Input: 			Title being searched (UTF-8 encoded), current User that's signed in.
			 * Return:			Arraylist of Movie objects for search results
			 * */
		 
		 ArrayList<Movie> movieList = new ArrayList<Movie>();
		 	
		 	//Base URL for TheMovieDB API's movie search, append title to this
	    	String url = "http://api.themoviedb.org/3/search/movie?api_key=59471fd0915a80b420b392a5db81f1c2&query=" + title;
	        String json = IOUtils.toString(new URL(url));
	        JsonParser parser = new JsonParser();

	        //Element is the root node of the parsed JSON
	        //Using element we can access the keys/values in the JSON
	        JsonElement element = parser.parse(json);
	        	        
	        if (element.isJsonObject()) {
	            JsonObject pages = element.getAsJsonObject();
	            JsonArray movies = pages.getAsJsonArray("results");
	            
	            //Get user's subscriptions to see if they're subscribed to any of the search results
	            HashSet<Movie> subscriptions = null;
	            if (user != null){
            		subscriptions = DatabaseHandler.getSubscriptions(user.getEmail());
	            }
	            
	            for (int i = 0; i < movies.size(); i++) {
	                JsonObject dataset = movies.get(i).getAsJsonObject();
	                String dateString = dataset.get("release_date").getAsString();
	                String imgUrl;
	                Date releaseDate;
	                
	                if (!dateString.isEmpty())
	                	releaseDate = this.parseDate(dateString, "yyyy-MM-dd");
	                else
	                	releaseDate = this.parseDate("0000-00-00", "yyyy-MM-dd"); //no date found
	                
	                if (!dataset.get("poster_path").isJsonNull()){
	                	imgUrl = dataset.get("poster_path").getAsString();
	                	imgUrl = "http://image.tmdb.org/t/p/w500/" + imgUrl;
	                }
	                else
	                	imgUrl = "https://placehold.it/200x300?text=Movie"; //no img found
	                
	                Movie movie = new Movie(dataset.get("original_title").getAsString(), releaseDate, imgUrl);

	                if (user != null && subscriptions.contains(movie)) {
	                	movie.subscribed = true;
	                } else {
	                	movie.subscribed = false;	                	
	                }

	                movieList.add(movie);
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

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
				searchResults = this.getMoviesInfo(movieTitle, user);
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
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String userEmail = user.getEmail();
		
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
		String searchTitle = req.getParameter("movie_title");

		try {
			if (susbcribed.equalsIgnoreCase("false")){
				DatabaseHandler.addSubscription(movie, userEmail);
			} else {
				DatabaseHandler.deleteSubscription(movie, userEmail);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resp.sendRedirect("/?movie_title=" + java.net.URLEncoder.encode(searchTitle, "UTF-8"));
	}
	
	 private ArrayList<Movie> getMoviesInfo(String title, User user) throws MalformedURLException, IOException, ParseException, SQLException{
		 	ArrayList<Movie> movieList = new ArrayList<Movie>();
		 	
	    	String url = "http://api.themoviedb.org/3/search/movie?api_key=59471fd0915a80b420b392a5db81f1c2&query=" + title;
	        String json = IOUtils.toString(new URL(url));
	        JsonParser parser = new JsonParser();

	        // The JsonElement is the root node. It can be an object, array, null or
	        // java primitive.
	        JsonElement element = parser.parse(json);
	        	        
	        // use the isxxx methods to find out the type of jsonelement. In our
	        // example we know that the root object is the Albums object and
	        // contains an array of dataset objects
	        if (element.isJsonObject()) {
	            JsonObject pages = element.getAsJsonObject();
	            //System.out.println("Page " + pages.get("page").getAsString());
	            JsonArray datasets = pages.getAsJsonArray("results");
	            
	            //Get user's subscriptions to see if they're subscribed to any of the search results
	            HashSet<Movie> subscriptions = null;
	            if (user != null){
            		subscriptions = DatabaseHandler.getSubscriptions(user.getEmail());
	            }
	            
	            for (int i = 0; i < datasets.size(); i++) {
	                JsonObject dataset = datasets.get(i).getAsJsonObject();
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
	        SimpleDateFormat formatter = hashFormatters.get(format);

	        if (formatter == null)
	        {
	            formatter = new SimpleDateFormat(format);
	            hashFormatters.put(format, formatter);
	        }

	        return formatter.parse(date);
	    }
}

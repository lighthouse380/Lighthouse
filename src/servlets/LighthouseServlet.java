package servlets;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

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


@SuppressWarnings("serial")
public class LighthouseServlet extends HttpServlet {
	//logger for the memcache check (optional)
	
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
		
			
			
			DatabaseHandler.addUser(user.getEmail());
//			DatabaseHandler.printUsers();

			
		
	 	if (movieTitle != null && movieTitle != ""){		
			movieTitle = uriEncode(movieTitle);
			try {
				searchResults = this.getMoviesInfo(movieTitle, user);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
			

		
		req.setAttribute("searchResults", searchResults);
		req.setAttribute("currentTime", fmt.format(new Date()));
		
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
		
		Date movieDate = null;
		try {
			movieDate = this.parseDate(req.getParameter("releaseDate"), "EEE MMM dd kk:mm:ss zzz yyyy");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Movie movie = new Movie(movieTitle, movieDate, movieImg);
		DatabaseHandler.addSubscription(movie, userEmail);
		
		resp.sendRedirect("/");
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

	                if (user != null && DatabaseHandler.checkSubscription(movie, user.getEmail()))
	                	movie.subscribed = true;
	                else
	                	movie.subscribed = false;
	                

	                movieList.add(movie);
	            }
	        }
	        
	        return movieList;
	    }
	    
	    public String uriEncode(String string) {
	        String result = string;
	        if (null != string) {
	            try {
	                String scheme = null;
	                String ssp = string;
	                int es = string.indexOf(':');
	                if (es > 0) {
	                    scheme = string.substring(0, es);
	                    ssp = string.substring(es + 1);
	                }
	                result = (new URI(scheme, ssp, null)).toString();
	            } catch (URISyntaxException usex) {
	                // ignore and use string that has syntax error
	            }
	        }
	        return result;
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
package servlets;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String loginUrl = userService.createLoginURL("/");
		String logoutUrl = userService.createLogoutURL("/");
		
		req.setAttribute("user", user);
		req.setAttribute("loginUrl", loginUrl);
		req.setAttribute("logoutUrl", logoutUrl);
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Key userKey = KeyFactory.createKey("UserPrefs", user.getUserId());
		Entity userPrefs = new Entity(userKey);
		
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		String cacheKey = "UserPrefs:" + user.getUserId();
	 	ArrayList<Movie> searchResults = new ArrayList<Movie>();
		
		try{
			//double tzOffset = new Double(req.getParameter("tz_offset")).doubleValue();
			
			//userPrefs.setProperty("tz_offset", tzOffset);
			userPrefs.setProperty("user", user);
			ds.put(userPrefs);
			memcache.delete(cacheKey);
			String movieTitle = req.getParameter("movie_title");
			
			java.lang.System.out.println(movieTitle);
			
			movieTitle = uriEncode(movieTitle);
			searchResults = this.getMoviesInfo(movieTitle);
			
		} catch (ParseException e){
			//User enetered a value that wasn't a double. Ignore it for now.
		}
		
		req.setAttribute("searchResults", searchResults);
		
		resp.setContentType("text/html");
		
		RequestDispatcher jsp = req.getRequestDispatcher("/WEB-INF/home.jsp");
		jsp.forward(req, resp);
	}
	

	 private ArrayList<Movie> getMoviesInfo(String title) throws MalformedURLException, IOException, ParseException{
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
	                
	                Date releaseDate = this.parseDate(dateString, "yyyy-MM-dd");
	                		
	                Movie movie = new Movie(dataset.get("original_title").getAsString(), releaseDate);

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
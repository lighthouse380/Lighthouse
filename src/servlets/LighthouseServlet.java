package servlets;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.logging.*;

import org.apache.commons.io.IOUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


@SuppressWarnings("serial")
public class LighthouseServlet extends HttpServlet {
	//logger for the memcache check (optional)
	private static final Logger log = Logger.getLogger(LighthouseServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS");
		fmt.setTimeZone(new SimpleTimeZone(0, ""));
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String loginUrl = userService.createLoginURL("/");
		String logoutUrl = userService.createLogoutURL("/");
		
		req.setAttribute("user", user);
		req.setAttribute("loginUrl", loginUrl);
		req.setAttribute("logoutUrl", logoutUrl);
		
		/*if (req.getAttribute("searchResults") == null){
			req.setAttribute("searchResults", null);
		}*/
		
		Entity userPrefs = null;
		//Get user from Memcache, if not there get from Datastore and store in memcache for faster retrieval
		if(user != null){
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
			
			String cacheKey = "UserPrefs:" + user.getUserId();
			userPrefs = (Entity)memcache.get(cacheKey);

			//LOGGING MEMCACHE (OPTIONAL)
			if(userPrefs == null){
				log.warning("CACHE MISS");
			} else {
				log.warning("CACHE HIT! :D");
			}
			
			if(userPrefs == null){	
				Key userKey = KeyFactory.createKey("UserPrefs", user.getUserId());
				try{
					userPrefs = ds.get(userKey);
					memcache.put(cacheKey, userPrefs);
				} catch (EntityNotFoundException e){
					//no user prefs stored
				}
			}
			
		}
		if (userPrefs != null) {
			//double tzOffset = ((Double) userPrefs.getProperty("tz_offset")).doubleValue();
			//fmt.setTimeZone(new SimpleTimeZone((int) (tzOffset * 60 * 60 * 1000), ""));
			//req.setAttribute("tzOffset", tzOffset);
		} else {
			//req.setAttribute("tzOffset", 0);
		}
		
		req.setAttribute("currentTime", fmt.format(new Date()));
		
		resp.setContentType("text/html");
		
		RequestDispatcher jsp = req.getRequestDispatcher("/");
		jsp.forward(req, resp);
		
	}
	
	
}
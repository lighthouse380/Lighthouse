package servlets;


import java.io.IOException;
import java.text.ParseException;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
		
			//double tzOffset = new Double(req.getParameter("tz_offset")).doubleValue();
			
			//userPrefs.setProperty("tz_offset", tzOffset);
			userPrefs.setProperty("user", user);
			ds.put(userPrefs);
			memcache.delete(cacheKey);
		
		
		resp.setContentType("text/html");
		
		RequestDispatcher jsp = req.getRequestDispatcher("/WEB-INF/home.jsp");
		jsp.forward(req, resp);
	}
	


}
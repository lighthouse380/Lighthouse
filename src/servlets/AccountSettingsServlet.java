/* 
 * Class: 			AccountSettingsServlet
 * Author:			Khajag Basmajian 
 * Date Created:	04-17-2016
 * Purpose:			Handle GET and POST requests from servlet settings.jsp
 * 					The user can veiw their account settings and delete their 
 * 					profile from the DB
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

import utilities.DatabaseHandler;

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
public class AccountSettingsServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		/* 
		 * Method Name:		doGet()
		 * Author:			Khajag Basmajian
		 * Date Created:	04-17-2016
		 * Purpose:			Loads attributes of settings.jsp and sends the page to the browser. 
		 * Input: 			HTTP request and response which include attributes 
		 * Return:			method is void, but produces the jsp page.			
		 * */
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS");
		fmt.setTimeZone(new SimpleTimeZone(0, ""));

		//Get the currently logged in user with Google's User API
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		//Generate the URL that directs the user to Google's Sign in page
		//it will redirect with authentication to the root url "/" after user signs in
		String loginUrl = userService.createLoginURL("/");
		String logoutUrl = userService.createLogoutURL("/");

		req.setAttribute("user", user);
		req.setAttribute("loginUrl", loginUrl);
		req.setAttribute("logoutUrl", logoutUrl);

		// if the user is not null, add the user to the database
		if (user != null) {
			try {
				DatabaseHandler.addUser(user.getEmail());
			} catch (SQLException e) {
				e.printStackTrace();
			}				
		}

		resp.setContentType("text/html");

		// Send req to subscriptions.jsp
		RequestDispatcher jsp = req.getRequestDispatcher("/WEB-INF/settings.jsp");
		jsp.forward(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		/* 
		 * Method Name:		doPost()
		 * Author:			Khajag Basmajian 
		 * Date Created:	04-17-2016
		 * Purpose:			Take homepage's POST requests which is deleting
		 * 					the users account from the DB
		 * Input: 			HTTP request and response which include attributes
		 * 					(such as the users email)
		 * Return:			method is void, redirects to the doGet	
		 * */
		
		// Get the current users information from userservice
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String userEmail = user.getEmail();
		
		// if the user is not null or empty delete the user from DB
		try {
			if (userEmail != null || userEmail != ""){
				DatabaseHandler.deleteUser(userEmail);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// redirect back to home page because the user is now logged out
		resp.sendRedirect(userService.createLogoutURL("/"));
	}

}

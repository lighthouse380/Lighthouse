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

import Utilities.DatabaseHandler;
import Utilities.Movie;
import Utilities.Util;

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
	
	private static final String CHAR_ENCODING = "UTF-8";
	private static final Logger log = Logger.getLogger(LighthouseServlet.class.getName());
	
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
		String subscribed = req.getParameter("subscribed");
		String movieDBID = req.getParameter("movieDBID");

		Date movieDate = null;
		try {
			movieDate = Util.parseDate(req.getParameter("releaseDate"), "EEE MMM dd kk:mm:ss zzz yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		log.warning("From doPost: The movie's ID is: " + movieDBID);
		Movie movie = new Movie(movieTitle, movieDate, movieImg, movieDBID);
		
		//Get the search entry to reload the page with the same search results
		//after they clicked sub/unsub
		String searchTitle = req.getParameter("movie_title");

		try {
			if (subscribed.equalsIgnoreCase("false")) {
				DatabaseHandler.addSubscription(movie, userEmail);
			} else {
				DatabaseHandler.deleteSubscription(movie, userEmail);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//resp.sendRedirect("/?movie_title=" + java.net.URLEncoder.encode(searchTitle, CHAR_ENCODING));
	}

	    
}

package servlets;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SyncJobServlet extends HttpServlet {
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		/* 
		 * Method Name:		doGet()
		 * Author:			Carrick Bartle
		 * Date Created:	04-09-2016
		 * Purpose:			Checks all the movies that our users are subscribed to for any changes to their release dates.
		 * Input: 			HttpServletRequest and HttpServletResponse. 
		 * Return:			N/A			
		 * */

		ArrayList<Movie> movies = null;
    	try {
			movies = DatabaseHandler.getAllMovies();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	if (movies != null) {
    		
    		
    		
    	}
    	
    	
    	
    	
    }
}

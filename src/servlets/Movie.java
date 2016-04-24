/* 
 * Class: 			Movie
 * Author:			Carrick Bartle and Harout Grigoryan
 * Date Created:	03-21-2016
 * Purpose:			Holds information about a movie and enables a user to subscribe to and unsubscribe from it.
 * 
 * */
package servlets;

import java.sql.SQLException;
import java.util.Date;


public class Movie {
	
	private String title;
	private String imgUrl;
	private Date releaseDate;
	private String movieDBID;
	
	Boolean subscribed; 
	
	Movie(String title, Date releaseDate, String imgUrl, String movieDBID) {
		/* 
		 * Method Name:		Movie()
		 * Author:			Carrick Bartle and Harout Grigoryan
		 * Date Created:	03-21-2016
		 * Purpose:			Constructor for Movie class. Sets title, release date, and a URL to an 
		 * 					image of the movie.
		 * Input: 			The movie title in the form of a string. 
		 * 					The release date as a java.util.Date.
		 * 					The URL in the form of a string.
		 * Return:			A PreparedStatement for the query that can then be executed.			
		 * */
		
		// Set movie's title, release date, and image URL.
		this.title = title;
		this.releaseDate = releaseDate;
		this.imgUrl = imgUrl;
		this.movieDBID = movieDBID;  // The movie's unique identifier from The Movie Database.
	}
	
    @Override
    public int hashCode() {
		/* 
		 * Method Name:		hashCode()
		 * Author:			Carrick Bartle
		 * Date Created:	04-02-2016
		 * Purpose:			Overrides hashcode() to compute the hash code for the Movie object with 
		 * 					only its title and release date.
		 * Input: 			N/A
		 * Return:			A hash code computed with the Movie object's title and release date.			
		 * */
    	
    	// Compute hash code based on the title and release date. 
        final int prime = 31;
        int result = 1;
        result = prime * result + movieDBID.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
		/* 
		 * Method Name:		equals()
		 * Author:			Carrick Bartle
		 * Date Created:	04-02-2016
		 * Purpose:			Overrides equals() so that different Movie objects with the same ID from The Movie DB 
		 * 					evaluate to equal.
		 * Input: 			An object.
		 * Return:			A boolean that returns true if both objects are Movies that have the same MovieDB ID 
		 * 					and false otherwise.			
		 * */
    	
        if (this == obj)  // Return true if the movies being compared are the same object.
            return true;
        
        // Return false if the object is null or not the same class.
        if (obj == null)  
            return false;
        if (getClass() != obj.getClass()) 
            return false;
        
        Movie otherMovie = (Movie) obj;
        if (!movieDBID.equals(otherMovie.getMovieDBID()))  // Check if the two movies have the same ID.
            return false;
        return true;
    }
	
	void subscribe(String userID) throws SQLException {
		/* 
		 * Method Name:		subscribe()
		 * Author:			Carrick Bartle
		 * Date Created:	03-21-2016
		 * Purpose:			Adds a subscription to this movie for a given user to the Lighthouse database. 
		 * Input: 			A user's email in the form of a string. 
		 * Return:			N/A			
		 * */
		DatabaseHandler.addSubscription(this, userID);
	}
	
	void unsubscribe(String userID) throws SQLException {
		/* 
		 * Method Name:		unsubscribe()
		 * Author:			Carrick Bartle
		 * Date Created:	03-21-2016
		 * Purpose:			Removes a subscription to this movie for a given user from the Lighthouse database. 
		 * Input: 			A user's email in the form of a string. 
		 * Return:			N/A			
		 * */
		DatabaseHandler.deleteSubscription(this, userID);
	}
	
	public String getTitle() {
		/* 
		 * Method Name:		getTitle()
		 * Author:			Carrick Bartle
		 * Date Created:	03-21-2016
		 * Purpose:			Returns the title of this movie.
		 * Input: 			N/A 
		 * Return:			A string of this movie's title.			
		 * */
		return title;
	}

	public void setTitle(String title) {
		/* 
		 * Method Name:		setTitle()
		 * Author:			Carrick Bartle
		 * Date Created:	03-21-2016
		 * Purpose:			Sets the title of this movie.
		 * Input: 			A string of this movie's title.
		 * Return:			N/A
		 * */
		this.title = title;
	}

	public Date getReleaseDate() {
		/* 
		 * Method Name:		getReleaseDate()
		 * Author:			Carrick Bartle
		 * Date Created:	03-21-2016
		 * Purpose:			Returns the release date of this movie.
		 * Input: 			N/A
		 * Return:			A java.util.Date with the release date of this movie.			
		 * */
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		/* 
		 * Method Name:		setReleaseDate()
		 * Author:			Carrick Bartle
		 * Date Created:	03-21-2016
		 * Purpose:			Sets the release date of this movie.
		 * Input: 			A java.util.Date with the release date of this movie.	
		 * Return:			N/A
		 * */
		this.releaseDate = releaseDate;
	}

	public String getImgUrl() {
		/* 
		 * Method Name:		getImgUrl()
		 * Author:			Harout Grigoryan
		 * Date Created:	03-23-2016
		 * Purpose:			Returns the URL to the movie's poster image on 3rd party site.
		 * Input: 			N/A	
		 * Return:			String with URL
		 * */
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		/* 
		 * Method Name:		setImgUrl()
		 * Author:			Harout Grigoryan
		 * Date Created:	03-23-2016
		 * Purpose:			Sets the movie's image URL.
		 * Input: 			String with URL to movie's image	
		 * Return:			N/A
		 * */
		this.imgUrl = imgUrl;
	}


	public Boolean getSubscribed() {
		/* 
		 * Method Name:		getSubscribed()
		 * Author:			Harout Grigoryan
		 * Date Created:	03-23-2016
		 * Purpose:			Returns current user's subscription status for the movie.
		 * Input: 			N/A
		 * Return:			Boolean with subscription status
		 * */
		return subscribed;
	}


	public void setSubscribed(Boolean subscribed) {
		/* 
		 * Method Name:		setSubscribed()
		 * Author:			Harout Grigoryan
		 * Date Created:	03-23-2016
		 * Purpose:			Sets subscription status for the current user.
		 * Input: 			Subscription status boolean.	
		 * Return:			N/A
		 * */
		this.subscribed = subscribed;
	}

	public String getMovieDBID() {
		/* 
		 * Method Name:		getTheMovieDBID()
		 * Author:			Carrick Bartle
		 * Date Created:	04-13-2016
		 * Purpose:			Returns the movie's unique ID from The MovieDB.
		 * Input: 			N/A	
		 * Return:			String of the movie's unique ID from The MovieDB.
		 * */
		return movieDBID;
	}

}

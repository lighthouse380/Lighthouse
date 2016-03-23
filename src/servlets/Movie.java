package servlets;

import java.util.Date;


public class Movie {
	
	String title;
	Date releaseDate;
	
	Movie(String title, Date releaseDate) {
		this.title = title;
		this.releaseDate = releaseDate;
	}
	
	void subscribe(String userID) {
		DatabaseHandler dh = new DatabaseHandler();
		dh.addSubscription(this, userID);
	}
	
	void unsubscribe(String userID) {
		DatabaseHandler dh = new DatabaseHandler();
		dh.removeSubscription(this, userID);
	}
	

}

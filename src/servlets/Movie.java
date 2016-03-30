package servlets;

import java.util.Date;


public class Movie {
	
	
	String title;
	String imgUrl;
	Date releaseDate;
	Boolean subscribed; 
	
	Movie(String title, Date releaseDate, String imgUrl) {
		this.title = title;
		this.releaseDate = releaseDate;
		this.imgUrl = imgUrl;
	}
	
	
	void subscribe(String userID) {
		DatabaseHandler dh = new DatabaseHandler();
		DatabaseHandler.addSubscription(this, userID);
	}
	
	void unsubscribe(String userID) {
		DatabaseHandler dh = new DatabaseHandler();
		dh.removeSubscription(this, userID);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}


	public Boolean getSubscribed() {
		return subscribed;
	}


	public void setSubscribed(Boolean subscribed) {
		this.subscribed = subscribed;
	}

	

}

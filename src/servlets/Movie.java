package servlets;

import java.sql.SQLException;
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
	
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + title.hashCode();
        result = prime * result + releaseDate.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Movie otherMovie = (Movie) obj;
        if (!title.equals(otherMovie.title))
            return false;
        if (!releaseDate.equals(otherMovie.releaseDate))
            return false;
        return true;
    }
	
	void subscribe(String userID) throws SQLException {
		DatabaseHandler.addSubscription(this, userID);
	}
	
	void unsubscribe(String userID) throws SQLException {
		DatabaseHandler dh = new DatabaseHandler();
		dh.deleteSubscription(this, userID);
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

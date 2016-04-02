/* 
 * Class: 			Alert
 * Author:			Khajag Basmajian
 * Date Created:	03-20-2016
 * Purpose:			Holds information about alerts, including a movie's title, release date, and subscribers.
 * 
 * */
package servlets;

import java.util.ArrayList;
import java.util.Date;


public class Alert {

	private String title;
	private Date releaseDate;
	private ArrayList<String> emailAddresses;

	public Alert(String title, ArrayList<String> emailAddresses, Date releaseDate) {
		this.title = title;
		this.emailAddresses = emailAddresses;
		this.releaseDate = releaseDate;
	}

	public String getTitle() {
		return title;
	}

	public ArrayList<String> getEmailAddresses() {
		return emailAddresses;
	}
	
	public Date getReleaseDate() {
		return releaseDate;
	}

}

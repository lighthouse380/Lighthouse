package servlets;

import java.util.ArrayList;
import java.util.Date;

// Used by DailyEmails and DatabaseHandler.

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

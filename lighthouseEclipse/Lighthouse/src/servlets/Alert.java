package servlets;

import java.util.ArrayList;

// Alert will be used by other classes 

public class Alert {

	private String movieID;
	private ArrayList<String> accountID;

	public Alert(String movieID, ArrayList<String> accountID) {
		this.movieID = movieID;
		this.accountID = accountID;
	}

	public String getMovieID() {
		return movieID;
	}

	public ArrayList<String> getAccountID() {
		return accountID;
	}

}

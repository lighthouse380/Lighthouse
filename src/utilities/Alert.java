/* 
 * Class: 			DailyEmailsServlet
 * Author:			Khajag Basmajian
 * Date Created:	03-20-2016
 * Purpose:			Holds data pertaining to alerts (subscribers' email addresses, title, release date).
 * 
 * */
package utilities;

import java.util.ArrayList;
import java.util.Date;


public class Alert {

	private String title;
	private Date releaseDate;
	private ArrayList<String> emailAddresses;

	public Alert(String title, ArrayList<String> emailAddresses, Date releaseDate) {
		/* 
		 * Method Name:		Alert()
		 * Author:			Khajag Basmajian
		 * Date Created:	03-20-2016
		 * Purpose:			Alert constructor
		 * Input: 			Title string, strings of email addresses in ArrayList, release date as a java.util.Date. 
		 * Return:			N/A			
		 * */
		 
		this.title = title;
		this.emailAddresses = emailAddresses;
		this.releaseDate = releaseDate;
	}

	public String getTitle() {
		/* 
		 * Method Name:		getTitle()
		 * Author:			Khajag Basmajian
		 * Date Created:	03-20-2016
		 * Purpose:			Returns the title of the movie associated with this alert.
		 * Input: 			N/A 
		 * Return:			Title string.			
		 * */

		return title;
	}

	public ArrayList<String> getEmailAddresses() {
		/* 
		 * Method Name:		getEmailAddresses()
		 * Author:			Khajag Basmajian
		 * Date Created:	03-20-2016
		 * Purpose:			Returns the ArrayList of email addresses for the subscribers to the movie associated with this alert.
		 * Input: 			N/A
		 * Return:			Strings of email addresses in ArrayList.	
		 * */

		return emailAddresses;
	}
	
	public Date getReleaseDate() {
		/* 
		 * Method Name:		getReleaseDate()
		 * Author:			Khajag Basmajian
		 * Date Created:	03-20-2016
		 * Purpose:			Returns the release date of the movie associated with this alert.
		 * Input: 			N/A
		 * Return:			Release date as a java.util.Date. 
		 * */
		return releaseDate;
	}

}

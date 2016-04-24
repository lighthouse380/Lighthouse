/* 
 * Class: 			DailyEmailsServlet
 * Author:			Carrick Bartle and Sevan Gregorian
 * Date Created:	03-31-2016
 * Purpose:			Emails all release date reminders scheduled for that day. See cron.xml for run frequency.
 * 
 * */

package servlets;

//https://cloud.google.com/appengine/docs/java/config/cron   Cron Info
//https://cloud.google.com/appengine/docs/java/mail/         Mail Servlet Info
//https://cloud.google.com/appengine/docs/java/mail/usingjavamail sending mail with java mail

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;   // Both sender and receiving email addresses are represented through this
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;


@SuppressWarnings("serial")
public class DailyEmailsServlet extends HttpServlet {
	
	static final String LIGHTHOUSE_EMAIL = "thelighthouse380@gmail.com"; 
	static final String LIGHTHOUSE_NAME = "Lighthouse";
	static final String EMAIL_SUBJECT = "Lighthouse Subscription Reminder Email";
	static final String EMAIL_TEXT = " will be released on ";
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
		/* 
		 * Method Name:		doGet()
		 * Author:			Carrick Bartle and Sevan Gregorian
		 * Date Created:	04-01-2016
		 * Purpose:			Emails all release date reminders scheduled for today.
		 * Input: 			HttpServletRequest and HttpServletResponse. 
		 * Return:			N/A			
		 * */
    	
    	// Fetch all of today's alerts from the database, converted to Alert objects.
    	ArrayList<Alert> alerts = new ArrayList<>();
		try {
			alerts = DatabaseHandler.getTodaysAlerts();
			DatabaseHandler.deleteOldAlerts();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Initialize JavaMail objects.
//    	writer.println("Hello from DailyEmails");
//    	final String lighthousePassword = "W45ev5`J(#M4MT]2gjn>";
    	Properties props = new Properties();
//    	props.put("mail.smtp.auth", "true");
//    	props.put("mail.smtp.starttls.enable", "true");
//    	props.put("mail.smtp.host", "smtp.gmail.com");
//    	props.put("mail.smtp.port", "587");
    	Session session = Session.getDefaultInstance(props, null); 
//    			new javax.mail.Authenticator() {
//    				protected PasswordAuthentication getPasswordAuthentication() {
//    					return new PasswordAuthentication(LIGHTHOUSE_EMAIL, lighthousePassword); 
//    					} 
//    				});
    	
    	// Iterate through alerts.
    	for (Alert alert : alerts) {  
//    		System.out.println(alert.getTitle() + " " + alert.getReleaseDate());
//    		for (String email : alert.getEmailAddresses()) {
//    			System.out.println(email);
//    		}
    		
    		// Prepare email message with a particular movie's title and release date.
    		try {
	    	    Message msg = new MimeMessage(session);
	    	    msg.setFrom(new InternetAddress(LIGHTHOUSE_EMAIL, LIGHTHOUSE_NAME));
	    	    msg.setSubject(EMAIL_SUBJECT);
	    	    msg.setText(alert.getTitle() + EMAIL_TEXT + alert.getReleaseDate());
	    	    
	    	    // Send the message to each subscriber to this movie.
	    	    for (String email : alert.getEmailAddresses()) {
		    	    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));   
		    	    Transport.send(msg);  // Sends the email in a MimeMessage.	    	    	
	    	    }	    	    	
	    	} catch (UnsupportedEncodingException | MessagingException e) {
	        	PrintWriter writer = resp.getWriter();  // Print the error message to the page.
	        	writer.println(e.toString());   
	    	}
    	}
    }
    	

}

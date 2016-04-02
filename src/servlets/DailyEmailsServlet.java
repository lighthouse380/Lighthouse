package servlets;

/*
To receive email, you must put a section that enables incoming mail in your app's appengine-web.xml file:

<inbound-services>
  <service>mail</service>
</inbound-services>
*/

//https://cloud.google.com/appengine/docs/java/config/cron   Cron Info
//https://cloud.google.com/appengine/docs/java/mail/         Mail Servlet Info
//https://cloud.google.com/appengine/docs/java/mail/usingjavamail sending mail with java mail

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;   //both sender and receiving email addresses are represented through this
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.mail.PasswordAuthentication;

import java.io.InputStream;

import javax.activation.DataHandler;

import java.util.ArrayList;


@SuppressWarnings("serial")
public class DailyEmailsServlet extends HttpServlet {
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
    	ArrayList<Alert> alerts = new ArrayList<>();
		try {
			alerts = DatabaseHandler.getTodaysAlerts();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	PrintWriter writer = resp.getWriter();
//    	writer.println("Hello from DailyEmails");
    	final String lighthouseEmail = "thelighthouse380@gmail.com"; 
//    	final String lighthousePassword = "W45ev5`J(#M4MT]2gjn>";
    	
    	Properties props = new Properties();
//    	props.put("mail.smtp.auth", "true");
//    	props.put("mail.smtp.starttls.enable", "true");
//    	props.put("mail.smtp.host", "smtp.gmail.com");
//    	props.put("mail.smtp.port", "587");
    	Session session = Session.getDefaultInstance(props, null); 
//    			new javax.mail.Authenticator() {
//    				protected PasswordAuthentication getPasswordAuthentication() {
//    					return new PasswordAuthentication(lighthouseEmail, lighthousePassword); 
//    					} 
//    				});
    	for (Alert alert : alerts) {  
    		System.out.println(alert.getTitle() + " " + alert.getReleaseDate());
    		for (String email : alert.getEmailAddresses()) {
    			System.out.println(email);
    		}
    		try {
	    	    Message msg = new MimeMessage(session);
	    	    msg.setFrom(new InternetAddress(lighthouseEmail, "Lighthouse"));
	    	    msg.setSubject("Lighthouse Subscription Reminder Email");
	    	    msg.setText(alert.getTitle() + " will be released on " + alert.getReleaseDate()); // TODO eliminate time from release date
	    	    for (String email : alert.getEmailAddresses()) {
		    	    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));   
		    	    Transport.send(msg);  //javax.mail.Transport to send the email in MimeMessage	    	    	
	    	    }	    	    	
	    	} catch (UnsupportedEncodingException | MessagingException e) {
	        	writer.println(e.toString());   
	    	}
    	}
    	
    }
    	

}

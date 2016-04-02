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
import java.util.Properties;

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


public class DailyEmailsServlet extends HttpServlet {
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
//    	ArrayList<Alert> alerts = DatabaseHandler.getTodaysAlerts();
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
    	
//    	String alertEmail;
//    	for (Alert currAlert : alerts) {
	    	try {
	    	    Message msg = new MimeMessage(session);
	    	    msg.setFrom(new InternetAddress(lighthouseEmail, "Lighthouse"));
	    	    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("carrickdb@gmail.com"));   
	    	    msg.setSubject("Lighthouse Subscription Reminder Email");
	    	    msg.setText("mgjgjhgjh"); // Uses message body to send the text
	    	    Transport.send(msg);  //javax.mail.Transport to send the email in MimeMessage
	        	writer.println("Email supposedly sent.");
	    	} catch (UnsupportedEncodingException | MessagingException e) {
	        	writer.println(e.toString());
	    	    
	    	}     		
//    	}
    	
    }
    	

}

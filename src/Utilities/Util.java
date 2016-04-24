package Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Util {
	
	private static Map<String, SimpleDateFormat> hashFormatters = new HashMap<String, SimpleDateFormat>();

    public static Date parseDate(String date, String format) throws ParseException
    {
		/* 
		 * Method Name:		parseDate()
		 * Author:			Harout Grigoryan
		 * Date Created:	03-16-2016
		 * Purpose:			Creates Date formatted object based on given date string
		 * Input: 			String with date and desired format for Date
		 * Return:			Formatted Date object
		 * */
    	
        SimpleDateFormat formatter = hashFormatters.get(format);

        if (formatter == null)
        {
            formatter = new SimpleDateFormat(format);
            hashFormatters.put(format, formatter);
        }

        return formatter.parse(date);
    }

    public static String convertDate(Date javaDate) {
		/* 
		 * Method Name: 	convertDate()
		 * Author:			Carrick Bartle
		 * Date Created:	03-24-2016
		 * Purpose:			Takes a java.util.Date and converts it to a string that can be parsed
		 * 					as a DATE in MySQL.
		 * Input: 			A java.util.Date.
		 * Return:			A string with the given date in the form of "yyyy-MM-dd". 
		 * */ 
 
    	// Set the date format and convert the input Date into that format.
    	java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
    	String mySQLdate = sdf.format(javaDate);
    	return mySQLdate;
    }
    
}

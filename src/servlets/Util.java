package servlets;

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

}

package cs601.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cs601.database.Status;
import cs601.hotelapp.HotelWithRating;

/**
 * Provides base functionality to all servlets in this example. Original author:
 * Prof. Engle
 *
 * @see RegisterServer
 */
@SuppressWarnings("serial")
public class BaseServlet extends HttpServlet {

/*	protected void prepareResponse(String title, HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();

			writer.println("<!DOCTYPE html>");
			writer.println("<html>");
			writer.println("<head>");
			writer.println("\t<title>" + title + "</title>");
			writer.println("</head>");
			writer.println("<body>");
		} catch (IOException ex) {
			System.out.println("IOException while preparing the response: " + ex);
			return;
		}
	}

	protected void finishResponse(HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();

			writer.println();
			writer.println("<p style=\"font-size: 10pt; font-style: italic;\">");
			writer.println("Last updated at " + getDate());
			writer.println("</p>");

			writer.println("</body>");
			writer.println("</html>");

			writer.flush();

			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();
		} catch (IOException ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}

	protected String getDate() {
		String format = "hh:mm a 'on' EEE, MMM dd, yyyy";
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(Calendar.getInstance().getTime());
	}
*/
	/**
	 * Return a cookie map from the cookies in the request
	 * 
	 * @param request
	 * @return
	 */
/*	protected Map<String, String> getCookieMap(HttpServletRequest request) {
		HashMap<String, String> map = new HashMap<String, String>();

		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie.getValue());
			}
		}

		return map;
	}
*/
	/**
	 * Clear cookies
	 * 
	 * @param request
	 * @param response
	 */
/*	protected void clearCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			return;
		}

		for (Cookie cookie : cookies) {
			cookie.setValue("");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

	protected void clearCookie(String cookieName, HttpServletResponse response) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
*/
	
	/**
	 * Return the error message by error name
	 * @param errorName
	 * 		error name
	 * @return
	 * 		error message
	 */
	protected String getStatusMessage(String errorName) {
		Status status = null;

		try {
			status = Status.valueOf(errorName);
		} catch (Exception ex) {
			status = Status.ERROR;
		}

		return status.toString();
	}

	/**
	 * Return the error message by error code
	 * @param code
	 * 		error code
	 * @return
	 * 		error message
	 */
	protected String getStatusMessage(int code) {
		Status status = null;

		try {
			status = Status.values()[code];
		} catch (Exception ex) {
			status = Status.ERROR;
		}

		return status.toString();
	}
	
	@SuppressWarnings("null")
	protected String readWebPage(String path)
	{
		System.out.println(System.getProperty("user.dir"));
		File file = new File(path);
		BufferedReader br = null;
		StringBuffer content = new StringBuffer();
		try {
			br = new BufferedReader(new FileReader(file));
			String templine = null;
			while ((templine = br.readLine()) != null)
			{
				//System.out.println(templine);
				content.append(templine + System.lineSeparator());}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content.toString();
	}
	
	protected String generateJSONString(TreeSet<HotelWithRating> set, String page)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("{\"numOfPages\":\"" + set.size() / 6 + "\"," + "\"hotels\":[");
		Iterator<HotelWithRating> it = set.iterator();
		int p = Integer.parseInt(page);
		int i = 0;
		while (it.hasNext() &&  i < 6 * p)
		{
			HotelWithRating hotel = it.next();
			if (i < 6 * (p-1))
			{
				i++;
				continue;
			}
			sb.append("{\"hotelid\":\"" + hotel.getHotel().getHotelId() + "\",");
			sb.append("\"name\":\"" + hotel.getHotel().getHotelName() + "\",");
			sb.append("\"addr\":\"" + hotel.getHotel().getHotelAddress().getStreet() + "\",");
			sb.append("\"city\":\"" + hotel.getHotel().getHotelAddress().getCity() + "\",");
			sb.append("\"rating\":\"" + hotel.getRating() + "\"}," );
			i++;
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]" + "}");
		return sb.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
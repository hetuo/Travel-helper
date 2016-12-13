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
import java.util.ArrayList;
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
import cs601.hotelapp.Review;

/**
 * Provides base functionality to all servlets in this example. Original author:
 * Prof. Engle
 *
 * @see RegisterServer
 */
@SuppressWarnings("serial")
public class BaseServlet extends HttpServlet {

	public static Map<String, String> userMap = new HashMap<String, String>();
	public static String lastTime = null;
	
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
	
	protected String generateJSONString(ArrayList<Review> set)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("{\"reviews\":[");
		Iterator<Review> it = set.iterator();
		while (it.hasNext())
		{
			Review review = it.next();
			sb.append("{\"author\":\"" + review.getReviewUsername() + "\",");
			sb.append("\"hotel\":\"" + review.getHotelId() + "\",");
			sb.append("\"rating\":\"" + review.getRating() + "\",");
			sb.append("\"id\":\"" + review.getReviewId() + "\",");
			sb.append("\"title\":\"" + review.getReviewTitle() + "\",");
			//sb.append("\"city\":\"" + hotel.getHotel().getHotelAddress().getCity() + "\",");
			sb.append("\"text\":\"" + review.getReviewText() + "\"}," );
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]" + "}");
		return sb.toString();
	}
	
	protected String generateJSONString(ArrayList<Review> set, String page)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("{\"numOfPages\":\"" + set.size() / 10 + "\"," + "\"reviews\":[");
		Iterator<Review> it = set.iterator();
		int p = Integer.parseInt(page);
		int i = 0;
		while (it.hasNext() &&  i < 10 * p)
		{
			Review review = it.next();
			if (i < 10 * (p-1))
			{
				i++;
				continue;
			}
			sb.append("{\"author\":\"" + review.getReviewUsername() + "\",");
			sb.append("\"date\":\"" + review.getReviewDate() + "\",");
			sb.append("\"rating\":\"" + review.getRating() + "\",");
			sb.append("\"id\":\"" + review.getReviewId() + "\",");
			sb.append("\"likes\":\"" + review.getLikes() + "\",");
			//sb.append("\"city\":\"" + hotel.getHotel().getHotelAddress().getCity() + "\",");
			sb.append("\"text\":\"" + review.getReviewText() + "\"}," );
			i++;
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]" + "}");
		return sb.toString();
	}	
	
}
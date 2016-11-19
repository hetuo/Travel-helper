package cs601.hotelapp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cs601.concurrent.WorkQueue;


/**
 * A class that load the information of hotel from JSON files
 * to particular data structures.
 * @author TuoHe
 */
public class HotelDataBuilder {
	
	private ThreadSafeHotelData hoteldata;
	private WorkQueue queue = new WorkQueue();
	private volatile int numTasks = 0;
	private static final Logger logger = LogManager.getLogger();
	
	/** A default constructor for class HotelDataBuilder
	 * @param hoteldata
	 * 		particular data structure to store the information of hotel 
	 * */
	public HotelDataBuilder(ThreadSafeHotelData hoteldata)
	{
		this.hoteldata = hoteldata;
	}
	
	/** A default constructor for class HotelDataBuilder
	 * @param hoteldata
	 * 		particular data structure to store the information of hotel 
	 * @param q
	 * 		work queue to load the review information
	 * */
	public HotelDataBuilder(ThreadSafeHotelData hoteldata, WorkQueue q)
	{
		this.hoteldata = hoteldata;
		queue = q;
	}
	
	
	/**
	 * Read the json file with information about the hotels (id, name, address,
	 * etc) and load it into the appropriate data structure(s). Note: This
	 * method does not load reviews
	 * 
	 * @param jsonFilename
	 *            the name of the json file that contains information about the
	 *            hotels
	 */
	public void loadHotelInfo(String jsonFilename) {

		// Hint: Use JSONParser from JSONSimple library
		// FILL IN CODE
		
		JSONParser parser = new JSONParser();
		try{
			FileReader reader = new FileReader(jsonFilename);
			Object obj = parser.parse(reader);
			reader.close();
			JSONArray hotels = parseJSONFile((JSONObject)obj, "sr");
			int size = hotels.size();
			for (int i = 0; i < size; i++)
			{
				JSONObject hotel = (JSONObject)hotels.get(i);
				
				String hotelId = (String)hotel.get("id");
				String hotelName = (String)hotel.get("f");
				String city = (String)hotel.get("ci");
				String state = (String)hotel.get("pr");
				String streetAddress = (String)hotel.get("ad");
				String country = (String)hotel.get("ccc");
				double lat = 0.00, lon = 0.00;
				for (Object key : hotel.keySet())
				{
					if (hotel.get(key) instanceof JSONObject)
					{
						JSONObject location = (JSONObject)hotel.get(key);
						lat = Double.parseDouble((String)location.get("lat"));
						lon = Double.parseDouble((String)location.get("lng"));
					}
				}
				hoteldata.addHotel(hotelId, hotelName, city, state, streetAddress, lat, lon, country);
			}

		}catch(Exception pe)
		{
			pe.printStackTrace();
		}
		

	}
	
	/**
	 * Read the json file with information about the attractions 
	 * and load it into the appropriate data structure(s). 
	 * 
	 * @param filename
	 *            the name of the json file that contains information about the
	 *            attractions
	 * @param hotelid
	 * 			  the hotelid these attractions nearby
	 */
	public void parseAttractions(String filename, String hotelid)
	{
		JSONParser parser = new JSONParser();
		try{
			FileReader reader = new FileReader(filename);
			Object obj = parser.parse(reader);
			reader.close();	
			JSONArray attractions = parseJSONFile((JSONObject)obj, "results");
			int size = attractions.size();
			//System.out.println(attractions.size());
			for (int i = 0; i < size; i++)
			{
				JSONObject attraction = (JSONObject)attractions.get(i);
				
				String attractionId = (String)attraction.get("id");
				String name = (String)attraction.get("name");
				String address = (String)attraction.get("formatted_address");
				//double rating = Double.parseDouble((String)attraction.get("rating"));
				Number rating;
				if (attraction.get("rating") == null)
					rating = 3.0;
				else 
					rating = (Number)attraction.get("rating");
				//hoteldata.addHotel(attraction, name, address, rating);
				//Number rating = (Number)attraction.get("rating");
				hoteldata.addAttraction(attractionId, name, address, rating, hotelid);
			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	


	/**
	 * Parse JSON Object to JSON Array, find the the JSON array we need then return it.
	 * 
	 * @param jObj
	 *            The JSON object need to parse
	 * @param arrName
	 * 			  The JSON Array's name we need
	 * @return
	 * 			  The JSON Array we need
	 */
	public JSONArray parseJSONFile(JSONObject jObj, String arrName)
	{
		JSONArray jArray = null;
		for (Object key : jObj.keySet())
		{
			if (jObj.get(key) instanceof JSONObject){
				jArray = parseJSONFile((JSONObject)jObj.get(key), arrName);
				if (null != jArray)
					break;
				}
			else if(jObj.get(key) instanceof JSONArray){
				if (key.toString().equals(arrName))
					return (JSONArray)jObj.get(key);
				else
					continue;
			}
			else{
				continue;
			}
		}
		return jArray;
	}
	
	/**
	 * Load reviews for all the hotels into the appropriate data structure(s).
	 * Traverse a given directory recursively to find all the json files with
	 * reviews and load reviews from each json. Note: this method must be
	 * recursive and use DirectoryStream as discussed in class.
	 * 
	 * @param path
	 *            the path to the directory that contains json files with
	 *            reviews Note that the directory can contain json files, as
	 *            well as subfolders (of subfolders etc..) with more json files
	 */
	public void loadReviews(Path path) {

		// FILL IN CODE

		// Hint: first, write a separate method to read a single json file with
		// reviews
		// using JSONSimple library
		// Call this method from this one as you traverse directories and find
		// json files
	
		queue.execute(new DirectoryWorker(path));
	}
	

	/**
	 * DirectoryWorker
	 * An inner class that represents a piece of Runnable work. 
	 * In the run() method, it iterates over all files and subdirectories in a given directory, 
	 * parses each files to a private ThreadSafeHotelData object, then merge it to the shared
	 * ThreadSafeHotelData object.
	 */	
	public class DirectoryWorker implements Runnable {
		private Path directory; // the directory that this DirectoryWorker is responsible for
		private ThreadSafeHotelData privateData = new ThreadSafeHotelData();
		DirectoryWorker(Path dir) {
			directory = dir;
			//logger.debug("Thread begain");
			incrementTasks(); 
		}
		@Override
		public void run() {
			try {
				for (Path path : Files.newDirectoryStream(directory)) {
					if (Files.isDirectory(path)) {
						queue.execute(new DirectoryWorker(path)); // add new DirectoryWorker to the work queue
					} else {
						//logger.debug("Add to paths: " + path.toString());
						if (path.toString().endsWith(".json"))
							loadReview(path.toString(), privateData);
					}
				}
				privateData.mergeReviewData(hoteldata);
				
			} catch (IOException e) {
				//logger.warn("Unable to calculate size for {}", directory);
				//logger.catching(Level.DEBUG, e);
				e.printStackTrace();
			}
			finally {
				decrementTasks(); // done with this task
			}
			
		}
		
	}
	
	/**
	 * Load reviews for all the hotels into the appropriate data structure(s).
	 * Traverse a given directory recursively to find all the json files with
	 * reviews and load reviews from each json. Note: this method must be
	 * recursive and use DirectoryStream as discussed in class.
	 * 
	 * @param file
	 *            the path to the directory that contains json files with
	 *            reviews Note that the directory can contain json files, as
	 *            well as subfolders (of subfolders etc..) with more json files
	 * @param hotels
	 * 			  The data structure that store those reviews
	 */
	public void loadReview(String file, ThreadSafeHotelData hotels)
	{
		JSONParser parser = new JSONParser();
		
		try{
			FileReader read = new FileReader(file);
			Object obj = parser.parse(read);
			read.close();
			JSONArray reviews = parseJSONFile((JSONObject)obj, "review");
			int size = reviews.size();
			String hotelId = null;
			String reviewId = null;
			String reviewTitle = null;
			String reviewText = null;
			String username = null;
			String date = null;
			int rating = 0;
			boolean isRecom = false;
			for (int i = 0; i < size; i++)
			{
				JSONObject review = (JSONObject)reviews.get(i);
				hotelId = (String)review.get("hotelId");
				reviewId = (String)review.get("reviewId");
				reviewTitle = (String)review.get("title");
				reviewText = (String)review.get("reviewText");
				rating = Integer.parseInt(String.valueOf((long)review.get("ratingOverall")));
				if (review.get("isRecommended").equals("YES"))
					isRecom = true;
				username = (String)review.get("userNickname");
				if (null == username || username.equals(""))
				{
					username = new String("anonymous");
				}
				date = review.get("reviewSubmissionTime").toString().substring(0, 10);
				hotels.addReview(hotelId, reviewId, rating, reviewTitle, reviewText, isRecom, date, username);
								
			}			
		}catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}catch (IOException e)
		{
			e.printStackTrace();
		}catch (ParseException e)
		{
			e.printStackTrace();
		}	
	}
	
	/** Increment the number of tasks */
	public synchronized void incrementTasks()
	{
		numTasks++;
	}
	
	/** Decrement the number of tasks. 
	 * Call notifyAll() if no pending work left.  
	 */
	public synchronized void decrementTasks()
	{
		numTasks--;
		if (numTasks <= 0)
			notifyAll();
	}
	
	/** 
	 * Wait until there is no pending work, then shutdown the queue
	 */
	public synchronized void shutdown()
	{
		waitUntilFinished();
		queue.shutdown();
		queue.awaitTermination();
	}
	
	/**
	 *  Wait for all pending work to finish
	 */
	public synchronized void waitUntilFinished() {
		while (numTasks > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				logger.warn("Got interrupted while waiting for pending work to finish, ", e);
			}
		}
	}
	
	/**
	 * Call the printToFile method of ThreadSafeHotelData to print all the hotels' information
	 * @param filename
	 *            - Path specifying where to save the output.
	 */
	public synchronized void printToFile(Path filename)
	{
		waitUntilFinished();
		hoteldata.printToFile(filename);
	}
	
	/**
	 * Fetch all the attractions nearby all hotels in certain radius
	 * Save the message in json file which get from google. The filename
	 * of these json file are hotelId + result.json.
	 * 
	 * @param radiusInMiles
	 *            The radius which will be used in GET request.
	 */
	public void fetchAttractions(int radiusInMiles)
	{
		ArrayList<String> arr = hoteldata.generateQueries(radiusInMiles * 1609);
		String host = "maps.googleapis.com";
		for (int i = 0; i < arr.size(); i++)
		{
			String hotelId = arr.get(i).split("#")[0];
			String pathAndResources = arr.get(i).replace(hotelId+"#", "");
			String result = WebClientSSL.fetch(host, pathAndResources);
			try{
				FileWriter fw = new FileWriter(hotelId + "result.json");
				fw.write(result);
				fw.flush();
				fw.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			parseAttractions(hotelId + "result.json", hotelId);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

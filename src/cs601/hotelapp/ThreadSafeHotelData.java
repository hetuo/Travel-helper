package cs601.hotelapp;

import java.nio.file.Path;
import java.util.*;

//Added by TuoHe
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import cs601.concurrent.*;

/**
 * Class HotelData - a data structure that stores information about hotels and
 * hotel reviews. Allows to quickly lookup a hotel given the hotel id. 
 * Allows to easily find hotel reviews for a given hotel, given the hotelID. 
 * Reviews for a given hotel id are sorted by the date and user nickname.
 *
 */
 
 //TODO: coding style: indentation
public class ThreadSafeHotelData{

	// FILL IN CODE - declare data structures to store hotel data
	
	//Use HashMap to store all of the hotel data.
	//Added by TuoHe
	private Map<String, Hotel> hotelDataMap;
	private Map<String, TreeSet<Review>> reviewMap;
	private Map<String, TouristAttraction> attractionMap;
	private Map<String, ArrayList<TouristAttraction>>hotelAttraction;
	private ReentrantReadWriteLock lockOfHotelMap;
	private ReentrantReadWriteLock lockOfReviewMap;
	private ReentrantReadWriteLock lockOfAttractionMap;
	private ReentrantReadWriteLock lockOfHotelAttraction;
	
	//all instance variables should be private
	public static final int DATE_LEGALLENGTH = 10;
	public static final int FIRST_CHARINDEX = 4;
	public static final int SECOND_CHARINDEX = 7;
	

	/**
	 * Default constructor.
	 */
	public ThreadSafeHotelData() {
		// Initialize all data structures
		// FILL IN CODE
		
		/** Initialize the HashMap which type of key is String and type of value is Hotel.
		 *  Added by TuoHe
		 */
		hotelDataMap = new TreeMap<String, Hotel>();
		reviewMap = new TreeMap<String, TreeSet<Review>>();
		attractionMap = new TreeMap<String, TouristAttraction>();
		hotelAttraction = new TreeMap<String, ArrayList<TouristAttraction>>();
		lockOfHotelMap = new ReentrantReadWriteLock();
		lockOfReviewMap = new ReentrantReadWriteLock();
		lockOfAttractionMap = new ReentrantReadWriteLock();
		lockOfHotelAttraction = new ReentrantReadWriteLock();

	}

	/**
	 * Create a Hotel given the parameters, and add it to the appropriate data
	 * structure(s).
	 * 
	 * @param hotelId
	 *            - the id of the hotel
	 * @param hotelName
	 *            - the name of the hotel
	 * @param city
	 *            - the city where the hotel is located
	 * @param state
	 *            - the state where the hotel is located.
	 * @param streetAddress
	 *            - the building number and the street
	 * @param lat
	 * 			  - the latitude of this hotel
	 * @param lon
	 * 			  - the longitude of this hotel
	 */
	public void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress, double lat,
			double lon, String country) {
		// FILL IN CODE
		
		//Added by TuoHe - To be continued
		lockOfHotelMap.lockWrite();
		try{
			Address add = new Address(streetAddress, city, state, lon, lat);
			Hotel hotel = new Hotel(hotelId, hotelName, add, country);		
			hotelDataMap.put(hotelId, hotel);
		}
		finally{
			//System.out.println("11");
			lockOfHotelMap.unlockWrite();
		}
	}

	/**
	 * Add a new attraction.
	 * 
	 * @param attractionId
	 *            - the id of this attraction
	 * @param name
	 *            - the name of this attraction
	 * @param address
	 *            - the address of this attraction
	 * @param rating
	 *            - the rating of this attraction
	 * @param hotelId
	 *            - the hotel near this attraction
	 */		
	public void addAttraction(String attractionId, String name, String address, Number rating, String hotelId)
	{
		TouristAttraction attraction = new TouristAttraction(attractionId, name, address, rating);
		
		lockOfAttractionMap.lockWrite();
		try{
			attractionMap.put(attractionId, attraction);
		}
		finally{
			lockOfAttractionMap.unlockWrite();
		}
		
		lockOfHotelAttraction.lockWrite();
		try{
			ArrayList<TouristAttraction> arr = hotelAttraction.get(hotelId);
			if (null == arr)
			{			
				arr = new ArrayList<TouristAttraction>();
				arr.add(attraction);
				hotelAttraction.put(hotelId, arr);			
			}
			else
				arr.add(attraction);
		}
		finally
		{
			lockOfHotelAttraction.unlockWrite();
		}
	}
	
	/**
	 * Add a new review.
	 * 
	 * @param hotelId
	 *            - the id of the hotel reviewed
	 * @param reviewId
	 *            - the id of the review
	 * @param rating
	 *            - integer rating 1-5.
	 * @param reviewTitle
	 *            - the title of the review
	 * @param review
	 *            - text of the review
	 * @param isRecom
	 *            - whether the user recommends it or not
	 * @param date
	 *            - date of the review in the format yyyy-MM-dd, e.g.
	 *            2016-08-29.
	 * @param username
	 *            - the nickname of the user writing the review.
	 * @return true if successful, false if unsuccessful because of invalid date
	 *         or rating. Needs to catch and handle ParseException if the date is invalid.
	 *         Needs to check whether the rating is in the correct range
	 * 
	 */
	public boolean addReview(String hotelId, String reviewId, int rating, String reviewTitle, String review,
			boolean isRecom, String date, String username){

		// FILL IN CODE
		if (rating < 1 || rating > 5)
		{
			System.out.println("Invalid rating: " + rating + "(rating between 1-5)!");
			return false;
		}
		if (!checkDate(date))
			return false;
		Review newReview = new Review(reviewId, hotelId, reviewTitle, review, username, date, isRecom, rating);	
		lockOfReviewMap.lockWrite();
		lockOfReviewMap.lockRead();
		try{
			TreeSet<Review> reviewSet = reviewMap.get(hotelId);
			if (null == reviewSet)
			{
				reviewSet = new TreeSet<Review>();
				reviewSet.add(newReview);
				reviewMap.put(hotelId, reviewSet);
				return true;
			}
			else
			{
				return reviewSet.add(newReview);
			}
		}
		finally
		{
			//System.out.println("22");
			lockOfReviewMap.unlockRead();
			lockOfReviewMap.unlockWrite();
		}
	}

	/**
	 * Add a new view
	 * 
	 * @param newReview
	 *            the new review need to add
	 * @return
	 * 		false  failed to add this review.
	 * 		true   successfully to add this review.
	 */	
	public boolean addReview(Review newReview){
	
		lockOfReviewMap.lockWrite();
		lockOfReviewMap.lockRead();
		try{
			TreeSet<Review> reviewSet = reviewMap.get(newReview.getHotelId());
			if (null == reviewSet)
			{
				reviewSet = new TreeSet<Review>();
				reviewSet.add(newReview);
				reviewMap.put(newReview.getHotelId(), reviewSet);
				return true;
			}
			else
			{
				return reviewSet.add(newReview);
			}
		}
		finally
		{
			//System.out.println("22");
			lockOfReviewMap.unlockRead();
			lockOfReviewMap.unlockWrite();
		}
	}	
	
	/**
	 * Check Date's validity. The date must in format: yyyy-MM-dd
	 * 
	 * @param date
	 *            the date we want to check
	 * @return
	 * 		false  the date is invalid
	 * 		true   the date is valid
	 */
	public boolean checkDate(String date)
	{
		if (date.length() < DATE_LEGALLENGTH)
			return false;

		for (int i =0; i < date.length(); i++)
		{
			if (FIRST_CHARINDEX == i || SECOND_CHARINDEX == i)
			{
				if ('-' != date.charAt(i))
				{
					
					System.out.println(date.charAt(i) + "1---Invalid date: " +date+ "(date should in format yyyy-MM-ddTHH:mm:ssZ)");
					return false;
				}
			}			
			else if (date.charAt(i) < '0' || date.charAt(i) > '9')
			{
				System.out.println(date.charAt(i) + "5---Invalid date: " +date+ "(date should in format yyyy-MM-ddTHH:mm:ssZ)");
				return false;						
			}			
		}
		
		SimpleDateFormat currDate = new SimpleDateFormat("yyyy-MM-dd");
		if (date.compareTo(currDate.format(new Date())) >= 0)
		{
			System.out.println("Invalid date: " +date+ "(It's impossible that date greater than current time)");
			return false;				
		}
		
		return true;		
	}

	/**
	 * Return an alphabetized list of the ids of all hotels
	 * 
	 * @return
	 * 		The list of hotels
	 */
	public List<String> getHotels() {
		// FILL IN CODE
		lockOfHotelMap.lockRead();
		try{
			LinkedList<String> idList = new LinkedList<String>();       
			for (Object key : hotelDataMap.keySet())
				idList.add((String)key);			
			return idList; // don't forget to change it
		}
		finally{
			lockOfHotelMap.unlockRead();
		}
	}

	/**
	 * Return an hotel object according to the hotelId
	 * 
	 * @return
	 * 		Hotel object
	 */
	public Hotel getHotel(String hotelId){
		lockOfHotelMap.lockRead();
		try{
			Hotel hotelInMap = hotelDataMap.get(hotelId);
			if (hotelInMap == null)
				return null;
			else{
				Hotel hotelToReturn = new Hotel(hotelInMap.getHotelId(), hotelInMap.getHotelName(), 
						hotelInMap.getHotelAddress(), hotelInMap.getCountry());
				return hotelToReturn;
			}
		}finally
		{
			lockOfHotelMap.unlockRead();
		}
		
	}
	
	/**
	 * Return a list of reviews according to the hotelId and number
	 * 
	 * @return
	 * 		The list of reviews
	 */
	public ArrayList<Review> getReviews(String hotelId, int num)
	{
		lockOfReviewMap.lockRead();
		try{
			System.out.println("HT: enter getReviews");
			TreeSet<Review> set = reviewMap.get(hotelId);
			if (null == set || num > set.size())
				return null;
			
			ArrayList<Review> reviews = new ArrayList<Review>();
			Iterator<Review> i = set.iterator();
			int times = 0;
			while(times < num)
			{
				Review reviewInMap = i.next();
				Review reviewToreturn = new Review(reviewInMap.getReviewId(), reviewInMap.getHotelId(), 
						reviewInMap.getReviewTitle(), reviewInMap.getReviewText(), reviewInMap.getReviewUsername()
						, reviewInMap.getReviewDate(), reviewInMap.getRecom(), reviewInMap.getRating());
				reviews.add(reviewToreturn);
				times += 1;
			}
			System.out.println("HT: " + reviews.size());
			return reviews;
		}catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("HT: exception");
			return null;
		}
		finally
		{
			lockOfReviewMap.unlockRead();
			System.out.println("HT: Leave getReviews");
		}
		
	}
	
	/**
	 * Return a string of all the attractions' information near this hotel
	 * 
	 * @return
	 * 		the attractions near this hotel
	 */
	public String getAttractions(String hotelid)
	{
		lockOfHotelMap.lockRead();
		lockOfHotelAttraction.lockRead();
		try{
			ArrayList<TouristAttraction> arr = hotelAttraction.get(hotelid);
			if (null == arr)
				return null;
			StringBuffer sb = new StringBuffer();
			Hotel hotel = hotelDataMap.get(hotelid);
			if (null == hotel)
				return null;
			sb.append("Attractions near " + hotel.getHotelName() + ", " + hotelid + System.lineSeparator());
			for (int i = 0; i < arr.size(); i++)
			{
				sb.append(arr.get(i).getName());
				sb.append("; ");
				sb.append(arr.get(i).getAdderss());
				sb.append(System.lineSeparator());
			}
			return sb.toString();
		}
		finally
		{
			lockOfHotelMap.unlockRead();
			lockOfHotelAttraction.unlockRead();
		}				
	}
	
	
	/**
	 * Copy all informations in review map to another map.
	 * 
	 * @param dataTo
	 * 		the map we copy to
	 */
	public void mergeReviewData(ThreadSafeHotelData dataTo)
	{
		lockOfReviewMap.lockRead();
		try{
			for (String key : reviewMap.keySet())
			{
				TreeSet<Review> reviewSet = reviewMap.get(key);
				Iterator<Review> iterator = reviewSet.iterator();
				while (iterator.hasNext())
					dataTo.addReview(iterator.next());
			}
		}
		finally{
			lockOfReviewMap.unlockRead();
		}
	}
	
	
	/**
	 * Returns a string representing information about the hotel with the given
	 * id, including all the reviews for this hotel separated by
	 * -------------------- Format of the string: HoteName: hotelId
	 * streetAddress city, state -------------------- Review by username: rating
	 * ReviewTitle ReviewText -------------------- Review by username: rating
	 * ReviewTitle ReviewText ...
	 * 
	 * @param hotelId
	 *            -the hotel id
	 * @return - output string.
	 */
	public String toString(String hotelId) {

		// FILL IN CODE
		
		StringBuilder hotelInfor = new StringBuilder();
		lockOfHotelMap.lockRead();
		lockOfReviewMap.lockRead();
		try{
			Hotel hotel = (Hotel)hotelDataMap.get(hotelId);
			if (null == hotel)
			{
				System.out.println("No such that hotel");
				return "";
			}
			TreeSet<Review> reviewSet = reviewMap.get(hotelId);
			hotelInfor.append(hotel.getHotelName() + ": " + hotelId + "\n");
			hotelInfor.append(hotel.getHotelAddress().getStreet() + "\n");
			hotelInfor.append(hotel.getHotelAddress().getCity() + ", " + hotel.getHotelAddress().getState() + "\n");
	
			if (null == reviewSet)
				return hotelInfor.toString();
			Review review = null;
			for (Iterator<Review> ite = reviewSet.iterator(); ite.hasNext();)
			{
				review = ite.next();
				hotelInfor.append("--------------------\n");
				hotelInfor.append("Review by " + review.getReviewUsername() + ": " + review.getRating() + "\n");
				hotelInfor.append(review.getReviewTitle() + "\n");
				hotelInfor.append(review.getReviewText() + "\n");
			}
			return hotelInfor.toString(); // don't forget to change to the correct string
		}
		finally
		{
			//System.out.println("33");
			lockOfReviewMap.unlockRead();
			lockOfHotelMap.unlockRead();
		}
	}

	/**
	 * Save the string representation of the hotel data to the file specified by
	 * filename in the following format: 
	 * an empty line 
	 * A line of 20 asterisks ******************** on the next line 
	 * information for each hotel, printed in the format described in the toString method of this class.
	 * 
	 * @param filename
	 *            - Path specifying where to save the output.
	 */
	public void printToFile(Path filename) {
		// FILL IN CODE
		
		lockOfHotelMap.lockRead();
		try
		{
			FileWriter fw = new FileWriter(filename.toString());
			for (Object key : hotelDataMap.keySet())
			{
				fw.write("\n********************\n");
				fw.write(toString((String)key));
			}
			fw.close();
		
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lockOfHotelMap.unlockRead();
		}

	}
	
	/**
	 * Save the string representation of the attractions near each hotel data to the file specified by
	 * filename 
	 * 
	 * @param filename
	 *            - Path specifying where to save the output.
	 */
	public void printAttractionsNearEachHotel(Path filename)
	{
		FileWriter fw = null;
		lockOfHotelAttraction.lockRead();
		try {			
			fw = new FileWriter(filename.toString());
			for (String key : hotelAttraction.keySet())
			{
				fw.write(getAttractions(key));
				fw.write("++++++++++++++++++++" + System.lineSeparator());				
			}
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			lockOfHotelAttraction.unlockRead();
			if (null != fw)
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	
	/**
	 * Iterate the hotel map and generate the queries for each hotel.
	 * Other method will get these queries and then request information
	 * from google
	 * 
	 * @param meters
	 *            - The necessary signature when use google's api service
	 * @return
	 * 		The queries of all the hotel.       
	 */
	public ArrayList<String> generateQueries(int meters)
	{
		lockOfHotelMap.lockRead();
		try{
			ArrayList<String> arr = new ArrayList<String>();
			for (String key : hotelDataMap.keySet())
			{
				StringBuffer sb = new StringBuffer();
				Address addr = hotelDataMap.get(key).getHotelAddress();
				String city = addr.getCity().replaceAll(" ", "%20");
				sb.append(hotelDataMap.get(key).getHotelId() + "#");
				sb.append("/maps/api/place/textsearch/json?query=tourist%20attractions+in+");
				sb.append(city);
				sb.append("&location=");
				sb.append(addr.getLatitude() + "," + addr.getLongitude() + "&radius=" + meters);
				sb.append("&key=AIzaSyC7mq0PWhPlvvxPTYIN-E_RMiOMnA4Dw4M");
				arr.add(sb.toString());
			}
			return arr;
		}
		finally
		{
			lockOfHotelMap.unlockRead();
		}
	}	
	
/*	public Map<String, Hotel> getHotelMap()
	{
		return hotelDataMap;
	}
	public Map<String, TreeSet<Review>> getReviewMap()
	{
		return reviewMap;
	}*/
	
}

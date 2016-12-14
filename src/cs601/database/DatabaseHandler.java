package cs601.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cs601.hotelapp.HotelWithRating;
import cs601.hotelapp.Address;
import cs601.hotelapp.Hotel;
import cs601.hotelapp.HotelDetails;
import cs601.hotelapp.Review;
import cs601.hotelapp.ThreadSafeHotelData;

/**
 * Handles all database-related actions. Uses singleton design pattern. Modified
 * by Prof. Karpenko from the original example of Prof. Engle.
 * 
 * @see RegisterServer
 */
public class DatabaseHandler {

	/** Makes sure only one database handler is instantiated. */
	private static DatabaseHandler singleton = new DatabaseHandler();
	private static final String UPDATELOGINDATE_SQL = "update users set date = ? where username = ?;";
	private static final String GETLASTLOGINTIME_SQL = "select date from users where username = ?;";
	private static final String GETHOTELBYNAME_SQL = "select id from hotel where name = ?;";
	private static final String UPDATEREVIEW_SQL = "update review set title = ?, text = ?, date = ?, rating = ?, recom = ? where reviewid = ?;";
	private static final String GETMAPINFO_SQL = "select longitude, latitude from hotel where id = ?;";
	private static final String DELETEREVIEW_SQL = "delete from review where reviewId = ?;";
	private static final String GETREVIEWBYID_SQL = "select * from review where reviewId = ?;";
	private static final String GETUSERREVIEW_SQL = "select review.*, hotel.name from review join hotel on review.hotelid = hotel.id where username = ?;";
    //private static final String GETHOTELDETAILS_SQL = "select hotel.*, review.* from hotel left join review"
    //		+ " on hotel.id = review.hotelid where hotel.id = ?;";
	private static final String GETHOTELDETAILS_SQL = "select * from hotel where id = ?";
    private static final String GETHOTEL_SQL = "select city, longitude, latitude from hotel where id = ?;";
	/** Used to determine if login_users table exists. */
	private static final String TABLES_SQL = "SHOW TABLES LIKE 'login_users';";
	private static final String UPDATEREVIEWLIKES_SQL = "update review set likes = ? where reviewid = ?;";
	private static final String GETREVIEWLIKES_SQL = "select likes from review where reviewid = ?;";
	private static final String ADD_REVIEW_SQL = "insert into review (reviewid, hotelid, title, "
			+ "text, username, date, recom, rating) values(?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String LOGIN_SQL = "select passwd, usersalt from users where username = ?;";
	private static final String VIEWHOTEL_SQL = "select hotel.*, avg(rating) as rate from hotel"
			+ " left join review on hotel.id = review.hotelid group by hotel.id;";
	private static final String VIEWREVIEW_SQL = "select * from review where hotelid = ?;";
	private static final String VIEWREVIEW_SQL_DATE = "select * from review where hotelid = ? order by date desc;";	
	private static final String VIEWREVIEW_SQL_RATING = "select * from review where hotelid = ? order by rating desc;";
	/** Used to create login_users table for this example. */
	private static final String CREATE_SQL = "CREATE TABLE login_users ("
			+ "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " + "username VARCHAR(32) NOT NULL UNIQUE, "
			+ "password CHAR(64) NOT NULL, " + "usersalt CHAR(32) NOT NULL);";

	/** Used to insert a new user's info into the login_users table */
	private static final String REGISTER_SQL = "INSERT INTO users (username, passwd, usersalt) "
			+ "VALUES (?, ?, ?);";
	/** Used to store all tables in json to database.*/
	private static final String CREATE_HOTEL_TABLE_SQL = "insert into hotel (id, name, street, city, state, "
			+ "country, longitude, latitude) values(?, ?, ?, ?, ?, ?, ?, ?)";
	/** Used to store all reviews in json to database.*/
	private static final String CREATE_REVIEW_TABLE_SQL = "insert into review (reviewid, hotelid, title, "
			+ "text, username, date, recom, rating) values(?, ?, ?, ?, ?, ?, ?, ?)";
	/** Used to store all users in json to database.*/
	private static final String CREATE_USER_TABLE_SQL = "insert into users(username, passwd, usersalt)"
			+ "values(?, ?, ?);";
	
	/** Used to determine if a username already exists. */
	private static final String USER_SQL = "SELECT username FROM users WHERE username = ?";

	// ------------------ constants below will be useful for the login operation
	// once you implement it
	/** Used to retrieve the salt associated with a specific user. */
	private static final String SALT_SQL = "SELECT usersalt FROM login_users WHERE username = ?";

	/** Used to authenticate a user. */
	private static final String AUTH_SQL = "SELECT username FROM login_users " + "WHERE username = ? AND password = ?";

	/** Used to remove a user from the database. */
	private static final String DELETE_SQL = "DELETE FROM login_users WHERE username = ?";

	/** Used to configure connection to database. */
	private DatabaseConnector db;

	/** Used to generate password hash salt for user. */
	private Random random;

	/**
	 * This class is a singleton, so the constructor is private. Other classes
	 * need to call getInstance()
	 */
	private DatabaseHandler() {
		Status status = Status.OK;
		random = new Random(System.currentTimeMillis());

		try {
			db = new DatabaseConnector("database.properties");
			status = db.testConnection() ? setupTables() : Status.CONNECTION_FAILED;
		} catch (FileNotFoundException e) {
			status = Status.MISSING_CONFIG;
		} catch (IOException e) {
			e.printStackTrace();
			status = Status.MISSING_VALUES;
		}

		if (status != Status.OK) {
			System.out.println("Error while obtaining a connection to the database: " + status);
		}
	}

	/**
	 * Gets the single instance of the database handler.
	 *
	 * @return instance of the database handler
	 */
	public static DatabaseHandler getInstance() {
		return singleton;
	}

	/**
	 * Checks to see if a String is null or empty.
	 * 
	 * @param text
	 *            - String to check
	 * @return true if non-null and non-empty
	 */
	public static boolean isBlank(String text) {
		return (text == null) || text.trim().isEmpty();
	}

	/**
	 * Checks if necessary table exists in database, and if not tries to create
	 * it.
	 *
	 * @return {@link Status.OK} if table exists or create is successful
	 */
	private Status setupTables() {
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection(); Statement statement = connection.createStatement();) {
			if (!statement.executeQuery(TABLES_SQL).next()) {
				// Table missing, must create
				statement.executeUpdate(CREATE_SQL);

				// Check if create was successful
				if (!statement.executeQuery(TABLES_SQL).next()) {
					status = Status.CREATE_FAILED;
				} else {
					status = Status.OK;
				}
			} else {
				status = Status.OK;
			}
		} catch (Exception ex) {
			status = Status.CREATE_FAILED;
		}

		return status;
	}

	/**
	 * Tests if a user already exists in the database. Requires an active
	 * database connection.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - username to check
	 * @return Status.OK if user does not exist in database
	 * @throws SQLException
	 */
	private Status duplicateUser(Connection connection, String user) {

		assert connection != null;
		assert user != null;

		Status status = Status.ERROR;

		try (PreparedStatement statement = connection.prepareStatement(USER_SQL);) {
			statement.setString(1, user);

			ResultSet results = statement.executeQuery();
			status = results.next() ? Status.DUPLICATE_USER : Status.OK;
		} catch (SQLException e) {
			status = Status.SQL_EXCEPTION;
			System.out.println("Exception occured while processing SQL statement:" + e);
		}

		return status;
	}

	/**
	 * Returns the hex encoding of a byte array.
	 *
	 * @param bytes
	 *            - byte array to encode
	 * @param length
	 *            - desired length of encoding
	 * @return hex encoded byte array
	 */
	public static String encodeHex(byte[] bytes, int length) {
		BigInteger bigint = new BigInteger(1, bytes);
		String hex = String.format("%0" + length + "X", bigint);

		assert hex.length() == length;
		return hex;
	}

	/**
	 * Calculates the hash of a password and salt using SHA-256.
	 *
	 * @param password
	 *            - password to hash
	 * @param salt
	 *            - salt associated with user
	 * @return hashed password
	 */
	public static String getHash(String password, String salt) {
		String salted = salt + password;
		String hashed = salted;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salted.getBytes());
			hashed = encodeHex(md.digest(), 64);
		} catch (Exception ex) {
			System.out.println("Unable to properly hash password." + ex);
		}

		return hashed;
	}
	
	
	
	/**
	 * Add a new review to the database
	 * @param username
	 * 		user's name
	 * @param hotelid
	 * 		hotel's id
	 * @param title
	 * 		review's title
	 * @param text
	 * 		review's content
	 * @param rating
	 * 		review's rating
	 * @param recom
	 * 		review's recommendation
	 * @return
	 * 		the result of adding this review to database
	 */
	public Status addReview(String username, String hotelid, String title, String text, String rating, String recom)
	{
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(ADD_REVIEW_SQL);){
				byte[] saltBytes = new byte[12];
				random.nextBytes(saltBytes);
				String reviewid = encodeHex(saltBytes, 24);				
				statement.setString(1, reviewid);
				//statement2.setInt(2, userid);
				statement.setString(2, hotelid);
				statement.setString(3, title);
				statement.setString(4, text);
				statement.setString(5, username);
				SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
				statement.setString(6, df.format(new Date()));
				if (recom.equals("yes"))
					statement.setBoolean(7, true);
				else
					statement.setBoolean(7, false);
				statement.setInt(8, Integer.parseInt(rating));
				statement.executeUpdate();
				status = Status.OK;
			}
			
		}catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}
		return status;
	}
	
	/**
	 * search all reviews information in database for the web request
	 * @param list
	 * 		data structure to store hotels information
	 * @param hotelid
	 * 		the id of hotel which from web's request
	 * @return
	 * 		The result of search all hotels in database
	 */
	public Status viewReview(String hotelid, ArrayList<Review> list, String type)
	{
		Status status = Status.ERROR;
		String sql = null;
		if (type.equals("normal"))
			sql = VIEWREVIEW_SQL;
		else if (type.equals("date"))
			sql = VIEWREVIEW_SQL_DATE;
		else
			sql = VIEWREVIEW_SQL_RATING;
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(sql);){
				statement.setString(1, hotelid);
				System.out.println(hotelid);
				ResultSet rs = statement.executeQuery();
				/*if (rs.next())
					System.out.println("xxxxxxxxxxxxxxxxxxxxxxx");*/
				while (rs.next())
				{
					Review review = new Review(rs.getString(1), rs.getString(2), rs.getString(3), 
							rs.getString(4), rs.getString(5), rs.getString(6), rs.getBoolean(7), rs.getInt(8), rs.getInt(9));
					list.add(review);
				}
				//	System.out.println("XX" + rs.getString(9));
				//sr.setResultSet(rs);
				status = Status.OK;
			}
			
		}catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}
		return status;
	}
	
	public Status getUserReview(String name, ArrayList<Review> list)
	{
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(GETUSERREVIEW_SQL);){
				statement.setString(1, name);
				//System.out.println(hotelid);
				ResultSet rs = statement.executeQuery();
				/*if (rs.next())
					System.out.println("xxxxxxxxxxxxxxxxxxxxxxx");*/
				while (rs.next())
				{
					Review review = new Review(rs.getString(1), rs.getString(10), rs.getString(3), 
							rs.getString(4), rs.getString(5), rs.getString(6), rs.getBoolean(7), rs.getInt(8), rs.getInt(9));
					list.add(review);
				}
				status = Status.OK;
			}
			
		}catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}
		return status;		
	}
	
	public Review getReviewById(String reviewId)
	{
		Review review = null;

		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(GETREVIEWBYID_SQL);){
				statement.setString(1, reviewId);
				//System.out.println(hotelid);
				ResultSet rs = statement.executeQuery();
				/*if (rs.next())
					System.out.println("xxxxxxxxxxxxxxxxxxxxxxx");*/
				while (rs.next())
				{
					review = new Review(rs.getString(1), rs.getString(2), rs.getString(3), 
							rs.getString(4), rs.getString(5), rs.getString(6), rs.getBoolean(7), rs.getInt(8), rs.getInt(9));

				}

			}
			
		}catch (SQLException ex) {
			
			System.out.println("Error while connecting to the database: " + ex);
		}
		return review;		
	}
	
	public void updateReview(String reviewId, String title, String text, String rating, String recom)
	{
		
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(UPDATEREVIEW_SQL);){
			
				statement.setString(1, title);

				statement.setString(2, text);

				SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
				statement.setString(3, df.format(new Date()));
				statement.setInt(4, Integer.parseInt(rating));
				if (recom.equals("yes"))
					statement.setBoolean(5, true);
				else
					statement.setBoolean(5, false);
				statement.setString(6, reviewId);
				statement.executeUpdate();

			}
			
		}catch (SQLException ex) {
			
			System.out.println("Error while connecting to the database: " + ex);
		}

	}
	
	public String getReviewLikes(String reviewId)
	{
		String result = null;
		int likes = 0;
		try (Connection connection = db.getConnection();)
		{
			try (PreparedStatement statement = connection.prepareStatement(GETREVIEWLIKES_SQL);)
			{
				statement.setString(1, reviewId);
				ResultSet rs = statement.executeQuery();
				if (rs.next())
					likes = rs.getInt(1) + 1;		
			}
			try (PreparedStatement statement = connection.prepareStatement(UPDATEREVIEWLIKES_SQL);)
			{
				statement.setString(2, reviewId);
				statement.setInt(1, likes);
				statement.executeUpdate();		
			}
		}	
		catch (SQLException e)
		{
			e.printStackTrace();
		}	
		result = "{\"likes\":"+likes+"}";
		return result;
	}
	
	public String getHotelByName(String name)
	{
		String result = null;
		try (Connection connection = db.getConnection();)
		{
			try (PreparedStatement statement = connection.prepareStatement(GETHOTELBYNAME_SQL);)
			{
				statement.setString(1, name);
				ResultSet rs = statement.executeQuery();
				if (rs.next())
					result = rs.getString(1);		
			}
		}	
		catch (SQLException e)
		{
			e.printStackTrace();
		}	
		return result;
	}
	
	public String getMapInformation(String hotelid)
	{
		String mapInfo = null;
		try (Connection connection = db.getConnection();)
		{
			try (PreparedStatement statement = connection.prepareStatement(GETMAPINFO_SQL);)
			{
				statement.setString(1, hotelid);
				ResultSet rs = statement.executeQuery();
				if (rs.next())
					mapInfo = String.valueOf(rs.getDouble(1)) + ":" + String.valueOf(rs.getDouble(2));			
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return mapInfo;
	}
	
	public void deleteReview(String reviewId)
	{
		try (Connection connection = db.getConnection();)
		{
			try (PreparedStatement statement = connection.prepareStatement(DELETEREVIEW_SQL);)
			{
				statement.setString(1, reviewId);
				statement.executeUpdate();		
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}		
	}
	
	public String getHotelAddr(String hotelid)
	{	
		String addr = null;
		try (Connection connection = db.getConnection();)
		{
			try (PreparedStatement statement = connection.prepareStatement(GETHOTEL_SQL);)
			{
				statement.setString(1, hotelid);
				ResultSet rs = statement.executeQuery();
				if (rs.next())
					addr = rs.getString(1) +":"+String.valueOf(rs.getDouble(2)) + ":" + String.valueOf(rs.getDouble(3));			
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return addr;
	}
	
	public Status getHotelDetails(String hotelid, HotelWithRating details)
	{
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();)
		{
			try (PreparedStatement statement = connection.prepareStatement(GETHOTELDETAILS_SQL);)
			{
				statement.setString(1, hotelid);
				ResultSet rs = statement.executeQuery();
				ArrayList<Review> reviews = new ArrayList<Review>();
				if (rs.next())
				{
					if (details.getHotel() == null)
					{
						Hotel hotel = new Hotel(rs.getString(1), rs.getString(2), new Address(rs.getString(3), 
								rs.getString(4), rs.getString(5), rs.getDouble(7), rs.getDouble(8)), rs.getString(6));
						details.setHotel(hotel);
					}
				}
				/*while (rs.next())
				{
					if (details.getHotel() == null)
					{
						Hotel hotel = new Hotel(rs.getString(1), rs.getString(2), new Address(rs.getString(3), 
								rs.getString(4), rs.getString(5), rs.getDouble(7), rs.getDouble(8)), rs.getString(6));
						details.setHotel(hotel);
					}					
					Review review = new Review(rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), 
							rs.getString(13), rs.getString(14), rs.getBoolean(15), rs.getInt(16), rs.getInt(17));
					reviews.add(review);
				}
				details.setReviews(reviews);*/
				status = Status.OK;
			}
		}
		catch (SQLException e)
		{
			status = Status.CONNECTION_FAILED;
			e.printStackTrace();
		}
		return status;
	}
	
	/**
	 * search all hotels information in database for the web request
	 * @param list
	 * 		data structure to store hotels information
	 * @return
	 * 		The result of search all hotels in database
	 */
	public Status viewHotel(TreeSet<HotelWithRating> list)
	{
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(VIEWHOTEL_SQL);){
				ResultSet rs = statement.executeQuery();
				while (rs.next())
				{
					Hotel hotel = new Hotel(rs.getString(1), rs.getString(2), new Address(rs.getString(3),
							 rs.getString(4), rs.getString(5), rs.getDouble(7), rs.getDouble(8)), 
							 rs.getString(6));
					list.add(new HotelWithRating(hotel,rs.getFloat(10)));
				}
				status = Status.OK;
			}
			
		}catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}
		return status;
	}
	
	
	/**
	 * user login, make sure the user name and password are correct.
	 *
	 * @param user
	 *            - user name of user
	 * @param passwd
	 *            - password of user
	 * @return {@link Status.OK} if login successful
	 */
	public Status userLogin(String user, String passwd, ArrayList<String> list)
	{
		Status status = Status.ERROR;
		if (isBlank(user) || isBlank(passwd)) {
			status = Status.INVALID_LOGIN;
			System.out.println("Invalid regiser info");
			return status;
		}
		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			status = duplicateUser(connection, user);
			if (status == Status.DUPLICATE_USER) {
				try (PreparedStatement statement = connection.prepareStatement(LOGIN_SQL);){
					//statement.setString(1, user);
					statement.setNCharacterStream(1, new StringReader(user), user.length());
					ResultSet rs = statement.executeQuery();
					String passwdInDatabase = null;
					String saltInDatabase = null;
					if (rs.next()){
					passwdInDatabase = rs.getString(1);
					saltInDatabase = rs.getString(2);}
					if (getHash(passwd, saltInDatabase).equals(passwdInDatabase))
						status = Status.OK;
					else
						status = Status.INVALID_LOGIN;
					
				}
				
				try (PreparedStatement statement = connection.prepareStatement(GETLASTLOGINTIME_SQL);){
					//statement.setString(1, user);
					statement.setString(1, user);
					ResultSet rs = statement.executeQuery();
					String lastDate = null;
					System.out.println(lastDate);
					if (rs.next()){
					lastDate = rs.getString(1);
					}
					list.add(lastDate);				
				}
				
				try (PreparedStatement statement = connection.prepareStatement(UPDATELOGINDATE_SQL);){
					//statement.setString(1, user);
					SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
					statement.setString(1, df.format(new Date()));
						
					statement.setString(2, user);
					statement.executeUpdate();					
				}
				
			}
			else if (status == Status.OK)
				return Status.INVALID_LOGIN;
		}catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}		
		return status;
	}
	
	
	
	

	/**
	 * Registers a new user, placing the username, password hash, and salt into
	 * the database if the username does not already exist.
	 *
	 * @param newuser
	 *            - username of new user
	 * @param newpass
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 */
	public Status registerUser(String newuser, String newpass) {
		Status status = Status.ERROR;
		System.out.println("Registering " + newuser + ".");
		// make sure we have non-null and non-emtpy values for login
		if (isBlank(newuser) || isBlank(newpass)) {
			status = Status.INVALID_LOGIN;
			System.out.println("Invalid regiser info");
			return status;
		}
		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			status = duplicateUser(connection, newuser);
			// if okay so far, try to insert new user
			if (status == Status.OK) {
				// generate salt
				byte[] saltBytes = new byte[16];
				random.nextBytes(saltBytes);
				String usersalt = encodeHex(saltBytes, 32); // hash salt
				String passhash = getHash(newpass, usersalt); 
				// add user info to the database table
				try (PreparedStatement statement = connection.prepareStatement(REGISTER_SQL);) {
					statement.setString(1, newuser);
					statement.setString(2, passhash);
					statement.setString(3, usersalt);
					statement.executeUpdate();

					status = Status.OK;
				}
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}
		return status;
	}

	
 	/**
 	 * Add all hotels in json to database
 	 * 
 	 * @param hdata The data structure that save the hotel information
 	 * @return
 	 * 		Is it performed successfully or not and the reason
 	 */
/*	
 	public Status createHotelTable(ThreadSafeHotelData hdata)
	{
		Status status = Status.ERROR;
		Map<String, Hotel> hotelMap = hdata.getHotelMap();
		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(CREATE_HOTEL_TABLE_SQL);) {
				for (String id : hotelMap.keySet())
				{
					System.out.println("ID: " + id);
					Hotel hotel = hotelMap.get(id);
					statement.setString(1, id);
					statement.setString(2, hotel.getHotelName());
					statement.setString(3, hotel.getHotelAddress().getStreet());
					statement.setString(4, hotel.getHotelAddress().getCity());
					statement.setString(5, hotel.getHotelAddress().getState());
					statement.setString(6, hotel.getCountry());
					statement.setDouble(7, hotel.getHotelAddress().getLongitude());
					statement.setDouble(8, hotel.getHotelAddress().getLatitude());
					statement.executeUpdate();
					System.out.println("OVER");
				}				
				status = Status.OK;
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}

		return status;
	}*/
	
 	/**
 	 * Add all reviews in json to database
 	 * 
 	 * @param hdata The data structure that save the hotel information
 	 * @return
 	 * 		Is it performed successfully or not and the reason
 	 */
/*	public Status createReviewTable(ThreadSafeHotelData hdata)
	{
		Status status = Status.ERROR;
		//Map<String, Hotel> hotelMap = hdata.getHotelMap();
		Map<String, TreeSet<Review>>reviewMap = hdata.getReviewMap();
		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(CREATE_REVIEW_TABLE_SQL);) {
				for (String id : reviewMap.keySet())
				{
					System.out.println("ID: " + id);
					TreeSet<Review> reviewSet = reviewMap.get(id);
					for (Review review : reviewSet)
					{
						statement.setString(1, review.getReviewId());
						statement.setString(2, review.getHotelId());
						statement.setString(3, review.getReviewTitle());
						statement.setString(4, review.getReviewText());
						statement.setString(5, review.getReviewUsername());
						statement.setString(6, review.getReviewDate());
						statement.setBoolean(7, review.getRecom());
						statement.setInt(8, review.getRating());
						statement.executeUpdate();
					}


					System.out.println("OVER");
				}				
				status = Status.OK;
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}

		return status;
	}
*/	
 	/**
 	 * Add all users in json to database
 	 * 
 	 * @param hdata The data structure that save the hotel information
 	 * @return
 	 * 		Is it performed successfully or not and the reason
 	 */
	/*public Status createUserTable(ThreadSafeHotelData hdata)
	{
		Status status = Status.ERROR;
		//Map<String, Hotel> hotelMap = hdata.getHotelMap();
		Map<String, TreeSet<Review>>reviewMap = hdata.getReviewMap();
		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(CREATE_USER_TABLE_SQL);) {
				for (String id : reviewMap.keySet())
				{
					System.out.println("ID: " + id);
					TreeSet<Review> reviewSet = reviewMap.get(id);
					for (Review review : reviewSet)
					{
						Status s = duplicateUser(connection, review.getReviewUsername());
						if (s == Status.DUPLICATE_USER)
							continue;
						if (s == Status.OK)
						{	
							statement.setString(1, review.getReviewUsername());
							byte[] saltBytes = new byte[16];
							random.nextBytes(saltBytes);
							String usersalt = encodeHex(saltBytes, 32); // hash salt
							String passhash = getHash("123456", usersalt);
							statement.setString(2, passhash);
							statement.setString(3, usersalt);
							statement.executeUpdate();
						}
					}


					System.out.println("OVER");
				}				
				status = Status.OK;
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}

		return status;
	}*/
	
	/**
	 * Gets the salt for a specific user.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException
	 *             if any issues with database connection
	 */
	private String getSalt(Connection connection, String user) throws SQLException {
		assert connection != null;
		assert user != null;

		String salt = null;

		try (PreparedStatement statement = connection.prepareStatement(SALT_SQL);) {
			statement.setString(1, user);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				salt = results.getString("usersalt");
			}
		}

		return salt;
	}
}

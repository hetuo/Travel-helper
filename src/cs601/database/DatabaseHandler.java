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

import cs601.Server.HotelWithRating;
import cs601.Server.SqlResult;
import cs601.hotelapp.Address;
import cs601.hotelapp.Hotel;
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

	/** Used to determine if login_users table exists. */
	private static final String TABLES_SQL = "SHOW TABLES LIKE 'login_users';";
	private static final String USER_ID_SQL = "select id from users where username = ?;";
	private static final String ADD_REVIEW_SQL = "insert into review (reviewid, userid, hotelid, title, "
			+ "text, username, date, recom, rating) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String LOGIN_SQL = "select passwd, usersalt from users where username = ?;";
	private static final String VIEWHOTEL_SQL = "select hotel.*, avg(rating) as rate from hotel"
			+ " left join review on hotel.id = review.hotelid group by hotel.id;";
	private static final String VIEWREVIEW_SQL = "select * from review where hotelid = ?;";
	/** Used to create login_users table for this example. */
	private static final String CREATE_SQL = "CREATE TABLE login_users ("
			+ "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " + "username VARCHAR(32) NOT NULL UNIQUE, "
			+ "password CHAR(64) NOT NULL, " + "usersalt CHAR(32) NOT NULL);";

	/** Used to insert a new user's info into the login_users table */
	private static final String REGISTER_SQL = "INSERT INTO users (username, passwd, usersalt) "
			+ "VALUES (?, ?, ?);";
	
	private static final String CREATE_HOTEL_TABLE_SQL = "insert into hotel (id, name, street, city, state, "
			+ "country, longitude, latitude) values(?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String CREATE_REVIEW_TABLE_SQL = "insert into review (reviewid, hotelid, title, "
			+ "text, username, date, recom, rating) values(?, ?, ?, ?, ?, ?, ?, ?)";
	
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
	
	public Status addReview(String username, String hotelid, String title, String text, String rating, String recom)
	{
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(USER_ID_SQL);){
				statement.setString(1, username);
				//System.out.println(hotelid);
				ResultSet rs = statement.executeQuery();
				int userid = 0;
				if (rs.next())
					userid = rs.getInt(1);
				byte[] saltBytes = new byte[12];
				random.nextBytes(saltBytes);
				String reviewid = encodeHex(saltBytes, 24);
				PreparedStatement statement2 = connection.prepareStatement(ADD_REVIEW_SQL);
				statement2.setString(1, reviewid);
				statement2.setInt(2, userid);
				statement2.setString(3, hotelid);
				statement2.setString(4, title);
				statement2.setString(5, text);
				statement2.setString(6, username);
				SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
				statement2.setString(7, df.format(new Date()));
				if (recom.equals("yes"))
					statement2.setBoolean(8, true);
				else
					statement2.setBoolean(8, false);
				statement2.setInt(9, Integer.parseInt(rating));
				statement2.executeUpdate();
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
	
	public Status viewReview(String hotelid, ArrayList<Review> list)
	{
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(VIEWREVIEW_SQL);){
				statement.setString(1, hotelid);
				System.out.println(hotelid);
				ResultSet rs = statement.executeQuery();
				/*if (rs.next())
					System.out.println("xxxxxxxxxxxxxxxxxxxxxxx");*/
				while (rs.next())
				{
					Review review = new Review(rs.getString(1), rs.getString(3), rs.getString(4), 
							rs.getString(5), rs.getString(6), rs.getString(7), rs.getBoolean(8), rs.getInt(9));
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
	
	public Status viewHotel(ArrayList<HotelWithRating> list)
	{
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(VIEWHOTEL_SQL);){
				//statement.setString(1, user);
				
				ResultSet rs = statement.executeQuery();
				//if (rs.next())
				//	System.out.println("xxxxxxxxxxxxxxxxxxxxxxx");
				while (rs.next())
				{
					Hotel hotel = new Hotel(rs.getString(1), rs.getString(2), new Address(rs.getString(3),
							 rs.getString(4), rs.getString(5), rs.getDouble(7), rs.getDouble(8)), 
							 rs.getString(6));
					list.add(new HotelWithRating(hotel,rs.getFloat(9)));
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
	
	
	public Status userLogin(String user, String passwd)
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
				String passhash = getHash(newpass, usersalt); // combine
																// password and
																// salt and hash
																// again

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

	
	/*public Status createHotelTable(ThreadSafeHotelData hdata)
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
	
	/*public Status createReviewTable(ThreadSafeHotelData hdata)
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

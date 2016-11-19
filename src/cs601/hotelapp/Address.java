package cs601.hotelapp;


/**
 * Class Address - a data structure that stores information 
 * about address of hotel. ..
 * @author TuoHe
 */

public class Address {
	

	private String street;
	private String city;
	private String state;
	private double longitude;
	private double latitude;

	/** A default constructor for class Address
	 * @param str
	 * 		The name of street
	 * @param ct
	 * 		The name of city
	 * @param st
	 * 		The name of state
	 * @param lon
	 * 		The longitude of hotel
	 * @param lat
	 * 		The latitude of hotel 
	 * */
	public Address(String str, String ct, String st, double lon, double lat)
	{
		street = str;
		city = ct;
		state = st;
		longitude = lon;
		latitude = lat;
	}
	
	/** A method to get street name
	 * @return the street name */
	public String getStreet()
	{
		return street;
	}
	
	/** A method to set street name
	 * @param street
	 * 		The street name that want to set to this address */
	public void setStreet(String street)
	{
		this.street = street;
	}
	
	/** A method to get city name
	 * @return the city name */
	public String getCity()
	{
		return city;
	}
	
	/** A method to set city name
	 * @param city
	 * 		The city name that want to set to this address */
	public void setCity(String city)
	{
		this.city = city;
	}
	
	/** A method to get state name
	 * @return the state name */
	public String getState()
	{
		return state;
	}
	
	/** A method to set state name
	 * @param state
	 * 		The state name that want to set to this address */
	public void setState(String state)
	{
		this.state = state;
	}
	
	/** A method to get longitude
	 * @return the longitude of hotel */
	public double getLongitude()
	{
		return longitude;
	}
	
	/** A method to set longitude
	 * @param longitude
	 * 		The longitude that want to set to this address */
	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}
	
	/** A method to get latitude
	 * @return the latitude */
	public double getLatitude()
	{
		return latitude;
	}
	
	/** A method to set latitude
	 * @param latitude
	 * 		The latitude that want to set to this address */
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

}

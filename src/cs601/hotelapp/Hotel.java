package cs601.hotelapp;

/**
 * Class Hotel - a data structure that stores information 
 * about hotels include the address of the hotel. 
 * @author TuoHe
 */
public class Hotel implements Comparable<Hotel>{
	

	private String id;
	private String name;
	private Address add;
	private String country;

	/** A default constructor for class Hotel
	 * @param Id
	 * 		The id of hotel
	 * @param Name
	 * 		The name of hotel
	 * @param Add
	 * 		The address of hotel
	 * */
	public Hotel(String Id, String Name, Address Add, String country)
	{
		id = Id;
		name = Name;
		add = Add;
		this.country = country;
	}
	
	/** A method to get hotel id
	 * @return the hotel id */
	public String getHotelId()
	{
		return id;
	}
	
	/** A method to set hotel id
	 * @param id
	 * 		The hotel id that want to set to this hotel */
    public void setHotelId(String id)
    {
    	this.id = id;
    }

	/** A method to get the country this hotel locate at
	 * @return the hotel id */
	public String getCountry()
	{
		return country;
	}
	
	/** A method to set the country this hotel locate at
	 * @param id
	 * 		The country that want to set to this hotel */
    public void setCountry(String country)
    {
    	this.country = country;
    }    
    
	/** A method to get hotel name
	 * @return the hotel name */
    public String getHotelName()
    {
    	return name;
    }
    
	/** A method to set hotel name
	 * @param name
	 * 		The hotel name that want to set to this hotel */
    public void setHotelName(String name)
    {
    	this.name = name;
    }
    
	/** A method to get hotel address
	 * @return the hotel address */
    public Address getHotelAddress()
    {
    	return add;
    }
    
	/** A method to set hotel address
	 * @param add
	 * 		The hotel address that want to set to this hotel */
    public void setHotelAddress(Address add)
    {
    	this.add = add;
    }
    
	/**

	 * Compare hotels based on name(alphabetically). 
	 * 	
	 * @param H - Another hotel which need to be compared with this hotel.
     *
	 * @return Instance of the following:
	 * 	0 - Two hotels are "equally",
	 * 	1 - This hotels is "greater" than another one,
	 * 	-1 - This hotels is "lesser" than another one.
	 */
	@Override
	public int compareTo(Hotel H)
	{
		return this.name.compareTo(H.getHotelName());
	}

}

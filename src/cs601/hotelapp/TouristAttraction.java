package cs601.hotelapp;

/**
 * Class TouristAttraction - a data structure that stores information 
 * about attraction. 
 * @author TuoHe
 */
public class TouristAttraction {
	
	private String attractionId;
	private String name;
	private String address;
	private Number rating;
	
	/** A default constructor for class TouristAttraction
	 * @param attractionId
	 * 		The id of attraction
	 * @param name
	 * 		The name of attraction
	 * @param address
	 * 		The address of attraction
	 * @param rating
	 * 		The rating of attraction
	 * */
	public TouristAttraction(String attractionId, String name, String address, Number rating)
	{
		this.attractionId = attractionId;
		this.name = name;
		this.address = address;
		this.rating = rating;
	}
	
	/** A method to get the attractionId
	 * @return attractionId */
	public String getAttractionId()
	{
		return attractionId;
	}
	
	/** A method to get the attractionId
	 * @return attractionId */	
	public String getName()
	{
		return name;
	}

	/** A method to get the address
	 * @return address */
	public String getAdderss()
	{
		return address;
	}
	
	/** A method to get the rating
	 * @return rating */
	public Number getRating()
	{
		return rating;
	}
		
     /**
      * Returns a string representation of the attraction in the following format:
      * The id of the attraction, 1 whitespace, the name of the attraction, 1 whitespace 
      * , the address of the attraction, 1 whitespace, the rating of the attraction
      * @return String
      */
	@Override
	public String toString()
	{
		return attractionId + " " + name + " " + address + " " + rating;
	}
	

}

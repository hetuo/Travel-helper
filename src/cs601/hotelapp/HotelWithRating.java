package cs601.hotelapp;

import cs601.hotelapp.Hotel;

/**
 * class HotelWtihRating - a data structure to store hotel and it's rating
 * @author tuo
 *
 */
public class HotelWithRating implements Comparable<HotelWithRating>{

	private Hotel hotel;
	private float rating;
	
	/**
	 * A default constructor method of this class
	 * @param hotel
	 * 		hotel information 
	 * @param rating
	 * 		hotel's rating
	 */
	public HotelWithRating(Hotel hotel, float rating)
	{
		this.hotel = hotel;
		this.rating = rating;
	}
	
	/**
	 * Return hotel information
	 * @return
	 * 		hotel information
	 */
	public Hotel getHotel()
	{
		return this.hotel;
	}
	
	public void setHotel(Hotel hotel)
	{
		this.hotel = hotel;
	}
	
	/**
	 * Return hotel rating
	 * @return
	 * 		hotel rating
	 */
	public float getRating()
	{
		return this.rating;
	}
	
	/**
	 * Override compareTo method of interface comparable
	 */
	@Override
	public int compareTo(HotelWithRating H)
	{
		return hotel.getHotelName().compareTo(H.getHotel().getHotelName());
	}
}

package cs601.Server;

import cs601.hotelapp.Hotel;

public class HotelWithRating {

	private Hotel hotel;
	private float rating;
	
	
	public HotelWithRating(Hotel hotel, float rating)
	{
		this.hotel = hotel;
		this.rating = rating;
	}
	
	public Hotel getHotel()
	{
		return this.hotel;
	}
	
	public float getRating()
	{
		return this.rating;
	}
}

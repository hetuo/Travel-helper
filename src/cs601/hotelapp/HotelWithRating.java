package cs601.hotelapp;

import cs601.hotelapp.Hotel;

public class HotelWithRating implements Comparable<HotelWithRating>{

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
	
	@Override
	public int compareTo(HotelWithRating H)
	{
		return hotel.getHotelName().compareTo(H.getHotel().getHotelName());
	}
}

package cs601.hotelapp;

import java.util.ArrayList;

public class HotelDetails {
	
	private Hotel hotel;
	private ArrayList<Review> reviews;
	
	public HotelDetails(Hotel hotel, ArrayList<Review> reviews)
	{
		this.hotel = hotel;
		this.reviews = reviews;
	}
	
	public Hotel getHotel()
	{
		return hotel;
	}
	
	public ArrayList<Review> getReviews()
	{
		return reviews;
	}
	
	public void setHotel(Hotel hotel)
	{
		this.hotel = hotel;
	}
	
	public void setReviews(ArrayList<Review> reviews)
	{
		this.reviews = reviews;
	}
	
}

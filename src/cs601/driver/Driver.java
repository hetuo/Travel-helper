package cs601.driver;

import java.nio.file.Paths;

import cs601.hotelapp.HotelDataBuilder;
import cs601.hotelapp.ThreadSafeHotelData;

/**
 * class Driver - Start the jetty server
 * @author tuo
 *
 */
public class Driver {
	
	public static void main(String[] args)
	{
		//ThreadSafeHotelData hdata = new ThreadSafeHotelData();
		//HotelDataBuilder builder = new HotelDataBuilder(hdata);
		//builder.loadHotelInfo("input/hotels200.json");
		//builder.loadReviews(Paths.get("input/reviews"));
		//builder.shutdown();
		JettyHttpServer jetty = new JettyHttpServer();
		jetty.startServer();
	}

}

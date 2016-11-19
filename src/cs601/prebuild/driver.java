package cs601.prebuild;

import java.nio.file.Paths;

import cs601.database.DatabaseHandler;
import cs601.database.Status;
import cs601.hotelapp.HotelDataBuilder;
import cs601.hotelapp.ThreadSafeHotelData;

public class driver {
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	/*public static void main(String[] args)
	{
		ThreadSafeHotelData hdata = new ThreadSafeHotelData();
		HotelDataBuilder builder = new HotelDataBuilder(hdata);
		builder.loadHotelInfo("input/hotels200.json");
		builder.loadReviews(Paths.get("input/reviews"));
		builder.shutdown();
		//Status status = dbhandler.createHotelTable(hdata);
		//dbhandler.createReviewTable(hdata);
	}*/

}

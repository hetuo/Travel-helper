package cs601.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs601.database.DatabaseHandler;
import cs601.database.Status;
import cs601.hotelapp.HotelDetails;

public class HotelDetailsServlet extends BaseServlet{

	private static final DatabaseHandler dbHandler = DatabaseHandler.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		String hotelid = request.getParameter("hotelid");
		HotelDetails details = new HotelDetails(null, null);
		Status status = dbHandler.getHotelDetails(hotelid, details);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		
	}
}

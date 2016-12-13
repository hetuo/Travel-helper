package cs601.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import cs601.database.DatabaseHandler;
import cs601.database.Status;
import cs601.hotelapp.HotelDetails;

@SuppressWarnings("serial")
public class HotelDetailsServlet extends BaseServlet{

	private static final DatabaseHandler dbHandler = DatabaseHandler.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String hotelid = request.getParameter("hotelid");
		String rating = request.getParameter("rating");
		HttpSession session = request.getSession();
		session.setAttribute("currHotelId", hotelid);
		HotelDetails details = new HotelDetails(null, null);
		dbHandler.getHotelDetails(hotelid, details);
		VelocityContext context = new VelocityContext();
		VelocityEngine ve = (VelocityEngine)request.getServletContext().getAttribute("templateEngine");
		Template template = ve.getTemplate("src/cs601/webpage/details.html");
		String expedia = "https://www.expedia.com/h" + hotelid + ".Hotel-Information";
		context.put("hotelid", hotelid);
		context.put("status", "user");
		context.put("expedia", expedia);
		context.put("lat", details.getHotel().getHotelAddress().getLatitude());
		context.put("lng", details.getHotel().getHotelAddress().getLongitude());
		context.put("name", details.getHotel().getHotelName());
		context.put("address", details.getHotel().getHotelAddress().getStreet() + ", " + details.getHotel()
		.getHotelAddress().getCity() + ", " + details.getHotel().getCountry());
		context.put("rating", rating);
		context.put("expedia", "www.expedia.com");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		PrintWriter out = response.getWriter();
		out.println(writer.toString());
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		PrintWriter out = response.getWriter();
		String reviewId = request.getParameter("reviewId");
		System.out.println(reviewId);
		String result = dbHandler.getReviewLikes(reviewId);
		out.println(result);
	}
}

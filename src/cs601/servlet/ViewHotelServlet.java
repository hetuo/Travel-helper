package cs601.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs601.database.DatabaseHandler;
import cs601.database.Status;
import cs601.hotelapp.HotelWithRating;

@SuppressWarnings("serial")
public class ViewHotelServlet extends BaseServlet {
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		if (request.getSession().getAttribute("user") == null)
			response.sendRedirect("/login");
		//ArrayList<HotelWithRating> list = new ArrayList<HotelWithRating>();
		TreeSet<HotelWithRating> list = new TreeSet<HotelWithRating>();
		dbhandler.viewHotel(list);
		PrintWriter out = response.getWriter();
		displayForm(out, list); 		
	}
	
	private void displayForm(PrintWriter out, TreeSet<HotelWithRating> list)
	{
		assert out != null;			
		out.println("<table style=\"width:100%\">");
		out.println("<tr><td>Name</td><td>Street</td><td>City</td><td>Rating</td>");				
		for (HotelWithRating hotel : list)
		{
			out.println("<tr>");
			out.println("<td>"+"<a href=/review?hotelid="+hotel.getHotel().getHotelId()+">"+hotel.getHotel().getHotelName()+"</td>");
			out.println("<td>"+hotel.getHotel().getHotelAddress().getStreet()+"</td>");
			out.println("<td>"+hotel.getHotel().getHotelAddress().getCity()+"</td>");
			out.println("<td>"+hotel.getRating()+"</td>");
			out.println("<td>"+"<a href=/addreview?hotelid="+hotel.getHotel().getHotelId()+">Add review</td>");
			out.println("</tr>");
		}
		out.println("</table>");		
	}

}

package cs601.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs601.database.DatabaseHandler;
import cs601.database.Status;

public class ViewHotelServlet extends BaseServlet {
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		//String username = (String)request.getSession().getAttribute("user");
		//SqlResult sr = new SqlResult();
		if (request.getSession().getAttribute("user") == null)
			response.sendRedirect("/login");
		ArrayList<HotelWithRating> list = new ArrayList<HotelWithRating>();
		Status status = dbhandler.viewHotel(list);
		System.out.println("XXXXXX" + list.size());
		/*try {
			if (sr.getTest() == 1)
				System.out.println("HTTTT");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		PrintWriter out = response.getWriter();
		//out.println("<p>hello world</p>");
		displayForm(out, list); 
		//finishResponse(response);
		
	}
	
	private void displayForm(PrintWriter out, ArrayList<HotelWithRating> list)
	{
		
		
		
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

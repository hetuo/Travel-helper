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
import cs601.hotelapp.Review;


/**
 * Class ViewReviewServlet - a servlet class that handler the request from web about view review
 * @author TuoHe
 */
@SuppressWarnings("serial")
public class ViewReviewServlet extends BaseServlet{
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	
	/**Override the doGet method to process the request from client about view reivew
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		PrintWriter out = response.getWriter();
		if (request.getSession().getAttribute("user") == null)
			response.sendRedirect("/login");
		String hotelid = request.getParameter("hotelid");
		if (null == hotelid){
			out.println("<p>Invalid hotel id</p>");
			return;
		}		
		TreeSet<Review> list = new TreeSet<Review>();
		dbhandler.viewReview(hotelid, list);
		displayForm(out, list); 	
	}
	
	/*@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
	}*/
	
	/** Writes and HTML form that shows reviews
	 **/
	private void displayForm(PrintWriter out, TreeSet<Review> list)
	{	
		out.println("<table style=\"width:100%\">");
		out.println("<tr><td>Title</td><td>Content</td><td>User</td><td>Date</td><td>IsRecom</td>"
				+ "<td>Rating</td>");
		for (Review review : list)
		{
			out.println("<tr>");
			out.println("<td>"+review.getReviewTitle()+"</td>");
			out.println("<td>"+review.getReviewText()+"</td>");
			out.println("<td>"+review.getReviewUsername()+"</td>");
			out.println("<td>"+review.getReviewDate()+"</td>");
			if (review.getRecom())
				out.println("<td>"+"YES"+"</td>");
			else
				out.println("<td>"+"NO"+"</td>");
			out.println("<td>"+review.getRating()+"</td>");
			out.println("</tr>");
		}
		out.println("</table>");
	}
	
	

}

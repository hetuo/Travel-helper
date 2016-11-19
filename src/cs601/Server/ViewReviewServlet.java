package cs601.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs601.database.DatabaseHandler;
import cs601.database.Status;
import cs601.hotelapp.Review;

public class ViewReviewServlet extends BaseServlet{
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		//String username = (String)request.getSession().getAttribute("user");
		//SqlResult sr = new SqlResult();
		PrintWriter out = response.getWriter();
		if (request.getSession().getAttribute("user") == null)
			response.sendRedirect("/login");
		String hotelid = request.getParameter("hotelid");
		if (null == hotelid){
			out.println("<p>Invalid hotel id</p>");
			return;
		}
		
		ArrayList<Review> list = new ArrayList<Review>();
		Status status = dbhandler.viewReview(hotelid, list);
		System.out.println("XXXXXX" + list.size());
		/*try {
			if (sr.getTest() == 1)
				System.out.println("HTTTT");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//out.println("<p>hello world</p>");
		displayForm(out, list); 
		//finishResponse(response);		
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		String user = (String)request.getSession().getAttribute("user");
		String text = (String)request.getParameter("comment");
		//Status status = dbhandler.addReview(user, text);
	}
	
	private void displayForm(PrintWriter out, ArrayList<Review> list)
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

package cs601.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs601.database.DatabaseHandler;
import cs601.database.Status;

public class AddReviewServlet extends BaseServlet{
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	private String hotelid = null;
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
			//String username = (String)request.getSession().getAttribute("user");
			//SqlResult sr = new SqlResult();
			if (request.getSession().getAttribute("user") == null)
				response.sendRedirect("/login");
			hotelid = (String)request.getParameter("hotelid");
			//ArrayList<HotelWithRating> list = new ArrayList<HotelWithRating>();
			//Status status = dbhandler.viewHotel(list);
			//System.out.println("XXXXXX" + list.size());
			/*try {
				if (sr.getTest() == 1)
					System.out.println("HTTTT");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			PrintWriter out = response.getWriter();
			//out.println("<p>hello world</p>");
			displayForm(out); 
			//finishResponse(response);
			
		}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		String username = (String)request.getSession().getAttribute("user");
		if (username == null)
			response.sendRedirect("/login");
		String title = (String)request.getParameter("title");
		String text = (String)request.getParameter("text");
		String rating = (String)request.getParameter("rating");
		String recom = (String)request.getParameter("recom");
		Status status = dbhandler.addReview(username, hotelid, title, text, rating, recom);
		PrintWriter out = response.getWriter();
		out.println("<p>Add review successfully!</p><br>");
		out.println("<a href=/main>Click here back to main page</a>");
	}
	
	
	
	
	
	
	
	
	
	
	
	private void displayForm(PrintWriter out)
	{
		out.println("<form action=\"/addreview\" method=\"post\">"); // the form will be processed by POST
		out.println("<table border=\"0\">");
		out.println("\t<tr>");
		out.println("\t\t<td>Title:</td>");
		out.println("\t\t<td><textarea name=\"title\" cols=\"20\" rows=\"1\"></textarea></td>");
		out.println("\t</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Text:</td>");
		out.println("\t\t<td><textarea name=\"text\" cols=\"20\" rows=\"5\"></textarea></td>");
		out.println("</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Rating:</td>");
		out.println("\t\t<td><textarea name=\"rating\" cols=\"5\" rows=\"1\"></textarea></td>");
		out.println("</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>IsRecom:</td>");
		out.println("\t\t<td><input type=\"radio\" name=\"recom\" value=\"yes\" checked> YES</td>");
		out.println("\t\t<td><input type=\"radio\" name=\"recom\" value=\"no\"> NO</td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<p><input type=\"submit\" value=\"Add\"></p>");
		out.println("</form>");
	}

}

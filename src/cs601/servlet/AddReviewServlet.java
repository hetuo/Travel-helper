package cs601.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs601.database.DatabaseHandler;
import cs601.database.Status;

/**
 * Class AddReviewServlet - a servlet class that handler the request from web about add review
 * @author TuoHe
 */
@SuppressWarnings("serial")
public class AddReviewServlet extends BaseServlet{
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	private String hotelid = null;
	
	/**Override the doGet method to process the request from client about add review
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
			if (request.getSession().getAttribute("user") == null)
				response.sendRedirect("/login");
			hotelid = (String)request.getParameter("hotelid");
			PrintWriter out = response.getWriter();
			displayForm(out); 			
		}
	
	/**Override the doPost method to process the request from client about add review
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		String username = (String)request.getSession().getAttribute("user");
		if (username == null)
			response.sendRedirect("/login");
		String title = (String)request.getParameter("title");
		String text = (String)request.getParameter("text");
		String rating = (String)request.getParameter("rating");
		String recom = (String)request.getParameter("recom");
		dbhandler.addReview(username, hotelid, title, text, rating, recom);
		PrintWriter out = response.getWriter();
		out.println("<p>Add review successfully!</p><br>");
		out.println("<a href=/viewhotel>Click here back to view hotel</a>");
	}
	
	/** Writes and HTML form that shows two textfields and a button to the PrintWriter 
	 * @param out
	 * 		response 
	 * */
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

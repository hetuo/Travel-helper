package cs601.Server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import cs601.database.DatabaseHandler;
import cs601.database.Status;

public class MainServlet extends BaseServlet{
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		prepareResponse("Login", response);

		PrintWriter out = response.getWriter();
		String user = (String)request.getSession().getAttribute("user");
		if (null == user)
			response.sendRedirect("/login");
		// error will not be null if we were forwarded her from the post method where something went wrong
		String error = request.getParameter("error");
		if(error != null) {
			String errorMessage = getStatusMessage(error);
			out.println("<p style=\"color: red;\">" + errorMessage + "</p>");
		}

		displayForm(out); 
		finishResponse(response);
		
	}
	
	/**Override the doPost method to process the request from client about review information
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
		prepareResponse("Register New User", response);

		// Get data from the textfields of the html form
		String newuser = request.getParameter("user");
		String newpass = request.getParameter("pass");
		// sanitize user input to avoid XSS attacks:
		newuser = StringEscapeUtils.escapeHtml4(newuser);
		newpass = StringEscapeUtils.escapeHtml4(newpass);
		
		// add user's info to the database 
		Status status = dbhandler.userLogin(newuser, newpass);
		

		if(status == Status.OK) { // registration was successful
			String url = "/main?username=" + newuser;
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url);
		}
		else { // if something went wrong
			String url = "/register?error=" + status.name();
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url); // send a get request  (redirect to the same path)
		}
	}
	
	/** Writes and HTML form that shows two textfields and a button to the PrintWriter */
	private void displayForm(PrintWriter out) {
		assert out != null;	
		out.println("<a href=/logout>Logout</a>");
		out.println("<a href=/viewhotel>View all hotels</a>");
		
		/*out.println("<form action=\"/viewhotel\" method=\"get\">");
		out.println("<table border=\"0\">");
		out.println("\t<tr>");
		out.println("<p><input type=\"submit\" value=\"Login\" onclick=\"javascript:this.form.action='/login';"
				+ "javascript:this.form.method='post';\"></p>");
		out.println("\t\t<td>Usename:</td>");
		out.println("\t\t<td><input type=\"text\" name=\"user\" size=\"30\"></td>");
		out.println("\t</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"pass\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<p><input type=\"submit\" value=\"Login\" onclick=\"javascript:this.form.action='/login';"
				+ "javascript:this.form.method='post';\"></p>");
		out.println("<p><input type=\"submit\" value=\"Register\" onclick=\"javascript:this.form.action='/register';"
				+ "javascript:this.form.method='get';\"></p>");
		out.println("</form>");*/
	}

}
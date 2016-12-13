package cs601.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import cs601.database.DatabaseHandler;
import cs601.database.Status;

/**
 * Class RegisterServlet - a servlet class that handler the request from web about register
 * @author TuoHe
 */
public class RegisterServlet extends BaseServlet{

	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	
	/**Override the doGet method to process the request from client about register
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
			PrintWriter out = response.getWriter();			
			// error will not be null if we were forwarded her from the post method where something went wrong
			String error = request.getParameter("error");
			if(error != null) {
				String errorMessage = getStatusMessage(error);
				VelocityContext context = new VelocityContext();
				VelocityEngine ve = (VelocityEngine)request.getServletContext().getAttribute("templateEngine");
				Template template = ve.getTemplate("src/cs601/webpage/loginError.html");
				context.put("error", errorMessage);
				StringWriter writer = new StringWriter();
				template.merge(context, writer);
				out.println(writer.toString());
			}
			out.print(readWebPage("src/cs601/webpage/register.html"));
		}
	
	/**Override the doPost method to process the request from client about register
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
		// Get data from the textfields of the html form
		String newuser = request.getParameter("username");
		String newpass = request.getParameter("password");
		// sanitize user input to avoid XSS attacks:
		newuser = StringEscapeUtils.escapeHtml4(newuser);
		newpass = StringEscapeUtils.escapeHtml4(newpass);
		
		// add user's info to the database 
		Status status = dbhandler.registerUser(newuser, newpass);
		

		if(status == Status.OK) { // registration was successful
			response.getWriter().print(readWebPage("src/cs601/webpage/registerok.html"));
		}
		else { // if something went wrong
			String url = "/register?error=" + status.name();
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url); // send a get request  (redirect to the same path)
		}
	}
}

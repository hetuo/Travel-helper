package cs601.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import cs601.database.DatabaseHandler;
import cs601.database.Status;

//import cs601.hotelapp.ThreadSafeHotelData;

/**
 * Class LoginServlet - a servlet class that handler the request from web about login
 * @author TuoHe
 */
@SuppressWarnings("serial")
public class LoginServlet extends BaseServlet{
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	
	/**Override the doGet method to process the request from client about login
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		PrintWriter out = response.getWriter();
		String sessionId = (String)request.getSession().getAttribute("user");
		if (sessionId != null && userMap.get(sessionId) != null)
			response.sendRedirect("/main");
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
		out.print(readWebPage("src/cs601/webpage/login.html"));
	}
	
	/**Override the doPost method to process the request from client about login
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
		String name = request.getParameter("username");
		String passwd = request.getParameter("password");
		HttpSession session = request.getSession();	
		name = StringEscapeUtils.escapeHtml4(name);
		passwd = StringEscapeUtils.escapeHtml4(passwd);
		// add user's info to the database 
		Status status = dbhandler.userLogin(name, passwd);
		if(status == Status.OK) { // registration was successful
			String sessionId = session.getId();
			session.setAttribute("user", sessionId);
			userMap.put(sessionId, name);
			response.sendRedirect("/main");
		}
		else { // if something went wrong
			String url = "/login?error=" + status.name();
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url); // send a get request  (redirect to the same path)
		}
	}
}

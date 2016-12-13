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
 * Class MainServlet - a servlet class that handler the request from web after login
 * @author TuoHe
 */
@SuppressWarnings("serial")
public class MainServlet extends BaseServlet{
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	
	/**Override the doGet method to process the request from client after login
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		PrintWriter out = response.getWriter();		
		VelocityContext context = new VelocityContext();
		VelocityEngine ve = (VelocityEngine)request.getServletContext().getAttribute("templateEngine");
		Template template = ve.getTemplate("src/cs601/webpage/main.html");

		String sessionId = (String)request.getSession().getAttribute("user");
		System.out.println(sessionId + userMap.get(sessionId));
		if (null == sessionId || userMap.get(sessionId) == null)
		{
			context.put("status", "tourist");
		}	
		else
		{	
			context.put("status", "user");
			context.put("username", userMap.get(sessionId));
		}
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		out.println(writer.toString());
	}
	
	/**Override the doPost method to process the request from client about review information
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
	}
	
	/** Writes and HTML form that shows 2 links to the PrintWriter */
	private void displayForm(PrintWriter out) {
		assert out != null;	
		out.println("<a href=/logout>Logout</a>");
		out.println("<a href=/viewhotel>View all hotels</a>");
	}

}

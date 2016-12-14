package cs601.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import cs601.database.DatabaseHandler;
import cs601.hotelapp.Review;

public class ProfileServlet extends BaseServlet{
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	
	/**Override the doGet method to process the request from client about login
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		PrintWriter out = response.getWriter();
		String sessionId = (String)request.getSession().getAttribute("user");
		if (sessionId == null || userMap.get(sessionId) == null)
			response.sendRedirect("/login");


		VelocityContext context = new VelocityContext();
		VelocityEngine ve = (VelocityEngine)request.getServletContext().getAttribute("templateEngine");
		Template template = ve.getTemplate("src/cs601/webpage/profile.html");
		context.put("name", userMap.get(sessionId));
		if (lastTime.equals("null"))
		context.put("lastTime", "First time!");
		else
			context.put("lastTime", lastTime);
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		out.println(writer.toString());

	}
	
	/**Override the dopost method to process the request from client about login
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		PrintWriter out = response.getWriter();
		String sessionId = (String)request.getSession().getAttribute("user");
		if (sessionId == null || userMap.get(sessionId) == null)
			response.sendRedirect("/login");
		ArrayList<Review> list = new ArrayList<Review>();
		dbhandler.getUserReview(userMap.get(sessionId), list);
		String result = generateJSONString(list);
		result = result.replace("\r\n", "\\r\\n");
		System.out.println(result);
		response.getWriter().println(result);
	}


}

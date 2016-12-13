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
		String sessionId = (String)request.getSession().getAttribute("user");
		hotelid = (String)request.getSession().getAttribute("hotelid");
		if (sessionId == null || userMap.get(sessionId) == null)
			response.sendRedirect("/login");
		String title = (String)request.getParameter("title");
		String text = (String)request.getParameter("comment");
		String rating = (String)request.getParameter("optradio");
		String recom = (String)request.getParameter("recom");
		String username = userMap.get(sessionId);
		dbhandler.addReview(username, hotelid, title, text, rating, recom);
        VelocityEngine ve = (VelocityEngine)request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("src/cs601/webpage/success.html");
        context.put("username", userMap.get(sessionId));
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        response.getWriter().println(writer.toString());
		}
	
	/**Override the doPost method to process the request from client about add review
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		String sessionId = (String)request.getSession().getAttribute("user");
		if (sessionId == null || userMap.get(sessionId) == null)
			response.sendRedirect("/login");
		String title = (String)request.getParameter("title");
		String text = (String)request.getParameter("text");
		String rating = (String)request.getParameter("rating");
		String recom = (String)request.getParameter("recom");
		dbhandler.addReview(userMap.get(sessionId), hotelid, title, text, rating, recom);
        VelocityEngine ve = (VelocityEngine)request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("src/cs601/webpage/success.html");
        context.put("username", userMap.get(sessionId));
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        response.getWriter().println(writer.toString());
	}
	


}

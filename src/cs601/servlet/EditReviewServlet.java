package cs601.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import cs601.database.DatabaseHandler;
import cs601.hotelapp.Review;

public class EditReviewServlet extends BaseServlet{
	
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
		String reviewId = request.getParameter("reviewId");
		Review review = dbhandler.getReviewById(reviewId);
		System.out.println(review.getReviewTitle());
		VelocityContext context = new VelocityContext();
		VelocityEngine ve = (VelocityEngine)request.getServletContext().getAttribute("templateEngine");
		Template template = ve.getTemplate("src/cs601/webpage/editreview.html");
		context.put("username", userMap.get(sessionId));
		context.put("title", review.getReviewTitle());
		context.put("text", review.getReviewText());
		context.put("reviewId", reviewId);
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		out.println(writer.toString());

	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		String sessionId = (String)request.getSession().getAttribute("user");
		if (sessionId == null || userMap.get(sessionId) == null)
			response.sendRedirect("/login");
		String reviewId = request.getParameter("reviewId");
		String title = (String)request.getParameter("title");
		String text = (String)request.getParameter("comment");
		String rating = (String)request.getParameter("optradio");
		String recom = (String)request.getParameter("recom");
		dbhandler.updateReview(reviewId, title, text, rating, recom);
		response.getWriter().println("ok");
	}


}

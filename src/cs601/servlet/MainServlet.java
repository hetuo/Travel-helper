package cs601.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import cs601.database.DatabaseHandler;
import cs601.database.Status;

@SuppressWarnings("serial")
public class MainServlet extends BaseServlet{
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		PrintWriter out = response.getWriter();
		String user = (String)request.getSession().getAttribute("user");
		if (null == user)
			response.sendRedirect("/login");
		displayForm(out); 		
	}
	
	/**Override the doPost method to process the request from client about review information
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
	}
	
	/** Writes and HTML form that shows two textfields and a button to the PrintWriter */
	private void displayForm(PrintWriter out) {
		assert out != null;	
		out.println("<a href=/logout>Logout</a>");
		out.println("<a href=/viewhotel>View all hotels</a>");
	}

}

package cs601.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class LogoutServlet extends BaseServlet{
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		
		HttpSession session = request.getSession();
		if (session == null)
			response.sendRedirect("/login");
		else
		{
			session.removeAttribute("user");
			response.sendRedirect("/login");
		}
		
	}

}

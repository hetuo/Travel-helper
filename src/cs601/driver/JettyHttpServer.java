package cs601.driver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import cs601.servlet.*;

/**
 * Class JettyHttpServer - a class to create jetty server
 * @author TuoHe
 */
public class JettyHttpServer {
	
	public static final int PORT = 8080;
		
	/**
	 * A method to start the Jetty server
	 */
	public void startServer()
	{
		Server server = new Server(PORT);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		//context.setAttribute("utility", utility);
		context.addServlet(LoginServlet.class, "/login");
		context.addServlet(RegisterServlet.class, "/register");
		context.addServlet(MainServlet.class, "/main");
		context.addServlet(LogoutServlet.class, "/logout");
		context.addServlet(ViewHotelServlet.class, "/viewhotel");
		context.addServlet(ViewReviewServlet.class, "/review");
		context.addServlet(AddReviewServlet.class, "/addreview");
		server.setHandler(context);
		try {	
			server.start();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

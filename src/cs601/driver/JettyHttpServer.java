package cs601.driver;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
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
		context.setResourceBase("src/cs601/webpage/");
		//context.setAttribute("utility", utility);
		context.addServlet(LoginServlet.class, "/login");
		context.addServlet(RegisterServlet.class, "/register");
		context.addServlet(MainServlet.class, "/main");
		context.addServlet(LogoutServlet.class, "/logout");
		context.addServlet(ViewHotelServlet.class, "/viewhotel");
		context.addServlet(ViewReviewServlet.class, "/review");
		context.addServlet(AddReviewServlet.class, "/addreview");
		context.addServlet(HotelDetailsServlet.class, "/detail");
		context.addServlet(DefaultServlet.class, "/");
		server.setHandler(context);
		
		/*ResourceHandler handler = new ResourceHandler();
		handler.setWelcomeFiles(new String[] {"/src/cs601/webpage/login.html"});
		handler.setResourceBase("/home/tuo/workspace/the8USF-project");
		HandlerList handlers = new HandlerList();
		context.setHandler(handler);
		handlers.setHandlers(new Handler[] {context, handler});
		server.setHandler(handlers);*/
		try {	
			server.start();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package cs601.hotelapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class WebClientSSL {
	
	/**
	 * Creates a secure socket to communicate with googleapi's server that
	 * provides geocode API, sends a GET request, and gets a response as a
	 * string. Then remove the head"
	 * 
	 * @return A String of the response.
	 */
	public static String fetch(String host, String pathAndResource) {
		String s = "";

		// let's say we want to find out USF's coordinates:
		//String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=University%20of%20San%20Francisco,%20US";// &sensor=false";
		//URL url;
		PrintWriter out = null;
		BufferedReader in = null;
		SSLSocket socket = null;
		try {
			//url = new URL(urlString);

			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

			// HTTPS uses port 443
			socket = (SSLSocket) factory.createSocket(host, 443);

			// output stream for the secure socket
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			String request = getRequest(host, pathAndResource);
			System.out.println("Request: " + request);

			out.println(request); // send a request to the server
			out.flush();

			// input stream for the secure socket.
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// use input stream to read server's response
			String line;
			StringBuffer sb = new StringBuffer();
			int flag = 1;
			while ((line = in.readLine()) != null) {
				//System.out.println(line);
				if (1 == flag)
				{
					if (line.length() == 1 && line.charAt(0) == '{')
					{
						flag = 0;						
					}
				}
				if (0 == flag){
				sb.append(line);
				sb.append(System.lineSeparator());}
			}
			s = sb.toString();
		} catch (IOException e) {
			System.out.println(
					"An IOException occured while writing to the socket stream or reading from the stream: ");
			e.printStackTrace();
		} finally {
			try {
				// close the streams and the socket
				out.close();
				in.close();
				socket.close();
			} catch (IOException e) {
				System.out.println("An exception occured while trying to close the streams or the socket: " + e);
			}
		}
		return s;

	}

	/**
	 * Takes a host and a string containing path/resource/query and creates a
	 * string of the HTTP GET request
	 * 
	 * @param host
	 * 		  maps.googleapis.com
	 * @param pathResourceQuery
	 * 	      the query contains path and resource
	 * @return
	 * 		  The request string
	 */
	private static String getRequest(String host, String pathResourceQuery) {
		String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator() // GET
																			// request
				+ "Host: " + host + System.lineSeparator() // Host header required for HTTP/1.1
				+ "Connection: close" + System.lineSeparator() // make sure the server closes the
															   // connection after we fetch one page
				+ System.lineSeparator();
		return request;
	}	
}

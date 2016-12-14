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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cs601.database.DatabaseHandler;
import cs601.hotelapp.Review;
import cs601.tourist.TouristAttraction;
import cs601.tourist.WebClientSSL;

public class TouristAttractionServlet extends BaseServlet{
	
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	
	/**Override the doGet method to process the request from client about view reivew
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{	
		response.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
		String sessionId = (String)request.getSession().getAttribute("user");
		if (sessionId == null || userMap.get(sessionId) == null)
			response.sendRedirect("/login");
		String radius = request.getParameter("radius");
		//String hotelid = (String)request.getSession().getAttribute("currHotelId");
		String hotelid = request.getParameter("hotelid");
		System.out.println(hotelid);
		String addrInfo = dbhandler.getHotelAddr(hotelid);
		PrintWriter out = response.getWriter();
		ArrayList<TouristAttraction> attractions = fetchAttractions(Integer.parseInt(radius), addrInfo);
        VelocityEngine ve = (VelocityEngine)request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("src/cs601/webpage/attraction.html");
        context.put("username", userMap.get(sessionId));
        context.put("attractionList", attractions);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
		
	}
	
	/**
	 * Fetch all the attractions nearby all hotels in certain radius
	 * Save the message in json file which get from google. The filename
	 * of these json file are hotelId + result.json.
	 * 
	 * @param radiusInMiles
	 *            The radius which will be used in GET request.
	 */
	private ArrayList<TouristAttraction> fetchAttractions(int radiusInMiles, String addrInfo)
	{
        String pathAndResources = generateQueries(radiusInMiles * 1609, addrInfo); 
        String host = "maps.googleapis.com";
        String result = WebClientSSL.fetch(host, pathAndResources);
        ArrayList<TouristAttraction> attractions = parseAttractionJson(result);
        //System.out.println(result);
        return attractions; 
	}
	
	private String generateQueries(int meters, String adderInfo)
	{

        
        String city = adderInfo.split(":")[0].replaceAll(" ", "%20");
        String longitude = adderInfo.split(":")[1];
        String latitude = adderInfo.split(":")[2];
		StringBuffer sb = new StringBuffer();

        sb.append("/maps/api/place/textsearch/json?query=tourist%20attractions+in+");
        sb.append(city);
        sb.append("&location=");
        sb.append(latitude + "," + longitude + "&radius=" + meters);
        sb.append("&key=AIzaSyC7mq0PWhPlvvxPTYIN-E_RMiOMnA4Dw4M");
        return sb.toString();
	}
	
	/**
	 * Parse the json file get from google maps
	 * @param jStr
	 * 		the string need to parse
	 * @return
	 * 		json result
	 */
	private ArrayList<TouristAttraction> parseAttractionJson(String jStr)
	{
		ArrayList<TouristAttraction> list = new ArrayList<TouristAttraction>();
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(jStr);
			JSONArray attractions = parseJSONFile((JSONObject)obj, "results");
			int size = attractions.size();
            for (int i = 0; i < size; i++)
            {
                JSONObject attraction = (JSONObject)attractions.get(i);

                String attractionId = (String)attraction.get("id");
                String name = (String)attraction.get("name");
                String address = (String)attraction.get("formatted_address");
                //double rating = Double.parseDouble((String)attraction.get("rating"));
                Number rating;
                if (attraction.get("rating") == null)
                    rating = 3.0;
                else 
                    rating = (Number)attraction.get("rating");
                JSONObject geometry = (JSONObject)attraction.get("geometry");
                JSONObject location = (JSONObject)geometry.get("location");
                double lng = (double)location.get("lng");
                double lat = (double)location.get("lat");
                //System.out.println(lng + "&&" + lat);
                TouristAttraction touristAttraction = new TouristAttraction(attractionId, name, address, rating, lng, lat);
                list.add(touristAttraction);
            }
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * Parse JSON Object to JSON Array, find the the JSON array we need then return it.
	 * 
	 * @param jObj
	 *            The JSON object need to parse
	 * @param arrName
	 * 			  The JSON Array's name we need
	 * @return
	 * 			  The JSON Array we need
	 */
    private JSONArray parseJSONFile(JSONObject jObj, String arrName)
    {   
        JSONArray jArray = null;
        for (Object key : jObj.keySet())
        {
            if (jObj.get(key) instanceof JSONObject){
                jArray = parseJSONFile((JSONObject)jObj.get(key), arrName);
                if (null != jArray)
                    break;
                }
            else if(jObj.get(key) instanceof JSONArray){
                if (key.toString().equals(arrName))
                    return (JSONArray)jObj.get(key);
                else
                    continue;
            }
            else{
                continue;
            }
        }
        return jArray;
    } 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

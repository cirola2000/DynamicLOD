package dataid.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.queries.DistributionQueries;

public class Service extends HttpServlet {
	

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		printOutput(request,response);
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		printOutput(request,response);
	}
	
	public void printOutput(HttpServletRequest request,HttpServletResponse response){
		try {
			
			String url = request.getParameter("url");
			
			// check if distribution was streamed
			DistributionMongoDBObject dist = new DistributionMongoDBObject(url);
			
			JSONObject obj = new JSONObject();
			obj.put("url", url);
			
			if(dist.getLastTimeLinkset() != null){
				obj.put(DistributionMongoDBObject.FORMAT, dist.getFormat() );
				obj.put(DistributionMongoDBObject.LAST_TIME_LINKSET, dist.getLastTimeLinkset() );				
			}
			else{
				obj.put(DistributionMongoDBObject.SUCCESSFULLY_DOWNLOADED, dist.isSuccessfullyDownloaded() );
			}
			
		 
//			JSONArray list = new JSONArray();
//			list.put("msg 1");
//			list.put("msg 2");
//			list.put("msg 3");
//			https://raw.githubusercontent.com/AKSW/n3-collection/master/RSSss
//			obj.put("messages", list);
			
			response.getWriter().print(obj.toString());
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

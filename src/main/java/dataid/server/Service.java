package dataid.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.objects.LinksetMongoDBObject;
import dataid.mongodb.queries.DistributionQueries;
import dataid.mongodb.queries.LinksetQueries;

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
			DistributionMongoDBObject dist = DistributionQueries.getByDownloadURL(url);
			
			JSONObject obj = new JSONObject();
			obj.put("url", url);
			
			if(dist!= null){
				obj.put(DistributionMongoDBObject.FORMAT, dist.getFormat() );
				obj.put(DistributionMongoDBObject.LAST_TIME_LINKSET, dist.getLastTimeLinkset());
				
				ArrayList<LinksetMongoDBObject> linksets = LinksetQueries.getLinksetsInDegreeByDistribution(url);
				
				JSONArray indegreeArray = new JSONArray();
				for(LinksetMongoDBObject linkset : linksets){
					JSONObject jsonLinkset = new JSONObject();
					jsonLinkset.put(linkset.SUBJECTS_DISTRIBUTION_TARGET, linkset.getSubjectsDistributionTarget().toString());
					jsonLinkset.put(linkset.LINKS, linkset.getLinks());
					indegreeArray.put(jsonLinkset);
				}
				obj.put("indegree", indegreeArray); 
				
				linksets = LinksetQueries.getLinksetsOutDegreeByDistribution(url);
				
				JSONArray outdegreeArray = new JSONArray();
				for(LinksetMongoDBObject linkset : linksets){
					JSONObject jsonLinkset = new JSONObject();
					jsonLinkset.put(linkset.OBJECTS_DISTRIBUTION_TARGET, linkset.getSubjectsDistributionTarget().toString());
					jsonLinkset.put(linkset.LINKS, linkset.getLinks());
					outdegreeArray.put(jsonLinkset);
				}
				obj.put("outdegree", outdegreeArray); 

			}
			else{
				obj.put("distributionLoaded", false);
			}			
			
			response.getWriter().print(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

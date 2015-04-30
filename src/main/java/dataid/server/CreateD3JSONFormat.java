package dataid.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

import dataid.mongodb.objects.DatasetMongoDBObject;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.objects.LinksetMongoDBObject;
import dataid.mongodb.queries.DatasetQueries;
import dataid.mongodb.queries.DistributionQueries;
import dataid.mongodb.queries.LinksetQueries;

public class CreateD3JSONFormat extends HttpServlet {
	

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		printOutput(request, response);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		printOutput(request, response);
	}

	public void printOutput(HttpServletRequest request,
			HttpServletResponse response) {
		JSONArray nodes = new JSONArray();
		JSONArray links = new JSONArray();

		ArrayList<String> nodeList = new ArrayList<String>();

		String paramDataset = null;
		boolean hasParameters = false;

		try {
			paramDataset = request.getParameter("dataset");
			if (paramDataset != null){
				if(!paramDataset.equals("")){
				paramDataset = request.getParameter("dataset").replace(
						"@@@@@@", "#");
				hasParameters = true;
				System.out.println(paramDataset);
			}
			}

			JSONObject obj = new JSONObject();

			ArrayList<LinksetMongoDBObject> linkList = null;
			
			System.out.println(hasParameters);

			if (!hasParameters) {
				linkList = LinksetQueries.getLinksetsGroupByDatasets();
			} else {
				linkList = LinksetQueries
						.getLinksetsFilterByDataset(paramDataset);
				
			}

			if (linkList != null)
				for (LinksetMongoDBObject singleLink : linkList) {
					
					// if (!singleEdge.getObjectsDatasetTarget().equals(
					// singleEdge.getSubjectsDatasetTarget())) {
					JSONObject link = null;

					JSONObject edgeDetail = new JSONObject();
					// edgeDetail.put("directed", true);
					edgeDetail.put("color", "red");
					if (singleLink.getLinks() > 0) {

						if(!hasParameters){
							edgeDetail.put("target", singleLink
									.getSubjectsDatasetTarget().toString());
							edgeDetail.put("source", singleLink
									.getObjectsDatasetTarget().toString());
							edgeDetail.put("value", 5);
							links.put(edgeDetail);
							addNode(singleLink.getSubjectsDatasetTarget()
									.toString(), hasParameters, nodeList, nodes);
							addNode(singleLink.getObjectsDatasetTarget().toString(), hasParameters, nodeList, nodes);

						}
						else{
							edgeDetail.put("target", singleLink
									.getSubjectsDistributionTarget().toString());
							edgeDetail.put("source", singleLink
									.getObjectsDistributionTarget().toString());
							
							edgeDetail.put("value", 5);
							links.put(edgeDetail);
							addNode(singleLink.getSubjectsDistributionTarget()
									.toString(), hasParameters, nodeList, nodes);
							addNode(singleLink.getObjectsDistributionTarget().toString(), hasParameters, nodeList, nodes);

						}
					}
				}

			obj.put("nodes", nodes);
			obj.put("links", links);

			response.getWriter().print(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addNode(String link, boolean hasParameters, ArrayList<String> nodeList, JSONArray nodes) {
		if (!nodeList.contains(link)) {
			nodeList.add(link);
			JSONObject node = new JSONObject();
			
			String text = "";
			String name= "";
			String color= "";

			if (!hasParameters) {
				DatasetMongoDBObject dt = new DatasetMongoDBObject(link);
				
				if(dt.getTitle()!=null)
					text =dt.getTitle();
				else
					text= dt.getLabel();
				if(dt.getIsVocabulary())
					color = "rgb(255, 127, 14)";
				else
					color = "green";
					
				name= dt.getUri();
			} else {
				DistributionMongoDBObject dt = new DistributionMongoDBObject(
						link);

				if(dt.getTitle()!=null)
					node.put("text", dt.getTitle());
				else
					node.put("text", dt.getDownloadUrl());
				
				if(dt.isVocabulary())
					color = "rgb(255, 127, 14)";
				else
					color =  "green";
				name= dt.getUri();
				
			}
			
			text = text.replace(" ", "<br/>");
			
			node.put("text",text);
			
			node.put("color",color);
			node.put("name",name);
					
			node.put("radius", 20);
			nodes.put(node);
		}
	}
}

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

	JSONArray nodes = new JSONArray();
	JSONArray links = new JSONArray();

	ArrayList<String> nodeList = new ArrayList<String>();

	String paramDataset = null;
	

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
				System.out.println("no");
//				if (paramDataset == null || paramDataset.equals("")) {
				// ArrayList<DatasetMongoDBObject> nodeList = DatasetQueries
				// .getDatasets();
				// if (nodeList != null)
				// for (DatasetMongoDBObject dataset : nodeList) {
				// JSONObject node = new JSONObject();
				// if (dataset.getLabel() != "")
				// node.put("text", dataset.getLabel());
				// else
				// node.put("text", "-");
				// // node.put("shape", "dot");
				// node.put("radius", 25);
				// node.put("color", "green");
				// node.put("name", dataset.getUri());
				// nodes.put(node);
				// }
				linkList = LinksetQueries.getLinksetsGroupByDatasets();
			} else {
				System.out.println("yep");

				// ArrayList<DistributionMongoDBObject> nodeList =
				// DistributionQueries
				// .getDistributionsWithLinksFilterByDataset(paramDataset);
				//
				// if (nodeList != null)
				// for (DistributionMongoDBObject singleNode : nodeList) {
				// JSONObject node = new JSONObject();
				// node.put("text", singleNode.getTitle());
				// // node.put("shape", "dot");
				// node.put("radius", 10);
				// node.put("color", "green");
				// node.put("name", singleNode.getUri());
				// nodes.put(node);
				// }
				
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
									.toString(), hasParameters);
							addNode(singleLink.getObjectsDatasetTarget().toString(), hasParameters);

						}
						else{
							edgeDetail.put("target", singleLink
									.getSubjectsDistributionTarget().toString());
							edgeDetail.put("source", singleLink
									.getObjectsDistributionTarget().toString());
							
							edgeDetail.put("value", 5);
							links.put(edgeDetail);
							addNode(singleLink.getSubjectsDistributionTarget()
									.toString(), hasParameters);
							addNode(singleLink.getObjectsDistributionTarget().toString(), hasParameters);

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

	public void addNode(String link, boolean hasParameters) {
		if (!nodeList.contains(link)) {
			nodeList.add(link);
			JSONObject node = new JSONObject();

			if (!hasParameters) {
				DatasetMongoDBObject dt = new DatasetMongoDBObject(link);
				String text;
				if(dt.getTitle()!=null)
					node.put("text", dt.getTitle());
				else
					node.put("text", dt.getLabel());
					
				node.put("name", dt.getUri());
			} else {
				DistributionMongoDBObject dt = new DistributionMongoDBObject(
						link);
				String text;
				if(dt.getTitle()!=null)
					node.put("text", dt.getTitle());
				else
					node.put("text", dt.getDownloadUrl());
				node.put("name", dt.getUri());
				
			}
			node.put("radius", 20);
			node.put("color", "green");
			nodes.put(node);
		}
	}
}

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
	
	boolean hasParameters = false;

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

		try {
			paramDataset = request.getParameter("dataset");
			if (paramDataset != null){
				paramDataset = request.getParameter("dataset").replace(
						"@@@@@@", "#");
				hasParameters = true;
				System.out.println(paramDataset);
			}

			JSONObject obj = new JSONObject();

			ArrayList<LinksetMongoDBObject> linkList = null;

			if (paramDataset == null || paramDataset.equals("")) {
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
					if (singleLink.getLinks() > 0
							&& singleLink.getOntologyLinks() > 0) {
						edgeDetail.put("color", "red");
						edgeDetail.put("linkType", "ontologyAndLink");
					} else if (singleLink.getOntologyLinks() > 0) {
						edgeDetail.put("color", "green");
						edgeDetail.put("linkType", "ontology");
					} else if (singleLink.getLinks() > 0) {
						edgeDetail.put("color", "blue");
						edgeDetail.put("linkType", "link");
					}

					if (singleLink.getLinks() > 0) {

						edgeDetail.put("source", singleLink
								.getSubjectsDatasetTarget().toString());
						edgeDetail.put("target", singleLink
								.getObjectsDatasetTarget().toString());
						edgeDetail.put("value", 5);

						links.put(edgeDetail);

						addNode(singleLink.getSubjectsDatasetTarget()
								.toString());
						addNode(singleLink.getObjectsDatasetTarget().toString());
					}
				}

			obj.put("nodes", nodes);
			obj.put("links", links);

			response.getWriter().print(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addNode(String link) {

		if (!nodeList.contains(link)) {
			nodeList.add(link);
			JSONObject node = new JSONObject();

			if (hasParameters) {
				DatasetMongoDBObject dt = new DatasetMongoDBObject(link);
				node.put("text", dt.getTitle());
				node.put("name", dt.getUri());
			} else {
				DistributionMongoDBObject dt = new DistributionMongoDBObject(
						link);
				node.put("text", dt.getTitle());
				node.put("name", dt.getUri());
				
			}
			node.put("radius", 20);
			node.put("color", "green");
			nodes.put(node);
		}
	}
}

package dataid.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import dataid.mongodb.objects.DatasetMongoDBObject;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.objects.LinksetMongoDBObject;
import dataid.mongodb.queries.DatasetQueries;
import dataid.mongodb.queries.DistributionQueries;
import dataid.mongodb.queries.LinksetQueries;

public class CreateArborJSONFormat extends HttpServlet {

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
			String paramDataset = request.getParameter("dataset");
			if(paramDataset!=null)
				paramDataset = request.getParameter("dataset").replace("@@@@@@", "#");

			JSONObject obj = new JSONObject();

			JSONObject nodes = new JSONObject();
			JSONObject edges = new JSONObject();
			ArrayList<LinksetMongoDBObject> edgeList = null;

			if (paramDataset == null) {
				ArrayList<DatasetMongoDBObject> nodeList = DatasetQueries
						.getDatasets();
				if (nodeList != null)
					for (DatasetMongoDBObject dataset : nodeList) {
						JSONObject node = new JSONObject();
						if (dataset.getLabel() != "")
							node.put("label", dataset.getLabel());
						else
							node.put("label", "-");
						// node.put("shape", "dot");
						node.put("mass", 1);
						nodes.put(dataset.getUri(), node);
					}
				edgeList = LinksetQueries.getLinksetsGroupByDatasets();
			} else {
				ArrayList<DistributionMongoDBObject> nodeList = DistributionQueries
						.getDistributionsWithLinksFilterByDataset(paramDataset);

				if (nodeList != null)
					for (DistributionMongoDBObject singleNode : nodeList) {
						JSONObject node = new JSONObject();
						node.put("label", singleNode.getTitle());
						// node.put("shape", "dot");
						node.put("mass", 19);
						node.put("fixed", true);
						nodes.put(singleNode.getDownloadUrl(), node);
					}
				edgeList = LinksetQueries.getLinksetsFilterByDataset(paramDataset);
			}
			
			

			if (edgeList != null)
				for (LinksetMongoDBObject singleEdge : edgeList) {
					// if (!singleEdge.getObjectsDatasetTarget().equals(
					// singleEdge.getSubjectsDatasetTarget())) {
					JSONObject edge = null;

					JSONObject edgeDetail = new JSONObject();
					edgeDetail.put("directed", true);
					if (singleEdge.getLinks() > 0
							&& singleEdge.getOntologyLinks() > 0) {
						edgeDetail.put("color", "red");
						edgeDetail.put("linkType", "ontologyAndLink");
					} else if (singleEdge.getOntologyLinks() > 0) {
						edgeDetail.put("color", "green");
						edgeDetail.put("linkType", "ontology");
					} else if (singleEdge.getLinks() > 0) {
						edgeDetail.put("color", "blue");
						edgeDetail.put("linkType", "link");
					}
					
					if(singleEdge.getLinks() > 0 || singleEdge.getOntologyLinks() > 0)
					if (paramDataset == null) {
						if (edges.has(singleEdge.getObjectsDatasetTarget()
								.toString())) {
							edge = (JSONObject) edges.get(singleEdge
									.getObjectsDatasetTarget().toString());
						} else{
							edge = new JSONObject();
							
						}

						edge.put(singleEdge.getSubjectsDatasetTarget()
								.toString(), edgeDetail);

						edges.put(singleEdge.getObjectsDatasetTarget()
								.toString(), edge);
					} else {
						if (edges.has(singleEdge.getObjectsDistributionTarget()
								.toString())) {
							edge = (JSONObject) edges.get(singleEdge
									.getObjectsDistributionTarget().toString());
						} else
							edge = new JSONObject();

						edge.put(singleEdge.getSubjectsDistributionTarget()
								.toString(), edgeDetail);

						edges.put(singleEdge.getObjectsDistributionTarget()
								.toString(), edge);
					}
					// }
				}

			obj.put("nodes", nodes);
			obj.put("edges", edges);

			response.getWriter().print(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

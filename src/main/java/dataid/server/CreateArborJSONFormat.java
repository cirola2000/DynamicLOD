package dataid.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import dataid.mongodb.objects.DatasetMongoDBObject;
import dataid.mongodb.objects.LinksetMongoDBObject;
import dataid.mongodb.queries.DatasetQueries;
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

	public void printOutput(HttpServletRequest request, HttpServletResponse response){

		try {
			
//			String paramDataset = request.getParameter("dataset");
			
			JSONObject obj = new JSONObject();
			
			ArrayList<DatasetMongoDBObject> datasetList = DatasetQueries.getDatasets();

			JSONObject nodes = new JSONObject();
			if (datasetList != null)
				for (DatasetMongoDBObject dataset : datasetList) {
					JSONObject node = new JSONObject();
					node.put("label", dataset.getLabel());
//					node.put("shape", "dot");
					nodes.put(dataset.getUri(), node);
				}
			obj.put("nodes", nodes);

			
			ArrayList<LinksetMongoDBObject> linksetList = LinksetQueries
					.getLinksetsGroupByDatasets();

			JSONObject edges = new JSONObject();
			if (linksetList != null)
				for (LinksetMongoDBObject linkset : linksetList) {
					if(linkset.getLinks()>30)
					if (!linkset.getObjectsDatasetTarget().equals(
							linkset.getSubjectsDatasetTarget())) {
						JSONObject edgeDetail = new JSONObject();
						edgeDetail.put("directed", true);
						JSONObject edge = null;
						if(edges.has(linkset.getObjectsDatasetTarget().toString())){
							edge = (JSONObject) edges.get(linkset.getObjectsDatasetTarget().toString());
						}
						else
							edge= new JSONObject();
						
						edge.put(linkset.getSubjectsDatasetTarget().toString(), edgeDetail);
					
						edges.put(linkset.getObjectsDatasetTarget().toString(), edge);
					}
				}
			obj.put("edges", edges);
//
//			if (linksetList.isEmpty() && datasetList.isEmpty())
//				response.getWriter()
//						.println(
//								"There are no DataIDs files inserted! Please insert a DataID file and try again.");
//			else {
//
//				outModel.write(System.out, "TURTLE");
//				outModel.write(response.getWriter(), "TURTLE");
//			}
			response.getWriter().print(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package dataid.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.mongodb.DBObject;

import dataid.mongodb.actions.Queries;
import dataid.mongodb.objects.DatasetMongoDBObject;
import dataid.mongodb.objects.LinksetMongoDBObject;
import dataid.ontology.Dataset;
import dataid.ontology.vocabulary.NS;

public class CreateOutput extends HttpServlet {

	private static Model outModel = ModelFactory.createDefaultModel();

	// @Test
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {

			outModel.setNsPrefix("rdfs", NS.RDFS_URI);
			outModel.setNsPrefix("dcat", NS.DCAT_URI);
			outModel.setNsPrefix("void", NS.VOID_URI);
			outModel.setNsPrefix("sd", NS.SD_URI);
			outModel.setNsPrefix("prov", NS.PROV_URI);
			outModel.setNsPrefix("dct", NS.DCT_URI);
			outModel.setNsPrefix("xsd", NS.XSD_URI);
			outModel.setNsPrefix("foaf", NS.FOAF_URI);
			outModel.setNsPrefix("dataid", NS.DATAID_URI);

			// get all datasets
			Queries queries = new Queries();

			ArrayList<DatasetMongoDBObject> datasetList = queries.getDatasets();

			for (DatasetMongoDBObject dataset : datasetList) {
				Resource r = outModel.createResource(dataset.getUri());
				r.addProperty(Dataset.dataIDType,
						ResourceFactory.createResource(NS.VOID_URI + "Dataset"));
			}

			ArrayList<LinksetMongoDBObject> linksetList = queries
					.getLinksetsGroupByDatasets();
			// Iterable<DBObject> linksets =
			// queries.getLinksetsGroupByDatasets();

			if (linksetList != null)
				for (LinksetMongoDBObject linkset : linksetList) {
					if (!linkset.getObjectsDatasetTarget().equals(
							linkset.getSubjectsDatasetTarget())) {
						Resource r = outModel.createResource(linkset.getUri());
						r.addProperty(
								Dataset.dataIDType,
								ResourceFactory.createResource(NS.VOID_URI
										+ "Linkset"));
						r.addProperty(
								ResourceFactory.createProperty(NS.VOID_URI
										+ "subjectsTarget"), ResourceFactory
										.createProperty(linkset
												.getSubjectsDatasetTarget()
												.toString()));
						r.addProperty(ResourceFactory
								.createProperty(NS.VOID_URI + "objectsTarget"),
								ResourceFactory.createProperty(linkset
										.getObjectsDatasetTarget().toString()));
					}
				}

			outModel.write(System.out, "TURTLE");
			outModel.write(response.getWriter(), "TURTLE");

			// response.getWriter().println("List of distributions: "+request.getParameter("dataidAddress")+"<br><br>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package dataid.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.objects.LinksetMongoDBObject;
import dataid.mongodb.queries.DistributionQueries;
import dataid.mongodb.queries.LinksetQueries;
import dataid.ontology.Dataset;
import dataid.ontology.NS;

public class CreateOutputByDistributions extends HttpServlet {

	private Model outModel =null;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		printOutput(response);
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		printOutput(response);
	}
	
	public void printOutput(HttpServletResponse response){

		try {
			outModel = ModelFactory.createDefaultModel();

			outModel.setNsPrefix("rdfs", NS.RDFS_URI);
			outModel.setNsPrefix("dcat", NS.DCAT_URI);
			outModel.setNsPrefix("void", NS.VOID_URI);
			outModel.setNsPrefix("sd", NS.SD_URI);
			outModel.setNsPrefix("prov", NS.PROV_URI);
			outModel.setNsPrefix("dct", NS.DCT_URI);
			outModel.setNsPrefix("xsd", NS.XSD_URI);
			outModel.setNsPrefix("foaf", NS.FOAF_URI);
			outModel.setNsPrefix("dataid", NS.DATAID_URI);

//			ArrayList<DistributionMongoDBObject> distributionList = DistributionQueries.getDistributions();
			ArrayList<DistributionMongoDBObject> distributionList = DistributionQueries.getDistributionsWithLinks();

			if (distributionList != null)
				for (DistributionMongoDBObject distribution : distributionList) {
					Resource r = outModel.createResource(distribution.getDownloadUrl());
					r.addProperty(
							Dataset.dataIDType,
							ResourceFactory.createResource(NS.VOID_URI
									+ "Dataset"));
					 String baseName = FilenameUtils.getBaseName(distribution.getDownloadUrl());
					
					r.addProperty(
							Dataset.title,
							baseName);
					r.addProperty(
							Dataset.label,
							baseName);
				}

			ArrayList<LinksetMongoDBObject> linksetList = LinksetQueries
					.getLinksetsGroupByDistributions();

			if (linksetList != null)
				for (LinksetMongoDBObject linkset : linksetList) {
					if(linkset.getLinks()>0){
						Resource r = outModel.createResource(linkset.getUri());
						r.addProperty(
								Dataset.dataIDType,
								ResourceFactory.createResource(NS.VOID_URI
										+ "Linkset"));
						r.addProperty(
								ResourceFactory.createProperty(NS.VOID_URI
										+ "objectsTarget"), ResourceFactory
										.createProperty(linkset
												.getSubjectsDistributionTarget()
												.toString()));
						r.addProperty(ResourceFactory
								.createProperty(NS.VOID_URI + "subjectsTarget"),
								ResourceFactory.createProperty(linkset
										.getObjectsDistributionTarget().toString()));
						r.addProperty(ResourceFactory
								.createProperty(NS.VOID_URI + "triples"),
								ResourceFactory.createPlainLiteral(String.valueOf(linkset.getLinks())));
					}
				}
				

			if (linksetList.isEmpty() && distributionList.isEmpty())
				response.getWriter()
						.println(
								"There are no DataIDs files inserted! Please insert a DataID file and try again.");
			else {

//				outModel.write(System.out, "TURTLE");
				outModel.write(response.getWriter(), "TURTLE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

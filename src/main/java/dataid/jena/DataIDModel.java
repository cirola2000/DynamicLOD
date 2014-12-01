package dataid.jena;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import dataid.DataID;
import dataid.DataIDGeneralProperties;
import dataid.literal.SubsetModel;
import dataid.ontology.Dataset;
import dataid.ontology.Distribution;
import dataid.ontology.vocabulary.NS;

public class DataIDModel {

	private Model inModel = ModelFactory.createDefaultModel();
	List<SubsetModel> distributionsLinks;
	int numberOfDistributions = 0;

	public List<SubsetModel> parseDistributions(
			List<SubsetModel> distributionsLinks) {
		this.distributionsLinks = distributionsLinks;
		// select dataset
		StmtIterator datasets = inModel.listStatements(null,
				Dataset.dataIDType, Dataset.dataIDDataset);

		while (datasets.hasNext()) {

			Statement dataset = datasets.next();
			System.out.println("We found a dataset: " + dataset.getSubject());

			// try to find distribution within dataset
			StmtIterator stmtDistribution = inModel.listStatements(
					dataset.getSubject(), Distribution.dcatDistribution,
					(RDFNode) null);

			// case there's an distribution take the fist that has accessURL
			boolean accessURLFound = false;
			while (stmtDistribution.hasNext() && accessURLFound == false) {

				// store distribution
				Statement distribution = stmtDistribution.next();

				// find accessURL property
				StmtIterator stmtAccessURL = inModel.listStatements(
						distribution.getObject().asResource(),
						Distribution.accessURL, (RDFNode) null);

				// case there is an accessURL property
				if (stmtAccessURL.hasNext()) {
					accessURLFound = true;

					// store accessurl statement
					Statement accessURL = stmtAccessURL.next();

					// save distribution with accessURL to list
					distributionsLinks.add(new SubsetModel(
							numberOfDistributions, distribution.getSubject()
									.toString(), accessURL.getObject()
									.toString()));
					numberOfDistributions++;
				}
			}

			// try find a subset
			StmtIterator stmtSubsets = inModel.listStatements(
					dataset.getSubject(), Dataset.subset, (RDFNode) null);

			// try to find a subset
			// case there is a subset, call recursive

			if (stmtSubsets.hasNext()) {
				iterateSubsetsNew(stmtSubsets);
			}

		}

		return distributionsLinks;
	}

	// iterating over the subsets (recursive method)
	private void iterateSubsetsNew(StmtIterator stmtSubsets) {

		// iterate over subsets
		while (stmtSubsets.hasNext()) {

			// save subset
			Statement subset = stmtSubsets.next();

			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
					"Subset found: " + subset.getObject().toString());

			// find subset within subset
			StmtIterator stmtSubsets2 = inModel.listStatements(subset
					.getObject().asResource(), Dataset.subset, (RDFNode) null);

			// case there is a subset, call method recursively
			if (stmtSubsets2.hasNext()) {
				iterateSubsetsNew(stmtSubsets2);
			} else {

				// find distribution within subset
				StmtIterator stmtDistribution = inModel.listStatements(subset
						.getObject().asResource(),
						Distribution.dcatDistribution, (RDFNode) null);

				// case there's an distribution take the fist that has accessURL
				boolean accessURLFound = false;
				while (stmtDistribution.hasNext() && accessURLFound == false) {
					// store distribution
					Statement distribution = stmtDistribution.next();

					DataID.bean.addDisplayMessage(
							DataIDGeneralProperties.MESSAGE_LOG,
							"Distribution found: "
									+ distribution.getObject().toString());

					// find accessURL property
					StmtIterator stmtAccessURL = inModel.listStatements(
							distribution.getObject().asResource(),
							Distribution.accessURL, (RDFNode) null);

					// case there is an accessURL property
					if (stmtAccessURL.hasNext()) {
						accessURLFound = true;
						// store accessurl statement
						Statement accessURL = stmtAccessURL.next();
						DataID.bean.addDisplayMessage(
								DataIDGeneralProperties.MESSAGE_LOG,
								"Distribution found: accessURL: "
										+ accessURL.getObject().toString());

						// save distribution with accessURL to list
						distributionsLinks.add(new SubsetModel(
								numberOfDistributions, accessURL.getSubject()
										.toString(), accessURL.getObject()
										.toString()));
						numberOfDistributions++;
					}
				}
			}
		}

	}

	// read dataID file and return the dataset uri
	public String readModel(String URL) throws Exception {
		String name = null;

		inModel.read(URL, null, "TTL");
		ResIterator i = inModel.listResourcesWithProperty(Dataset.dataIDType,
				Dataset.dataIDDataset);
		if (i.hasNext()) {
			name = i.next().getURI().toString();
			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
					"Jena model created. ");
			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
					"Looks that this is a valid DataID file! " + name);
		}

		return name;
	}

	public static void mergeCurrentDataIDWithDataIDGraph(String URL)
			throws Exception {

		// merge current dataID with dataid graph
		Model m = ModelFactory.createDefaultModel();
		try {
			m.read(DataIDGeneralProperties.DATAID_GRAPH_MODEL_PATH, "TURTLE");
		} catch (Exception e) {
			
			// case this is the first dataID graph created, set namespaces
			m.setNsPrefix("rdfs", NS.RDFS_URI);
			m.setNsPrefix("dcat", NS.DCAT_URI);
			m.setNsPrefix("void", NS.VOID_URI);
			m.setNsPrefix("sd", NS.SD_URI);
			m.setNsPrefix("prov", NS.PROV_URI);
			m.setNsPrefix("dct", NS.DCT_URI);
			m.setNsPrefix("xsd", NS.XSD_URI);
			m.setNsPrefix("foaf", NS.FOAF_URI);
			m.setNsPrefix("dataid", NS.DATAID_URI);

			e.printStackTrace();
		}

		// create a jena model of the current dataid
		Model currentDataID = ModelFactory.createDefaultModel();
		currentDataID.read(URL, null, "TTL");
		
		// check whereas current dataid has not processed
		StmtIterator s = currentDataID.listStatements(null, Dataset.dataIDType, Dataset.dataIDDataset);
		
		if(s.hasNext()){
			// get current subject and try to find it in the dataid graph 
			StmtIterator t = m.listStatements(s.next().getSubject(), Dataset.dataIDType, Dataset.dataIDDataset);
			
			if(t.hasNext())
				throw new Exception("Current dataid file already had been processed.");
			}
		
		
		// merge currente dataid with dataid graph
		m.add(currentDataID);
		
		// write updated dataid graph
		m.write(new FileOutputStream(DataIDGeneralProperties.DATAID_GRAPH_MODEL_PATH), "TURTLE");

		m.close();
		currentDataID.close();
	}

}

package dataid.jena;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import dataid.DataID;
import dataid.DataIDGeneralProperties;
import dataid.literal.SubsetModel;
import dataid.ontology.Dataset;
import dataid.ontology.Distribution;
import dataid.ontology.Linkset;

public class DataIDModel {
	
	private Model inModel = ModelFactory.createDefaultModel();
	private Model outModel = ModelFactory.createDefaultModel();
	List<SubsetModel> distributionsLinks;
	int numberOfDistributions = 0;

	
	public List<SubsetModel> parseDistributions(List<SubsetModel> distributionsLinks) {
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

					DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
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
			outModel = inModel;
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

}

package dataid.mongodb.actions;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import dataid.literal.DynamicLODCloudEntryModel;
import dataid.mongodb.objects.DistributionMongoDBObject;

public class ProcessEntry {

	// jena model to store data on file system
	private static Model fsModel = ModelFactory.createDefaultModel();
	
	public boolean saveNewMongoDBEntry(DynamicLODCloudEntryModel entry) {
		
		DistributionMongoDBObject distributionMongoDBObj = new DistributionMongoDBObject(entry.getAccessURL());
		distributionMongoDBObj.setAccessUrl(entry.getAccessURL());
		distributionMongoDBObj.setByteSize(String.valueOf(entry.getByteSize()));
		distributionMongoDBObj.setObjectPath(entry.getObjectPath());
		distributionMongoDBObj.setSubjectFilterPath(entry.getSubjectFilterPath());
		distributionMongoDBObj.setTopDataset(entry.getDatasetURI());
		distributionMongoDBObj.setTimeToCreateFilter(entry.getTimeToCreateFilter());
		distributionMongoDBObj.setNumberOfTriplesLoadedIntoFilter(entry.getNumberOfTriplesLoadedIntoFilter());
		distributionMongoDBObj.setNumberOfObjectTriples(entry.getNumberOfObjectTriples());
			
		distributionMongoDBObj.updateObject();
		
		return true;
	}		

}

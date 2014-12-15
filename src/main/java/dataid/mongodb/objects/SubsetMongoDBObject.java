package dataid.mongodb.objects;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import dataid.exceptions.DataIDException;
import dataid.mongodb.DataIDDB;

public class SubsetMongoDBObject extends DataIDDB {
	
	
	// Collection name
	public static final String COLLECTION_NAME = "Subset";

	public static final String LABEL = "label";

	public static final String PARENT_DATASETS = "parent_datasets";

	public static final String SUBSET_URIS = "subset_uris";

	public static final String DISTRIBUTIONS_URIS = "distributions_uris";

	public static final String DATAID_FILENAME = "dtaid_file_name";

	// class properties

	private String label;

	private ArrayList<String> parentDatasetsURI = new ArrayList<String>();

	private ArrayList<String> subsetsURIs = new ArrayList<String>();

	private ArrayList<String> distributionsURIs = new ArrayList<String>();

	public SubsetMongoDBObject(String uri) {
		super(COLLECTION_NAME,uri);
		loadObject();
	}

	public boolean updateObject() {

		// save object case it doens't exists
		try {
			// updating subsets on mongodb
			mongoDBObject.put(SUBSET_URIS, subsetsURIs);

			// updating distributions on mongodb
			mongoDBObject.put(DISTRIBUTIONS_URIS, distributionsURIs);
			
			mongoDBObject.put(PARENT_DATASETS, parentDatasetsURI);

			insert();
		} catch (Exception e2) {
//			e2.printStackTrace();

			try {
				if (update())
					return true;
				else
					return false;
			} catch (DataIDException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	protected boolean loadObject() {
		DBObject obj = search();

		if (obj != null) {

			label = (String) obj.get(LABEL);

			// loading subsets to object
			BasicDBList subsetList = (BasicDBList) obj.get(SUBSET_URIS);
			for (Object sd : subsetList) {
				subsetsURIs.add((String) sd);
			}

			// loading distributions to object
			BasicDBList distributionList = (BasicDBList) obj
					.get(DISTRIBUTIONS_URIS);
			for (Object sd : distributionList) {
				distributionsURIs.add((String) sd);
			}

			// loading parent datasets to object
			BasicDBList parentDatasetsList = (BasicDBList) obj
					.get(PARENT_DATASETS);
			for (Object sd : parentDatasetsList) {
				parentDatasetsURI.add((String) sd);
			}

			System.out.println(obj);
			return true;
		}
		return false;
	}

	public void addSubsetURI(String subsetURI) {
		if (!subsetsURIs.contains(subsetURI))
			subsetsURIs.add(subsetURI);
	}

	public void addDistributionURI(String distributionURI) {
		if (!distributionsURIs.contains(distributionURI))
			distributionsURIs.add(distributionURI);
	}

	public void addParentDatasetURI(String parentDatasetURI) {
		if (!parentDatasetsURI.contains(parentDatasetURI))
			parentDatasetsURI.add(parentDatasetURI);
	}

	public void setLabel(String label) {
		this.label = label;
		mongoDBObject.put(LABEL, label);
	}

	public void setDistributionsURIs(ArrayList<String> distributionsURIs) {
		this.distributionsURIs = distributionsURIs;
	}

	public void setSubsetsURIs(ArrayList<String> subsetsURIs) {
		this.subsetsURIs = subsetsURIs;
	}

	public List<String> getDistributionsURIs() {
		return distributionsURIs;
	}

	public List<String> getSubsetsURIs() {
		return subsetsURIs;
	}

	public ArrayList<String> getParentDatasetURI() {
		return parentDatasetsURI;
	}

	public String getLabel() {
		return label;
	}

}

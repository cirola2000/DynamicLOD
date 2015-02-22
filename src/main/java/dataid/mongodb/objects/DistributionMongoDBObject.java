package dataid.mongodb.objects;

import java.util.ArrayList;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import dataid.exceptions.DataIDException;
import dataid.mongodb.DataIDDB;

public class DistributionMongoDBObject extends DataIDDB {

	
	// Collection name
	public static final String COLLECTION_NAME = "Distribution";

	public static final String ACCESS_URL = "accessUrl";

	public static final String PARENT_DATASETS = "parentDataset";

	public static final String TOP_DATASET = "topDataset";

	public static final String SUBJECT_FILTER_PATH = "subjectFilterPath";

	public static final String OBJECT_PATH = "objectPath"; 

	public static final String NUMBER_OF_TRIPLES_LOADED_INTO_FILTER = "numberOfTriplesLoadedIntoFilter";

	public static final String NUMBER_OF_OBJECTS_TRIPLES = "numberOfObjectTriples";

	public static final String TIME_TO_CREATE_FILTER = "timeToCreateFilter";
	
	public static final String AUTHORITY = "authority";
	
	public static final String TITLE = "title";

	public static final String HTTP_BYTE_SIZE = "httpByteSize";
	
	public static final String HTTP_FORMAT = "httpFormat";

	public static final String HTTP_LAST_MODIFIED = "httpLastModified";

	public static final String TRIPLES = "triples";	
	

	private String accessUrl;

	private String parentDataset;

	private String topDataset;

	private String subjectFilterPath;

	private String objectPath;

	private String numberOfTriplesLoadedIntoFilter;

	private String numberOfObjectTriples;

	private String timeToCreateFilter;

	private String title;

	private String httpByteSize;

	private String httpFormat;
	
	private String httpLastModified;

	private String triples;

	private String authority;
	
	public DistributionMongoDBObject(String uri) {
		super(COLLECTION_NAME, uri);
		loadObject();
	}

	public String getTimeToCreateFilter() {
		return timeToCreateFilter;
	}

	public void setTimeToCreateFilter(String timeToCreateFilter) {
		this.timeToCreateFilter = timeToCreateFilter;
	}

	private ArrayList<String> defaultDatasets = new ArrayList<String>();

	public String getaccessUrl() {
		return accessUrl;
	}

	public void addDefaultDataset(String defaultDataset) {
		if (!defaultDatasets.contains(defaultDataset))
			this.defaultDatasets.add(defaultDataset);
	}

	public boolean updateObject() {
		// save object case it doens't exists
		try {
			mongoDBObject.put(ACCESS_URL, accessUrl);
			mongoDBObject.put(PARENT_DATASETS, defaultDatasets);
			mongoDBObject.put(HTTP_BYTE_SIZE, httpByteSize);
			mongoDBObject.put(HTTP_FORMAT, httpFormat);
			mongoDBObject.put(HTTP_LAST_MODIFIED, httpLastModified);
			mongoDBObject.put(TRIPLES, triples);
			mongoDBObject.put(TOP_DATASET, topDataset);
			mongoDBObject.put(SUBJECT_FILTER_PATH, subjectFilterPath);
			mongoDBObject.put(OBJECT_PATH, objectPath);
			mongoDBObject.put(NUMBER_OF_TRIPLES_LOADED_INTO_FILTER,
					numberOfTriplesLoadedIntoFilter);
			mongoDBObject.put(NUMBER_OF_OBJECTS_TRIPLES, numberOfObjectTriples);
			mongoDBObject.put(TIME_TO_CREATE_FILTER, timeToCreateFilter);
			mongoDBObject.put(TITLE, title);
			mongoDBObject.put(AUTHORITY, authority);

			insert();

		} catch (Exception e2) {
			// e2.printStackTrace();
			try {
				if (update())
					return true;
				else
					return false;
			} catch (DataIDException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	protected boolean loadObject() {
		DBObject obj = search();

		if (obj != null) {
			accessUrl = (String) obj.get(ACCESS_URL);
			httpByteSize = (String) obj.get(HTTP_BYTE_SIZE);
			topDataset = (String) obj.get(TOP_DATASET);
			subjectFilterPath = (String) obj.get(SUBJECT_FILTER_PATH);
			objectPath = (String) obj.get(OBJECT_PATH);
			title = (String) obj.get(TITLE);
			httpFormat = (String) obj.get(HTTP_FORMAT);
			httpLastModified= (String) obj.get(HTTP_LAST_MODIFIED);
			triples = (String) obj.get(TRIPLES);
			numberOfTriplesLoadedIntoFilter = (String) obj
					.get(NUMBER_OF_TRIPLES_LOADED_INTO_FILTER);
			numberOfObjectTriples = (String) obj.get(NUMBER_OF_OBJECTS_TRIPLES);
			authority= (String) obj.get(AUTHORITY);

			// loading default datasets to object
			BasicDBList defaultDatasetList = (BasicDBList) obj
					.get(PARENT_DATASETS);
			if (defaultDatasetList != null)
				for (Object sd : defaultDatasetList) {
					defaultDatasets.add((String) sd);
				}
			return true;
		}
		return false;
	}

	public String getAccessUrl() {
		return accessUrl;
	}

	public void setAccessUrl(String accessUrl) {
		this.accessUrl = accessUrl;
	}

	public String getParentDataset() {
		return parentDataset;
	}

	public void setParentDataset(String parentDataset) {
		this.parentDataset = parentDataset;
	}

	public String getHttpByteSize() {
		return httpByteSize;
	}

	public void setHttpByteSize(String httpByteSize) {
		this.httpByteSize = httpByteSize;
	}

	public String getTopDataset() {
		return topDataset;
	}

	public void setTopDataset(String topDataset) {
		this.topDataset = topDataset;
	}

	public String getSubjectFilterPath() {
		return subjectFilterPath;
	}

	public void setSubjectFilterPath(String subjectFilterPath) {
		this.subjectFilterPath = subjectFilterPath;
	}

	public String getObjectPath() {
		return objectPath;
	}

	public void setObjectPath(String objectPath) {
		this.objectPath = objectPath;
	}

	public String getNumberOfTriplesLoadedIntoFilter() {
		return numberOfTriplesLoadedIntoFilter;
	}

	public void setNumberOfTriplesLoadedIntoFilter(
			String numberOfTriplesLoadedIntoFilter) {
		this.numberOfTriplesLoadedIntoFilter = numberOfTriplesLoadedIntoFilter;
	}

	public String getNumberOfObjectTriples() {
		return numberOfObjectTriples;
	}

	public void setNumberOfObjectTriples(String numberOfObjectTriples) {
		this.numberOfObjectTriples = numberOfObjectTriples;
	}

	public ArrayList<String> getDefaultDatasets() {
		return defaultDatasets;
	}

	public void setDefaultDatasets(ArrayList<String> defaultDatasets) {
		this.defaultDatasets = defaultDatasets;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHttpFormat() {
		return httpFormat;
	}

	public void setHttpFormat(String httpFormat) {
		this.httpFormat = httpFormat;
	}

	public String getHttpLastModified() {
		return httpLastModified;
	}

	public void setHttpLastModified(String httpLastModified) {
		this.httpLastModified = httpLastModified;
	}

	public String getTriples() {
		return triples;
	}

	public void setTriples(String triples) {
		this.triples = triples;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	

}

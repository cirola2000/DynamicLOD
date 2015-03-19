package dataid.mongodb.objects;

import java.util.ArrayList;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import dataid.exceptions.DataIDException;
import dataid.mongodb.DataIDDB;

public class DistributionMongoDBObject extends DataIDDB {

	// Collection name
	public static final String COLLECTION_NAME = "Distribution";

	
	// Distributions status on the system
	public static final String STATUS_WAITING_TO_CREATE_LINKSETS = "WAITING_TO_CREATE_LINKSETS";
	
	public static final String STATUS_DOWNLOADING = "DOWNLOADING";

	public static final String STATUS_DOWNLOADED = "DOWNLOADED";

	public static final String STATUS_SEPARATING_SUBJECTS_AND_OBJECTS = "SEPARATING_SUBJECTS_AND_OBJECTS";

	public static final String STATUS_WAITING_TO_DOWNLOAD = "WAITING_TO_DOWNLOAD";
	
	public static final String STATUS_CREATING_BLOOM_FILTER = "CREATING_BLOOM_FILTER";
	
	public static final String STATUS_CREATING_LINKSETS = "CREATING_LINKSETS";
	
	public static final String STATUS_ERROR = "ERROR";
	
	public static final String STATUS_DONE = "DONE";
	
	
	// collection properties
	public static final String DOWNLOAD_URL = "DownloadUrl";

	public static final String PARENT_DATASETS = "parentDataset";

	public static final String TOP_DATASET = "topDataset";

	public static final String SUBJECT_FILTER_PATH = "subjectFilterPath";

	public static final String OBJECT_PATH = "objectPath";

	public static final String NUMBER_OF_TRIPLES_LOADED_INTO_FILTER = "numberOfTriplesLoadedIntoFilter";

	public static final String NUMBER_OF_OBJECTS_TRIPLES = "numberOfObjectTriples";

	public static final String TIME_TO_CREATE_FILTER = "timeToCreateFilter";
	
	public static final String STATUS = "status";
	
	public static final String SUCCESSFULLY_DOWNLOADED = "successfully_downloaded";
	
	public static final String LAST_ERROR_MSG = "lastErrorMsg";

	public static final String LAST_TIME_LINKSET = "lastTimeLinkset";

	public static final String DOMAIN = "domain";

	public static final String TITLE = "title";

	public static final String HTTP_BYTE_SIZE = "httpByteSize";

	public static final String HTTP_FORMAT = "httpFormat";

	public static final String HTTP_LAST_MODIFIED = "httpLastModified";

	public static final String TRIPLES = "triples";
	
	public static final String FORMAT = "format";
	

	private ArrayList<String> defaultDatasets = new ArrayList<String>();

	private String downloadUrl;

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

	private Integer triples = 0;

	private String format;
	
	private String domain;
	
	private boolean successfullyDownloaded;
	
	private String lastErrorMsg;
	
	private String status;
	
	private String lastTimeLinkset;

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

	public void addDefaultDataset(String defaultDataset) {
		if (!defaultDatasets.contains(defaultDataset))
			this.defaultDatasets.add(defaultDataset);
	}

	public boolean updateObject(boolean checkBeforeInsert) {
		// save object case it doens't exists
		try {
			mongoDBObject.put(DOWNLOAD_URL, downloadUrl);
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
			mongoDBObject.put(FORMAT, format);	
			mongoDBObject.put(STATUS, status);	
			mongoDBObject.put(DOMAIN, domain);
			mongoDBObject.put(SUCCESSFULLY_DOWNLOADED, successfullyDownloaded);
			mongoDBObject.put(LAST_ERROR_MSG, lastErrorMsg);
			mongoDBObject.put(LAST_TIME_LINKSET, lastTimeLinkset);

			insert(checkBeforeInsert);

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
			downloadUrl = (String) obj.get(DOWNLOAD_URL);
			httpByteSize = (String) obj.get(HTTP_BYTE_SIZE);
			topDataset = (String) obj.get(TOP_DATASET);
			subjectFilterPath = (String) obj.get(SUBJECT_FILTER_PATH);
			objectPath = (String) obj.get(OBJECT_PATH);
			title = (String) obj.get(TITLE);
			httpFormat = (String) obj.get(HTTP_FORMAT);
			httpLastModified = (String) obj.get(HTTP_LAST_MODIFIED);
			format = (String) obj.get(FORMAT);
			status = (String) obj.get(STATUS);
			timeToCreateFilter = (String) obj.get(TIME_TO_CREATE_FILTER);
//			((Number) mapObj.get("autostart")).intValue();
			triples = ((Number) obj.get(TRIPLES)).intValue() ;
			numberOfTriplesLoadedIntoFilter = (String) obj
					.get(NUMBER_OF_TRIPLES_LOADED_INTO_FILTER);
			numberOfObjectTriples = (String) obj.get(NUMBER_OF_OBJECTS_TRIPLES);
			domain = (String) obj.get(DOMAIN);
			successfullyDownloaded = (Boolean) obj.get(SUCCESSFULLY_DOWNLOADED);
			lastErrorMsg = (String) obj.get(LAST_ERROR_MSG);
			lastTimeLinkset = (String) obj.get(LAST_TIME_LINKSET);
			
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

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
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

	public Integer getTriples() {
		return triples;
	}

	public void setTriples(Integer triples) {
		this.triples = triples;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isSuccessfullyDownloadede() {
		return successfullyDownloaded;
	}

	public void setSuccessfullyDownloaded(boolean successfullyDownloaded) {
		this.successfullyDownloaded = successfullyDownloaded;
	}

	public String getLastErrorMsg() {
		return lastErrorMsg;
	}

	public void setLastErrorMsg(String lastErrorMsg) {
		this.lastErrorMsg = lastErrorMsg;
	}

	public String getLastTimeLinkset() {
		return lastTimeLinkset;
	}

	public void setLastTimeLinkset(String lastTimeLinkset) {
		this.lastTimeLinkset = lastTimeLinkset;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}

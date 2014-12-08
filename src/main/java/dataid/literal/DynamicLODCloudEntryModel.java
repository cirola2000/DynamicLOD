package dataid.literal;


public class DynamicLODCloudEntryModel {

	private String accessURL;

	private String subsetURI;
	
	private String datasetURI;

	private double byteSize;

	private String subjectFilterPath;

	private String objectPath;

	private String dataIDFilePath;

	private String timeToCreateFilter;
	
	private String numberOfTriplesLoadedIntoFilter;
	
	private String numberOfObjectTriples;

	public String getAccessURL() {
		return accessURL;
	}

	public void setAccessURL(String accessURL) {
		this.accessURL = accessURL;
	}

	public String getSubsetURI() {
		return subsetURI;
	}

	public void setSubsetURI(String subsetURI) {
		this.subsetURI = subsetURI;
	}

	public String getDatasetURI() {
		return datasetURI;
	}

	public void setDatasetURI(String datasetURI) {
		this.datasetURI = datasetURI;
	}

	public double getByteSize() {
		return byteSize;
	}

	public void setByteSize(double byteSize) {
		this.byteSize = byteSize;
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

	public String getDataIDFilePath() {
		return dataIDFilePath;
	}

	public void setDataIDFilePath(String dataIDFilePath) {
		this.dataIDFilePath = dataIDFilePath;
	}

	public String getTimeToCreateFilter() {
		return timeToCreateFilter;
	}

	public void setTimeToCreateFilter(String timeToCreateFilter) {
		this.timeToCreateFilter = timeToCreateFilter;
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
	
	
	
	
}

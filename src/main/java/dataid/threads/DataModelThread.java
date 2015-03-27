package dataid.threads;

import dataid.DataIDGeneralProperties;
import dataid.filters.FileToFilter;
import dataid.filters.GoogleBloomFilter;

public class DataModelThread {

	public String distributionObjectPath;
	public String subjectFilterPath;

	public String objectDistributionURI;
	public String subjectDistributionURI;

	public String objectDatasetURI;
	public String subjectDatasetURI;

	public int links = 0;
	public int ontologyLinks = 0; 

	public GoogleBloomFilter filter = new GoogleBloomFilter();
	public GoogleBloomFilter ontologyFilter = null;
	
	public DataModelThread() {
		ontologyFilter = new FileToFilter().loadFileToFilter(ontologyFilter, DataIDGeneralProperties.SUBJECT_FILE_LOV_PATH+"subject_filter_lov.nq");
	}

}

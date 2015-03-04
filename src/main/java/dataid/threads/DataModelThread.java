package dataid.threads;

import dataid.filters.GoogleBloomFilter;

public class DataModelThread {

	public String distributionObjectPath;
	public String subjectFilterPath;

	public String objectDistributionURI;
	public String subjectDistributionURI;

	public String objectDatasetURI;
	public String subjectDatasetURI;

	public int links = 0;

	public GoogleBloomFilter filter = new GoogleBloomFilter();

}

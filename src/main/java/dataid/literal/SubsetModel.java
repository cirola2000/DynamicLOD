package dataid.literal;

public class SubsetModel {

	int id;

	String subsetURI;
	
	String datasetURI;

	public String getDatasetURI() {
		return datasetURI;
	}

	public void setDatasetURI(String datasetURI) {
		this.datasetURI = datasetURI;
	}

	String distributionURI;

	public SubsetModel(int id, String dataset, String subset, String distribution) {
		this.id = id;
		this.subsetURI = subset;
		this.distributionURI = distribution;
		this.datasetURI = dataset;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubsetURI() {
		return subsetURI;
	}

	public void setSubsetURI(String subset) {
		this.subsetURI = subset;
	}

	public String getDistribution() {
		return distributionURI;
	}

	public void setDistribution(String distribution) {
		this.distributionURI = distribution;
	}

}

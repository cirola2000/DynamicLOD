package dataid.literal;

public class SubsetModel {

	int id;

	String subsetURI;

	String distributionURI;

	public SubsetModel(int id, String subset, String distribution) {
		this.id = id;
		this.subsetURI = subset;
		this.distributionURI = distribution;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubset() {
		return subsetURI;
	}

	public void setSubset(String subset) {
		this.subsetURI = subset;
	}

	public String getDistribution() {
		return distributionURI;
	}

	public void setDistribution(String distribution) {
		this.distributionURI = distribution;
	}

}

package dataid.server;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.queries.DistributionQueries;

@ViewScoped
@ManagedBean
public class StatisticsBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<DistributionMongoDBObject> dataList;
	private String oi = "dae";

	private void loadDataList() {

		dataList = DistributionQueries.getDistributions();
		
	}

	public ArrayList<DistributionMongoDBObject> getDataList() {
		loadDataList();
		return dataList;
	}

	public String getOi() {
		return oi;
	}

	public void setOi(String oi) {
		this.oi = oi;
	}
	
	

}

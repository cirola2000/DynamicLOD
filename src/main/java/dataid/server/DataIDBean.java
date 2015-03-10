package dataid.server;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.richfaces.application.push.MessageException;
import org.richfaces.application.push.TopicKey;
import org.richfaces.application.push.TopicsContext;

import dataid.DataID;
import dataid.DataIDGeneralProperties;
import dataid.mongodb.actions.MakeLinksets;
import dataid.mongodb.actions.Queries;

@ViewScoped
@ManagedBean
public class DataIDBean implements Serializable, Runnable {

	private static final long serialVersionUID = -6239437588285327644L;

	public DataID dataid = null;

	static private double startTime = 0;
	static private double endTime = 0;

	private String url = "http://localhost:8080/dataids_example/dataidDbpediaEnglish.ttl";

	// log screen
	private String display = "";
	
	// dataid list
	private String dataIDList = "(empty)";
	
	
	// statistic data
	private int numberOfDatasets;
	
	private int numberOfSubsets;
	
	private int numberOfDistributions;
	
	private int numberOfTriples;
	
	
	// info about download
	private String downloadDatasetURI; 
	
	private int downloadNumberOfTriplesLoaded;
	
	private int downloadNumberTotalOfDistributions;
	
	private int downloadNumberOfDownloadedDistributions;
	
	private double downloadedMB;
	
	
	
	// TODO implement a smarter way to choose between add dataid or update graph
	String action = "";
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	TopicsContext topicsContext = TopicsContext.lookup();

	public void push() throws MessageException {
		TopicKey topicKey = new TopicKey("logMessage");
		topicsContext.publish(topicKey, "");
	}
	
	public void pushDataIDList() throws MessageException {
		TopicKey topicKey = new TopicKey("dataIDListMessage");
		TopicKey topicKey2 = new TopicKey("statsMessage");
		
		topicsContext.publish(topicKey, "");
		topicsContext.publish(topicKey2, "");
	}
	
	public void pushDownloadInfo() throws MessageException {
		TopicKey topicKey = new TopicKey("downloadDataIDMessage");
		topicsContext.publish(topicKey, "");
	}
	

	public void start() {
		startTime = 0;
		endTime = 0;
		
		action = "runDataid";

		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
		
		try {
			pushDataIDList();
		} catch (MessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void update() {
		startTime = 0;
		endTime = 0;
		
		action = "updateGraph";

		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();

	}
	
	

	public void run() {
		DataIDGeneralProperties a = new DataIDGeneralProperties();
		a.loadProperties();
		this.setDownloadNumberOfDownloadedDistributions(0);
		this.setDownloadDatasetURI("");
		try {
			this.push();
			
			if(action=="runDataid")
				startDataID();
			else
				updateGraph();
			 
//			this.dataIDList = Queries.getDataIDs();
			this.pushDataIDList();
			 
			 
		} catch (MessageException e) {
			e.printStackTrace();
		}

	}
	

	public void updateGraph() {
		
		MakeLinksets m = new MakeLinksets();
		m.updateLinksets(this);
	}
	
	public void startDataID() {

		dataid = new DataID(this.getUrl(), this);

	}

	public String getDataIDList() {
		this.dataIDList = Queries.getDataIDs();
		return dataIDList;
	}

	public void setDataIDList(String dataIDList) {
	
	}

	public String getNumberOfTriples() {
		this.numberOfTriples = Queries.getNumberOfTriples();
		NumberFormat df = new DecimalFormat("#,###.##");
		return df.format(numberOfTriples);
	}

	public void setNumberOfTriples(int numberOfTriples) {
		this.numberOfTriples = numberOfTriples;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
		

	// mutator methods for statistical data
	public int getNumberOfSubsets() {
		this.numberOfSubsets = Queries.getNumberOfSubsets();
		return numberOfSubsets;
	}

	public void setNumberOfSubsets(int numberOfSubsets) {
		this.numberOfSubsets = numberOfSubsets;
	}

	public int getNumberOfDatasets() {
		this.numberOfDatasets = Queries.getNumberOfDatasets();
		return numberOfDatasets;
	}

	public void setNumberOfDatasets(int numberOfDatasets) {
		this.numberOfDatasets = numberOfDatasets;
	}

	public int getNumberOfDistributions() {
		this.numberOfDistributions = Queries.getNumberOfDistributions();
		return numberOfDistributions;
	}

	public void setNumberOfDistributions(int numberOfDistributions) {
		this.numberOfDistributions = numberOfDistributions;
	}
	

	// mutator methods for download info 
	public String getDownloadDatasetURI() {
		return downloadDatasetURI;
	}

	public void setDownloadDatasetURI(String downloadDatasetURI) {
		this.downloadDatasetURI = downloadDatasetURI;
	}

	public String getDownloadNumberOfTriplesLoaded() {
		NumberFormat df = new DecimalFormat("#,###");
		return df.format(downloadNumberOfTriplesLoaded);
	}

	public void setDownloadNumberOfTriplesLoaded(
			int downloadNumberOfTriplesLoaded) {
		this.downloadNumberOfTriplesLoaded = downloadNumberOfTriplesLoaded;
	}

	public int getDownloadNumberTotalOfDistributions() {
		return downloadNumberTotalOfDistributions;
	}

	public void setDownloadNumberTotalOfDistributions(
			int downloadNumberTotalOfDistributions) {
		this.downloadNumberTotalOfDistributions = downloadNumberTotalOfDistributions;
	}

	public int getDownloadNumberOfDownloadedDistributions() {
		return downloadNumberOfDownloadedDistributions;
	}

	public void setDownloadNumberOfDownloadedDistributions(
			int downloadNumberOfDownloadedDistributions) {
		this.downloadNumberOfDownloadedDistributions = downloadNumberOfDownloadedDistributions;
	}

	public String getDownloadedMB() {
		NumberFormat df = new DecimalFormat("#,###.##");
		return df.format(downloadedMB);
	}

	public void setDownloadedMB(double downloadPercentage) {
		this.downloadedMB = downloadPercentage;
	}

	public void addDisplayMessage(String level, String info) {
		if (startTime == 0)
			startTime = System.currentTimeMillis();
		double endTime2 = endTime;

		endTime = System.currentTimeMillis();

		String color;
		if (level == DataIDGeneralProperties.MESSAGE_ERROR)
			color = "#FF0000";
		else if (level == DataIDGeneralProperties.MESSAGE_INFO)
			color = "#006600";
		else if (level == DataIDGeneralProperties.MESSAGE_WARN)
			color = "#FF6600";
		else
			color = "#000000";

		String timer = ("[" + (endTime - startTime) / 1000) + " +"
				+ (endTime - endTime2) / 1000 + "s]";

		this.setDisplay(this.getDisplay() + "<br><span style=\"color:" + color
				+ "; width:340px\">" + "[" + level + "]" + timer
				+ "<span style=\"margin-left:30px\">" + info + "</span>"
				+ "</span>");
		try {
			this.push();
		} catch (MessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
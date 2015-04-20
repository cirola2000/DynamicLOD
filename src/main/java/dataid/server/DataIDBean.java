package dataid.server;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EventListener;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.richfaces.application.push.MessageException;
import org.richfaces.application.push.TopicKey;
import org.richfaces.application.push.TopicsContext;

import dataid.DataID;
import dataid.DataIDGeneralProperties;
import dataid.mongodb.actions.MakeLinksets;
import dataid.mongodb.actions.Queries;
import dataid.mongodb.queries.DatasetQueries;
import dataid.mongodb.queries.DistributionQueries;
import dataid.mongodb.queries.SubsetQueries;

@ViewScoped
@ManagedBean
public class DataIDBean implements Serializable, Runnable {

	private static final long serialVersionUID = -6239437588285327644L;

	public DataID dataid = null;

	static private double startTime = 0;
	static private double endTime = 0;

	private String url = "https://raw.githubusercontent.com/cirola2000/DynamicLOD/master/src/main/webapp/dataids_example/dataid-news100.ttl";

	// log screen
	private ArrayList<String> display = new ArrayList<String>();

	public boolean updateLog = false;

	public boolean updateDistributionList = false;

	// dataid list
	private String distributionIDList = "(empty)";

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

	public static void push() throws MessageException {
		TopicKey topicKey = new TopicKey("logMessage");
		TopicsContext topicsContext = TopicsContext.lookup();
		topicsContext.publish(topicKey, "");
	}

	public static void pushDistributionList() throws MessageException {
		TopicKey topicKey = new TopicKey("distributionListMessage");
		TopicKey topicKey2 = new TopicKey("statsMessage");
		TopicsContext topicsContext = TopicsContext.lookup();

		topicsContext.publish(topicKey, "");
		topicsContext.publish(topicKey2, "");
	}

	public static void pushDownloadInfo() throws MessageException {
		TopicKey topicKey = new TopicKey("downloadDataIDMessage");
		TopicsContext topicsContext = TopicsContext.lookup();

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
			pushDistributionList();
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
			new Thread(new Runnable() {

				public void run() {
					try {
						while (true) {
							Thread.sleep(1000);
							if (updateLog) {
								DataIDBean.push();
								updateLog = false;
							}
							if (updateDistributionList) {
								DataIDBean.pushDistributionList();
								updateDistributionList = false;
							}
						}
					} catch (Exception e) {
					}
				}
			}).start();

			if (action == "runDataid")
				startDataID();
			else
				updateGraph();

			// this.dataIDList = Queries.getDataIDs();
			pushDistributionList();

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

	public String getDistributionList() {
		this.distributionIDList = Queries.getHTMLDistributionStatus();
		return distributionIDList;
	}

	public void setDataIDList(String dataIDList) {

	}

	public String getNumberOfTriples() {
		this.numberOfTriples = DistributionQueries.getNumberOfTriples();
		NumberFormat df = new DecimalFormat("#,###.##");
		return df.format(numberOfTriples);
	}

	public void setNumberOfTriples(int numberOfTriples) {
		this.numberOfTriples = numberOfTriples;
	}

	public String getDisplay() {
		if (display.size() > 50)
			display.remove(display.size() - 50);

		if (display.size() > 0)
			return display.get(display.size() - 1);
		else
			return "";
	}

	public void setDisplay(String display) {
		this.display.add(display);
	}

	// mutator methods for statistical data
	public int getNumberOfSubsets() {
		this.numberOfSubsets = SubsetQueries.getNumberOfSubsets();
		return numberOfSubsets;
	}

	public void setNumberOfSubsets(int numberOfSubsets) {
		this.numberOfSubsets = numberOfSubsets;
	}

	public int getNumberOfDatasets() {
		this.numberOfDatasets = DatasetQueries.getNumberOfDatasets();
		return numberOfDatasets;
	}

	public void setNumberOfDatasets(int numberOfDatasets) {
		this.numberOfDatasets = numberOfDatasets;
	}

	public int getNumberOfDistributions() {
		this.numberOfDistributions = DistributionQueries
				.getNumberOfDistributions();
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
	
	public void shouldUpdateLog(double downloadPercentage) {
		this.downloadedMB = downloadPercentage;
	}
	
	

	public void addDisplayMessage(String level, String info) {

		updateLog = true;

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
		// try {
		// // push();
		// } catch (MessageException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}
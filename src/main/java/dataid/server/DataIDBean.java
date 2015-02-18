package dataid.server;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.tools.ant.taskdefs.Sleep;
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

	private String url = "http://localhost:8080/dataids_example/dataid-news100.ttl";

	private String display = "";
	
	private String dataIDList = "(empty)";
	
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
		TopicKey topicKey = new TopicKey("shellMessage");
		topicsContext.publish(topicKey, "");
	}
	
	public void pushDataIDList() throws MessageException {
		TopicKey topicKey = new TopicKey("dataIDListMessage");
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
		return this.dataIDList;
	}

	public void setDataIDList(String dataIDList) {
	
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
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
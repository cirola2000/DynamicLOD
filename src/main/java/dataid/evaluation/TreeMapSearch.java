package dataid.evaluation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import dataid.files.FileIterator;
import dataid.files.SerializeObject;
import dataid.utils.Timer;

public class TreeMapSearch implements SearchAlgorithm {
	
	final static Logger logger = Logger.getLogger(TreeMapSearch.class);
	
	public TreeMap<String, Integer> tm = new TreeMap<String, Integer>();
	 
	int positives = 0;
	
	String timeToCreate;
	
	String timeToSearch;
	
	int subjects = 0;
	
	Timer t = new Timer();
	
	SerializeObject s = null;

	public int getPositives() {
		return positives;
	}
	public int getSubjects() {
		return subjects;
	}
	
	public void AddElements(String file) throws FileNotFoundException {

		t.startTimer();

		for (String subject : new FileIterator(file)) {
			tm.put(subject, null);
		}

		timeToCreate = t.stopTimer();
		logger.info("Time to create tree: " + timeToCreate);
	}

	public void SearchElements(String file) throws FileNotFoundException {

		t.startTimer();

		for (String object : new FileIterator(file)) {
			if (tm.containsKey(object)) {
//				logger.debug(object);
				positives++;
			}
			subjects++;
		}
		timeToSearch = t.stopTimer();
		logger.info("Time search objects on tree: " + timeToSearch);
	}

	public void Save(String file) throws IOException {
		logger.info("saving tree");
		s = new SerializeObject(file);
		s.save(tm);	
	}

	public long getFileSize() {
		return s.getFileSize();
	}
	
	public String getTimeToCreate() {
		return timeToCreate;
	}

	public String getTimeToSearch() {
		return timeToSearch;
	}
}

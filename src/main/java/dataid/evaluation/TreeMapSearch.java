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
	
	Timer t = new Timer();
	
	SerializeObject s = null;

	public int getPositives() {
		return positives;
	}
	
	public void AddElements(String file) throws FileNotFoundException {

		t.startTimer();

		for (String subject : new FileIterator(file)) {
			tm.put(subject, null);
		}

		logger.info("Time to create tree: " + t.stopTimer());
	}

	public void SearchElements(String file) throws FileNotFoundException {

		t.startTimer();

		for (String object : new FileIterator(file)) {
			if (tm.containsKey(object)) {
//				logger.debug(object);
				positives++;
			}
		}
		logger.info("Time search objects on tree: " + t.stopTimer());
	}

	public void Save(String file) throws IOException {
		s = new SerializeObject(file);
		s.save(tm);	
	}

	public long getFileSize() {
		return s.getFileSize();
	}
}

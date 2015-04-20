package dataid.evaluation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import dataid.files.FileIterator;
import dataid.files.SerializeObject;
import dataid.utils.Timer;

public class HashMapSearch implements SearchAlgorithm{
	
	final static Logger logger = Logger.getLogger(HashMapSearch.class);
	
	int positives = 0;
	
	SerializeObject s = null;
	
	HashMap<Integer, String> hs = new HashMap<Integer, String>();
	
	Timer t = new Timer();
	
	public int getPositives() {
		return positives;
	}
	
	public void AddElements(String file) throws FileNotFoundException {

		t.startTimer();

		for (String subject : new FileIterator(file)) {
			hs.put(subject.hashCode(), subject);
		}

		logger.info("Time to create HasMap: " + t.stopTimer());
	}
	
	public void SearchElements(String file) throws FileNotFoundException {

		t.startTimer();

		for (String object : new FileIterator(file)) {
			if (hs.containsKey(object.hashCode())) {
//				logger.debug(object);
				positives++;
			}
		}
		logger.info("Time search objects on tree: " + t.stopTimer());
	}

	public void Save(String file) throws IOException {
		s = new SerializeObject(file);
		s.save(hs);			
	}
	
	public long getFileSize() {
		return s.getFileSize();
	}
}

package dataid.evaluation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import dataid.files.FileIterator;
import dataid.files.SerializeObject;
import dataid.utils.Timer;

public class HashMapSearch implements SearchAlgorithm{
	
	final static Logger logger = Logger.getLogger(HashMapSearch.class);
	
	int positives = 0;
	
	String timeToCreate;
	
	String timeToSearch;
	
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

		timeToCreate = t.stopTimer();
		logger.info("Time to create HasMap: " +timeToCreate);
	}
	
	public void SearchElements(String file) throws FileNotFoundException {

		t.startTimer();

		for (String object : new FileIterator(file)) {
			if (hs.containsKey(object.hashCode())) {
//				logger.debug(object);
				positives++;
			}
		}
		timeToSearch = t.stopTimer();
		logger.info("Time search objects on hasMap: " + timeToSearch);
	}

	public void Save(String file) throws IOException {
		logger.info("saving hashmap");
		s = new SerializeObject(file);
		s.save(hs);			
	}
	
	public void load(String file)  throws Exception{
		// Read from disk using FileInputStream
		logger.info("loading tree file: " +file);
		
		FileInputStream f_in = new 
			FileInputStream(file);

		// Read object using ObjectInputStream
		ObjectInputStream obj_in = 
			new ObjectInputStream (f_in);

		// Read an object
		Object obj = obj_in.readObject();

		if (obj instanceof TreeSet)
		{
			// Cast object to a Vector
			hs = (HashMap<Integer, String>) obj;

			// Do something with vector....
		}
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

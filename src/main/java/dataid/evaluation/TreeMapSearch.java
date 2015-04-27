package dataid.evaluation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import dataid.files.FileIterator;
import dataid.files.SerializeObject;
import dataid.utils.Timer;

public class TreeMapSearch implements SearchAlgorithm {
	
	final static Logger logger = Logger.getLogger(TreeMapSearch.class);
	
	public TreeSet<String> tm = new TreeSet<String>();
	 
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

		logger.info("Adding elements: " + file);
		
		t.startTimer();

		for (String subject : new FileIterator(file)) {
			tm.add(subject);
		}

		timeToCreate = t.stopTimer();
		logger.info("Time to add elements in tree: " + timeToCreate);
		
	}

	public void SearchElements(String file) throws FileNotFoundException {

		t.startTimer();

		for (String object : new FileIterator(file)) {
			if (tm.contains(object)) {
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
			tm = (TreeSet<String>) obj;

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

package dataid.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;

import dataid.files.FileIterator;
import dataid.filters.GoogleBloomFilter;
import dataid.utils.Timer;

public class BloomFilterSearch implements SearchAlgorithm {

	final static Logger logger = Logger.getLogger(BloomFilterSearch.class);

	GoogleBloomFilter filter = null;
	
	NumberFormat formatter = new DecimalFormat("#0.0000000000000000");

	Timer t = new Timer();
	
	File f=null;
	
	int size;
	
	double fpp;

	int positives = 0;

	public int getPositives() {
		// TODO Auto-generated method stub
		return positives;
	}

	public BloomFilterSearch(int size, double fpp) {
		this.size = size;
		this.fpp = fpp;
	}
	
	public void AddElements(String file) throws FileNotFoundException {

		filter = new GoogleBloomFilter((int) size, fpp);
		System.out.println();
		logger.info("Bloom filter fpp: " + formatter.format(fpp));

		t.startTimer();
		for (String subject : new FileIterator(file)) {
			filter.add(subject);
		}

		logger.info("Time to create Bloom filter: " + t.stopTimer());

	}

	public void SearchElements(String file) throws FileNotFoundException {
		t.startTimer();
		for (String object : new FileIterator(file)) {
			try {
				if (filter.compare(object)) {
//					logger.debug(object);
					positives++;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("Time search objects on Bloom filter: " + t.stopTimer());
	}

	public void Save(String file) throws IOException {
		f = new File(file);
		filter.filter.writeTo(new FileOutputStream(f));
		
	}

	public long getFileSize() {
		return f.length();
	}
	
	

}

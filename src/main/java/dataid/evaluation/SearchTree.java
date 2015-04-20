package dataid.evaluation;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.junit.Test;

import dataid.DataIDGeneralProperties;
import dataid.files.FileIterator;
import dataid.files.SerializeObject;
import dataid.filters.GoogleBloomFilter;
import dataid.utils.Timer;

public class SearchTree implements Serializable {

	final static Logger logger = Logger.getLogger(SearchTree.class);

	@Test
	public void SearchTree() {
		try {
			new DataIDGeneralProperties().loadProperties();

			NumberFormat formatter = new DecimalFormat("#0.0000000000000000");

			int truePositive = 0;
			int falsePositive = 0;
			int maybe = 0;
			double precision;
			double recall;
			double fMeasure;

			// downloding distributions
			// DownloadAndSaveDistribution dist1 = new
			// DownloadAndSaveDistribution("http://downloads.dbpedia.org/3.9/en/geonames_links_en.nt.bz2");
			// dist1.downloadDistribution();
			// DownloadAndSaveDistribution dist2 = new
			// DownloadAndSaveDistribution("http://downloads.dbpedia.org/3.9/en/homepages_en.nt.bz2");
			// dist2.downloadDistribution();

			// String file1
			// =DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH+dist1.getFileName();
			// String file2 =
			// DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+dist2.getFileName();

			String file1 = DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
					+ "geonames_links_en.nt";
			String file2 = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
					+ "homepages_en.nt";

			TreeMap<String, Integer> tm = new TreeMap<String, Integer>();
			Timer t = new Timer();
			t.startTimer();

			for (String subject : new FileIterator(file1)) {
				tm.put(subject, null);
			}
			logger.info("Time to create tree: " + t.stopTimer());

			t.startTimer();

			for (String object : new FileIterator(file2)) {
				if (tm.containsKey(object)) {
					logger.debug(object);
					truePositive++;
				}
			}
			logger.info("Time search objects on tree: " + t.stopTimer());

			GoogleBloomFilter filter;
			double fpp =(double) 1 / tm.size();

			filter = new GoogleBloomFilter((int) tm.size(), fpp);
			System.out.println();
			logger.info("Bloom filter fpp: " + formatter.format(fpp));

			t.startTimer();
			for (String subject : new FileIterator(file1)) {
				filter.add(subject);
			}
			logger.info("Time to create Bloom filter: " + t.stopTimer());

			t.startTimer();
			for (String object : new FileIterator(file2)) {
				if (filter.compare(object)) {
					logger.debug(object);
					maybe++;
				}
			}
			logger.info("Time search objects on Bloom filter: " + t.stopTimer());

			falsePositive = maybe - truePositive;
			precision = (double) truePositive /(truePositive + falsePositive);

			// recal is always 1
			recall = (double) truePositive/truePositive;
			
			fMeasure = 2*((precision*recall)/ (precision+recall));
			


			logger.info("Bloom filter false positives: " + falsePositive);
			logger.info("Bloom filter precision: " + formatter.format(precision));
			logger.info("Bloom filter recall: " + formatter.format(recall));
			logger.info("Bloom filter fmeasure: " + formatter.format(fMeasure));
			

			SerializeObject s = new SerializeObject("/home/ciro/dataid/bumbum");
			s.save(tm);

			System.out.println(s.getFileSize());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

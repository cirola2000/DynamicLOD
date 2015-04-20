package dataid.evaluation;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.junit.Test;

import dataid.DataIDGeneralProperties;
import dataid.download.DownloadAndSaveDistribution;
import dataid.mongodb.objects.EvaluationMongoDBObject;

public class Evaluation implements Serializable {

	final static Logger logger = Logger.getLogger(Evaluation.class);

	String file1 = null;
	String file2 = null;


	double fpp = 0;
	int truePositive = 0;
	int falsePositive = 0;
	double precision;
	double recall;
	double fMeasure; 

	NumberFormat formatter = new DecimalFormat("#0.0000000000000000");

	
	HashMapSearch hashMap = new HashMapSearch();
	TreeMapSearch treeMap = new TreeMapSearch(); 
	BloomFilterSearch filter = null;


	@Test
	public void SearchTree() {
		try {
			new DataIDGeneralProperties().loadProperties();

			// downloding distributions
//			 DownloadAndSaveDistribution dist1 = new
//			 DownloadAndSaveDistribution(
//			 "http://downloads.dbpedia.org/3.9/en/article_templates_en.nt.bz2");
//			 dist1.downloadDistribution();
//			 DownloadAndSaveDistribution dist2 = new
//			 DownloadAndSaveDistribution(
//			 "http://downloads.dbpedia.org/3.9/en/interlanguage_links_chapters_en.nt.bz2");
//			 dist2.downloadDistribution();
			
//			 String file1 =
//			 DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
//			 + dist1.getFileName();
//			 String file2 =
//			 DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
//			 + dist2.getFileName();

			file1 = DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
					+ "geonames_links_en.nt";
			file2 = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
					+ "homepages_en.nt";

			treeMap.AddElements(file1);

			treeMap.SearchElements(file2);			
			treeMap.Save(DataIDGeneralProperties.BASE_PATH+"treeMap");

			System.out.println();
			
			hashMap.AddElements(file1);

			hashMap.SearchElements(file2);
			hashMap.Save(DataIDGeneralProperties.BASE_PATH+"hashMap");

			fpp = (double) 1 / treeMap.tm.size();
			
			filter = new BloomFilterSearch(treeMap.tm.size(), fpp);
			
			filter.AddElements(file1);

			filter.SearchElements(file2);
			filter.Save(DataIDGeneralProperties.BASE_PATH+"filter");


			
			truePositive = treeMap.getPositives();

			falsePositive = filter.getPositives() - truePositive;

			precision = (double) truePositive / (truePositive + falsePositive);

			// recal is always 1
			recall = (double) truePositive / truePositive;

			fMeasure = 2 * ((precision * recall) / (precision + recall));

			System.out.println();

			logger.info("Tree true positives: " + truePositive);
			logger.info("Bloom filter true positives: " + filter.getPositives());
			logger.info("Bloom filter precision: "
					+ formatter.format(precision));
			logger.info("Bloom filter recall: " + formatter.format(recall));
			logger.info("Bloom filter fmeasure: " + formatter.format(fMeasure));
			
			
			EvaluationMongoDBObject e = new EvaluationMongoDBObject(new ObjectId().get().toString());
			

			
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}

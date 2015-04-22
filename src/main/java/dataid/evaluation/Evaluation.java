package dataid.evaluation;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

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

	@Test
	public void SearchTree() {
		new DataIDGeneralProperties().loadProperties();

		// downloding distributions
		// DownloadAndSaveDistribution dist1 = new
		// DownloadAndSaveDistribution(
		// "http://downloads.dbpedia.org/3.9/en/article_templates_en.nt.bz2");
		// dist1.downloadDistribution();
		// DownloadAndSaveDistribution dist2 = new
		// DownloadAndSaveDistribution(
		// "http://downloads.dbpedia.org/3.9/en/interlanguage_links_chapters_en.nt.bz2");
		// dist2.downloadDistribution();

		// String file1 =
		// DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
		// + dist1.getFileName();
		// String file2 =
		// DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
		// + dist2.getFileName();

		ArrayList<String> ar = new ArrayList<String>() {
			{

				add("10milhoes.nt");

			}
		};

		for (String file2 : DBPediaLinks.links) {
			file2 = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
					+ file2;
			file2 = file2.replace(".bz2", "");

			for (String file1 : DBPediaLinks.links) {
				HashMapSearch hashMap = new HashMapSearch();
				TreeMapSearch treeMap = new TreeMapSearch();
				BloomFilterSearch filter = null;
				file1 = file1.replace(".bz2", "");

				file1 = DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH + file1;
				EvaluationMongoDBObject e = new EvaluationMongoDBObject(file2
						+ file1);
				System.out.println(file2 + " " + file1);

				int size = 0;

				if (e.getDsObject() == null) {
					try {
						if (treeMap.tm.size() < 1) {
							treeMap.AddElements(file1);
							size = treeMap.tm.size();
							treeMap.SearchElements(file2);
							treeMap.Save(DataIDGeneralProperties.BASE_PATH
									+ "treeMap");
							treeMap.tm = null;

							fpp = (double) 1 / size;

							filter = new BloomFilterSearch(size, fpp);

							filter.AddElements(file1);
							filter.SearchElements(file2);

							filter.Save(DataIDGeneralProperties.BASE_PATH
									+ "filter");

							filter.filter = null;

							hashMap.AddElements(file1);
							hashMap.SearchElements(file2);
							hashMap.Save(DataIDGeneralProperties.BASE_PATH
									+ "hashMap");

							hashMap.hs = null;


							System.out.println();

							truePositive = treeMap.getPositives();

							falsePositive = filter.getPositives()
									- truePositive;

							precision = (double) truePositive
									/ (truePositive + falsePositive);

							// recal is always 1
							recall = (double) truePositive / truePositive;

							fMeasure = 2 * ((precision * recall) / (precision + recall));

							System.out.println();

							logger.info("Tree true positives: " + truePositive);
							logger.info("Bloom filter true positives: "
									+ filter.getPositives());
							logger.info("Bloom filter precision: "
									+ formatter.format(precision));
							logger.info("Bloom filter recall: "
									+ formatter.format(recall));
							logger.info("Bloom filter fmeasure: "
									+ formatter.format(fMeasure));

							e = new EvaluationMongoDBObject(file2 + file1);
							e.setDsObject(file2);
							e.setDsSubject(file1);
							e.setDsObjectTriples(treeMap.getSubjects());
							e.setDsSubjectTriples(size);
							e.setPositivesBloom(filter.getPositives());
							e.setPositivesHash(hashMap.getPositives());
							e.setTimeCreateBloom(filter.getTimeToCreate());
							e.setTimeCreateHash(hashMap.getTimeToCreate());
							e.setTimeCreateTree(treeMap.getTimeToCreate());
							e.setTimeSearchBloom(filter.getTimeToSearch());
							e.setTimeSearchHash(hashMap.getTimeToSearch());
							e.setTimeSearchTree(treeMap.getTimeToSearch());
							e.setTruePositives(treeMap.getPositives());

							e.setFilterSize(filter.getFileSize());
							e.setHashSize(hashMap.getFileSize());
							e.setTreeSize(treeMap.getFileSize());

							e.setPrecision(precision);
							e.setRecall(recall);
							e.setFmeasure(fMeasure);
							e.updateObject(true);
						}
					} catch (Exception en) {
						en.printStackTrace();
					}

				} else {
					System.out.println("jumping " + file2 + file1);
				}
			}
		}

	}

}

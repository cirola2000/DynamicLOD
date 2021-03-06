package dataid.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
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
import dataid.mongodb.objects.LinksetMongoDBObject;
import dataid.mongodb.queries.LinksetQueries;
import dataid.utils.FileUtils;

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

	ArrayList<LinksetMongoDBObject> linkSets = LinksetQueries.getLinksets();

	ArrayList<String> objects = new ArrayList<String>();
	ArrayList<String> subjects = new ArrayList<String>();

	private void loadLinksets() {
		for (LinksetMongoDBObject linksetMongoDBObject : linkSets) {
			String object = linksetMongoDBObject.getObjectsDistributionTarget();
			if (!objects.contains(object))
				objects.add(object);
			
			String subject = linksetMongoDBObject
					.getSubjectsDistributionTarget();
			if (!subjects.contains(subject))
				subjects.add(subject);
			
			if (!objects.contains(subject))
				objects.add(subject);
			
			if (!subjects.contains(object))
				subjects.add(object);
		}
	}

	public void makeLinksets() {

		loadLinksets();
		
		TreeMapSearch t = new TreeMapSearch();
		

		for (String object: objects) {
			ArrayList<LinksetMongoDBObject> linkSets = LinksetQueries.getLinksetsOutDegreeByDistribution(object);
			
			System.out.println(object);
			System.out.println(getFile(object));
			t.tm=null;
			
			for (LinksetMongoDBObject linksetMongoDBObject : linkSets) {
				if(linksetMongoDBObject.getSubjectsDistributionTarget().equals("http://dbpedia.org/ontology/") || !linksetMongoDBObject.getSubjectsDistributionTarget().contains("pedia"))
//				if(linksetMongoDBObject.getLinks()>0 && linksetMongoDBObject.getOntologyLinks() ==0){
					try {
						if(t.tm==null)
							t.load(getFile(DataIDGeneralProperties.BASE_PATH+"tree/"+FileUtils.stringToHash(linksetMongoDBObject.getSubjectsDistributionTarget())));
						t.SearchElements(DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+getFile(linksetMongoDBObject.getObjectsDistributionTarget()));
						System.out.println(t.positives);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				System.out.println("----"+linksetMongoDBObject.getObjectsDistributionTarget());
//				linksetMongoDBObject.setOntologyLinks(t.getPositives());
				linksetMongoDBObject.updateObject(true);
				t.positives = 0;
//				}
			}
		}
		
		
	}

	private String getFile(String s){
		return s.replace("http://downloads.dbpedia.org/3.9/en/", "")
				.replace(".bz2", "")
				.replace("http://brown.nlp2rdf.org/lod/", "")
				.replace("https://raw.githubusercontent.com/AKSW/n3-collection/master/","");
	}
	
	public void makeTrees() {

		loadLinksets();
		String treePath = DataIDGeneralProperties.BASE_PATH + "/tree/";

		for (String string : subjects) {
			string = getFile(string);
			System.out.println("--"+string);
//			string = FileUtils.stringToHash(string);
			
			TreeMapSearch treeMap = new TreeMapSearch();

			try {
				File f = new File(treePath + string);
				if (!f.exists()) {
					treeMap.AddElements(DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
							+ string);
					treeMap.Save(treePath + string);
					treeMap.tm = null;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	// object
	static public ArrayList<String> objectList = new ArrayList<String>() {
		{
			
			add("10m");
		}
	};
	
	static public ArrayList<String> subjectList = new ArrayList<String>() {
		{
			
			add("opa");
		}
	};
	

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

		
		

		for (String objectFileName : objectList) {
//			for (String file2 : DBPediaLinks.links) {
//			objectFileName = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
//					+ objectFileName;
			objectFileName = DataIDGeneralProperties.BASE_PATH+"objects/"
					+ objectFileName;
			objectFileName = objectFileName.replace(".bz2", "");

			for (String subjectFileName : subjectList) {
//				for (String file1 : DBPediaLinks.links) {
				HashMapSearch hashMap = new HashMapSearch();
				TreeMapSearch treeMap = new TreeMapSearch();
				BloomFilterSearch filter = null;
				subjectFileName = subjectFileName.replace(".bz2", "");

//				subjectFileName = DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
//						+ subjectFileName;
				subjectFileName = DataIDGeneralProperties.BASE_PATH+"subjects/"
						+ subjectFileName;
				
				EvaluationMongoDBObject e = new EvaluationMongoDBObject(objectFileName
						+ subjectFileName);
				System.out.println(objectFileName + " " + subjectFileName);

				int size = 1000000;

//				if (e.getDsObject() == null) {
					if (true) {
					try {
						if (treeMap.tm.size() < 1) {

//							size = treeMap.tm.size();
//							hashMap.Save("/tmp/opa");
//							hashMap.load("/tmp/opa");

							
//							treeMap.AddElements(subjectFileName);
//							treeMap.SearchElements(objectFileName);

//							hashMap.AddElements(subjectFileName);
//							hashMap.SearchElements(objectFileName);

							fpp = (double) 0.9 / size;
							filter = new BloomFilterSearch(size, fpp);
							filter.AddElements(subjectFileName);
							filter.SearchElements(objectFileName);

							
							
							
//							treeMap.Save(DataIDGeneralProperties.BASE_PATH
//									+ "treeMap");
//							treeMap.tm = null;

							System.out.println("size: "+size);


//							filter.Save(DataIDGeneralProperties.BASE_PATH
//									+ "filter");

//							filter.filter = null;

//							hashMap.AddElements(file1);
//							hashMap.SearchElements(file2);
//							hashMap.Save(DataIDGeneralProperties.BASE_PATH
//									+ "hashMap");

//							hashMap.hs = null;

							System.out.println();

							truePositive = treeMap.getPositives();

//							falsePositive = filter.getPositives()
//									- truePositive;

//							precision = (double) truePositive
//									/ (truePositive + falsePositive);

							// recal is always 1
//							recall = (double) truePositive / truePositive;
//
//							fMeasure = 2 * ((precision * recall) / (precision + recall));
//
//							System.out.println();
//
//							logger.info("Tree true positives: " + truePositive);
//							logger.info("Bloom filter true positives: "
//									+ filter.getPositives());
//							logger.info("Bloom filter precision: "
//									+ formatter.format(precision));
//							logger.info("Bloom filter recall: "
//									+ formatter.format(recall));
//							logger.info("Bloom filter fmeasure: "
//									+ formatter.format(fMeasure));

//							e = new EvaluationMongoDBObject(file2 + file1);
//							e.setDsObject(file2);
//							e.setDsSubject(file1);
//							e.setDsObjectTriples(treeMap.getSubjects());
//							e.setDsSubjectTriples(size);
//							e.setPositivesBloom(filter.getPositives());
//							e.setPositivesHash(hashMap.getPositives());
//							e.setTimeCreateBloom(filter.getTimeToCreate());
//							e.setTimeCreateHash(hashMap.getTimeToCreate());
//							e.setTimeCreateTree(treeMap.getTimeToCreate());
//							e.setTimeSearchBloom(filter.getTimeToSearch());
//							e.setTimeSearchHash(hashMap.getTimeToSearch());
//							e.setTimeSearchTree(treeMap.getTimeToSearch());
//							e.setTruePositives(treeMap.getPositives());
//
//							e.setFilterSize(filter.getFileSize());
//							e.setHashSize(hashMap.getFileSize());
//							e.setTreeSize(treeMap.getFileSize());
//
//							e.setPrecision(precision);
//							e.setRecall(recall);
//							e.setFmeasure(fMeasure);
//							e.updateObject(true);
						}
					} catch (Exception en) {
						en.printStackTrace();
					}

				} else {
					System.out.println("jumping " + objectFileName + subjectFileName);
				}
			}
		}

	}

}

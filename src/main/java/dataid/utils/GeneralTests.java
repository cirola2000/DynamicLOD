package dataid.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dataid.filters.FileToFilter;
import dataid.filters.GoogleBloomFilter;
import dataid.mongodb.DataIDDB;
import dataid.mongodb.actions.DataThread;
import dataid.mongodb.actions.JobThread;
import dataid.mongodb.actions.Queries;
import dataid.mongodb.objects.DistributionMongoDBObject;

public class GeneralTests {

	
	public void RegexTest() {
		String u = "<http://aksw.org/N3/News-100/40#char=58,60> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String> .";
		u = "<http://aksw.org/N3/News-100/40#char=58,60> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> \"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String\"@en .";

		u = "<http://aksw.org/N3/News-100/72#char=14,29> <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#anchorOf> \"Golf von Mexiko\"^^<http://www.w3.org/2001/XMLSchema#string> .";
		// u =
		// "<http://aksw.org/N3/News-1p://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String> .";
		//
		Timer t = new Timer();
		t.startTimer();
		Pattern pattern = Pattern
				.compile("^(<[^>]+>)\\s+(<[^>]+>)\\s(.*)(\\s\\.)");

		Matcher matcher = pattern.matcher(u);
		if (!matcher.matches()) {
			System.out.println("Nop!");
		}

		System.out.println(matcher.groupCount());

		System.out.println(matcher.group(0));
		System.out.println(matcher.group(1));
		System.out.println(matcher.group(2));
		System.out.println(matcher.group(3));
		System.out.println(t.stopTimer());
	}

	public void FilterTest() {

		Timer t = new Timer();
		t.startTimer();

		try {

			System.out.println("Updating linksets...");

			// get distribution collection
			DBCollection distributionCollection = DataIDDB.getInstance()
					.getCollection(DistributionMongoDBObject.COLLECTION_NAME);

			// getting all distributions from mongoDB
			DBCursor distributions = distributionCollection.find();

			// load distributions
			if (distributions.hasNext()) {
				// creating a list of threads to process filters
				List<DataThread> listOfDataThreads = new ArrayList<DataThread>();

				DBObject distribution = distributions.next();

				System.out.println();
				System.out.println(distribution.get(
						DistributionMongoDBObject.ACCESS_URL).toString()
						+ " looking for other distributions that describes: "
						+ distribution.get(DistributionMongoDBObject.AUTHORITY)
								.toString());

				// find which filters should be opened for this distribution
				ArrayList<DistributionMongoDBObject> q = Queries
						.getDistributionsByAuthority(distribution
								.get(DistributionMongoDBObject.AUTHORITY_OBJECTS));

				System.out.println(q.size()+" distributions found");

				int i = 0;
				for (DistributionMongoDBObject a : q) {
					if (!a.getSubjectFilterPath()
							.equals(distribution
									.get(DistributionMongoDBObject.SUBJECT_FILTER_PATH)
									.toString())) {

						System.out.println("====+++++==========");
						System.out.println("Distribution "
								+ i++
								+ ": "
								+ a.getSubjectFilterPath());
						System.out.println("Filter Path: "+a.getSubjectFilterPath());

						DataThread dataThread = new DataThread();
						// save dataThread object
						GoogleBloomFilter filter = new GoogleBloomFilter();

						dataThread.subjectFilterPath = a.getSubjectFilterPath();
						try {
							filter.loadFilter(dataThread.subjectFilterPath);
						} catch (Exception e) {
							e.printStackTrace();
						}

						dataThread.filter = filter;

						dataThread.subjectDistributionURI = a.getAccessUrl();
						dataThread.subjectDatasetURI = a.getTopDataset();

						dataThread.objectDatasetURI = distribution.get(
								DistributionMongoDBObject.TOP_DATASET)
								.toString();
						dataThread.objectDistributionURI = distribution.get(
								DistributionMongoDBObject.ACCESS_URL)
								.toString();
						dataThread.distributionObjectPath = distribution.get(
								DistributionMongoDBObject.OBJECT_PATH)
								.toString();

						listOfDataThreads.add(dataThread);
					}
				}

				System.out.println();

				// reading object distribution file here
				BufferedReader br = new BufferedReader(new FileReader(
						distribution.get(DistributionMongoDBObject.OBJECT_PATH)
								.toString()));

				System.out.println("Loading objects from: "
						+ distribution.get(
								DistributionMongoDBObject.OBJECT_PATH)
								.toString()
						+ ". This might take a time, please be patient.");

				String sCurrentLine;

				// loading objects and creating a buffer to send to threads
				int bufferSize = 500;

				String[] buffer = new String[bufferSize];

				int bufferIndex = 0;

				if (listOfDataThreads.size() > 0)
					while ((sCurrentLine = br.readLine()) != null) {
						buffer[bufferIndex] = (sCurrentLine);
						bufferIndex++;
						int threadIndex = 0;

						
						// if buffer is full, start the threads!
						if (bufferIndex % bufferSize == 0) {
							Thread[] threads = new Thread[listOfDataThreads
									.size()];
							for (DataThread dataThread2 : listOfDataThreads) {
								threads[threadIndex] = new Thread(
										new JobThread(dataThread2,
												buffer.clone(), bufferSize));
								
								
								threads[threadIndex].start();
								threadIndex++;
							}

							// wait all threads finish and then start load
							// buffer again
							for (int d = 0; d < threads.length; d++)
								threads[d].join();

							bufferIndex = 0;

						}
					}

				System.out.println("Loaded objects from: "
						+ distribution.get(
								DistributionMongoDBObject.OBJECT_PATH)
								.toString());

				// save linksets into mongodb
				for (DataThread t0 : listOfDataThreads) {
					System.out.println();
					System.out.println("Distribution Object Path: "+t0.distributionObjectPath);
					System.out.println("Object distribution URI: "+t0.objectDistributionURI);
					System.out.println("Object dataset URI: "+t0.objectDatasetURI);
					System.out.println("Subject dataset URI: "+t0.subjectDatasetURI);
					System.out.println("Subject distribution URI: "+t0.subjectDistributionURI);
					System.out.println("Subject filter path: "+t0.subjectFilterPath);
					System.out.println("links: "+ t0.links);
				
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Time to update linksets: " + t.stopTimer() + "s");
	}
	
	
	
	public void testOneFilter(){
		try {
		GoogleBloomFilter f = new GoogleBloomFilter(300000,0.001);
		
		FileToFilter ftf = new FileToFilter();
		ftf.loadFileToFilter(f, "/home/ciro/dataid/subjects/subject_distribution_RSS-500.nt", null);
		// mudar esse
		f.saveFilter("/tmp/cirola");
//		f.loadFilter("/home/ciro/dataid/filters/subject_filter_News-100.nt");
		f.loadFilter("/tmp/cirola");
		
		
//		f.add("Casdasdas");
			System.out.println(f.compare("<http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String>"));

		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	@Test
	    public void Ciro() {
		Random random = new Random();
	        // convert object into funnel - specific to google implementation
	        int totals=100000;
	        Funnel<String> funnel = new Funnel<String>() {
	            public void funnel(String s, PrimitiveSink primitiveSink) {
	                primitiveSink.putString(s, Charsets.UTF_8);
	            }
	        };

	        BufferedReader br = null;

	        BloomFilter bloomFilter = BloomFilter.create(funnel, totals);
	        for (int i = 0; i < totals; i++) {
	            Integer value = random.nextInt(10000);
	            // add only even number to bloom filter
	            if ((value % 2) == 0) {
	                String key = "key" + value;
	                //insert only even values into bloom filter
//	                bloomFilter.put(key);

	            }
	        }
	        
	        
	        
	       			
			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(
						"/home/ciro/dataid/subjects/subject_distribution_RSS-500.nt"));
				while ((sCurrentLine = br.readLine()) != null) {
					bloomFilter.put(sCurrentLine);
					System.out.println("adding "+sCurrentLine);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
				} catch (IOException ex) {
				}
			}
	        
	        
	      System.out.println(bloomFilter.mightContain("<http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String>"));
	    
	}
	
	
	
	

}

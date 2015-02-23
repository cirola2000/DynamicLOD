package dataid.mongodb.actions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dataid.DataIDGeneralProperties;
import dataid.filters.GoogleBloomFilter;
import dataid.mongodb.DataIDDB;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.objects.LinksetMongoDBObject;
import dataid.server.DataIDBean;
import dataid.utils.Timer;

public class MakeLinksets {

	@Test
	public void updateLinksets(DataIDBean bean) {

		Timer t = new Timer();
		t.startTimer();

		try {

			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
					"Updating linksets...");

			// get distribution collection
			DBCollection distributionCollection = DataIDDB.getInstance()
					.getCollection(DistributionMongoDBObject.COLLECTION_NAME);

			// getting all distributions from mongoDB
			DBCursor distributions = distributionCollection.find();

			// load distributions
			while (distributions.hasNext()) {
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

				System.out.println(q.size());

				int i = 0;
				for (DistributionMongoDBObject a : q) {
					if (!a.getSubjectFilterPath().equals(
							distribution.get(
									DistributionMongoDBObject.SUBJECT_FILTER_PATH)
									.toString())) {

						System.out.println("====+++++==========");
						System.out.println("Distribution " + i++ + ": "
								+ distribution.get(
										DistributionMongoDBObject.OBJECT_PATH)
										.toString());
						System.out.println(a.getSubjectFilterPath());
						
						
						System.out.println();
						DataThread dataThread = new DataThread();
						// save dataThread object
						GoogleBloomFilter filter = new GoogleBloomFilter();

						try {
							filter.loadFilter(a.getSubjectFilterPath());
						} catch (Exception e) {
							e.printStackTrace();
						}

						dataThread.filter = filter;

						dataThread.subjectFilterPath = a.getSubjectFilterPath();
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

				bean.addDisplayMessage(
						DataIDGeneralProperties.MESSAGE_LOG,
						"Loading objects from: "
								+ distribution.get(
										DistributionMongoDBObject.OBJECT_PATH)
										.toString()
								+ ". This might take a time, please be patient.");
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

				bean.addDisplayMessage(
						DataIDGeneralProperties.MESSAGE_LOG,
						"Loaded objects from: "
								+ distribution.get(
										DistributionMongoDBObject.OBJECT_PATH)
										.toString());

				System.out.println("Loaded objects from: "
						+ distribution.get(
								DistributionMongoDBObject.OBJECT_PATH)
								.toString());
				// save linksets into mongodb
				saveLinksets(listOfDataThreads);

			}

			// get all filters
			// while (distributions.hasNext()) {
			// if (f > maxFilter)
			// break;
			// f++;
			//
			// DBObject distribution = distributions.next();
			//
			// DataThread dataThread = new DataThread();
			// GoogleBloomFilter filter = new GoogleBloomFilter();
			//
			// System.out
			// .println("Opening filter: "
			// + distribution
			// .get(DistributionMongoDBObject.SUBJECT_FILTER_PATH));
			// // load distribution fiter path into filter
			// try {
			// filter.loadFilter(distribution.get(
			// DistributionMongoDBObject.SUBJECT_FILTER_PATH)
			// .toString());
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			//
			// // save dataThread object
			// dataThread.filter = filter;
			// dataThread.subjectFilterPath = distribution.get(
			// DistributionMongoDBObject.SUBJECT_FILTER_PATH)
			// .toString();
			// dataThread.subjectDistributionURI = distribution.get(
			// DistributionMongoDBObject.ACCESS_URL).toString();
			// dataThread.subjectDatasetURI = distribution.get(
			// DistributionMongoDBObject.TOP_DATASET).toString();
			//
			// listOfDataThreads.add(dataThread);
			//
			// }
			//
			// bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
			// "Running " + listOfDataThreads.size()
			// + " filter in parallel.");
			//
			// distributionCollection = DataIDDB.getInstance().getCollection(
			// DistributionMongoDBObject.COLLECTION_NAME);
			// DBCursor cursor = distributionCollection.find();
			//
			// // gett all distributions. For each distribution object file:
			// // creates
			// // a small buffer and then creates threads (one for each filter)
			// while (cursor.hasNext()) {
			//
			// // get object path
			// DBObject objectPath = cursor.next();
			//
			// // reading object distribution file here
			// BufferedReader br = new BufferedReader(new FileReader(
			// objectPath.get(DistributionMongoDBObject.OBJECT_PATH)
			// .toString()));
			//
			// // System.out.println("Searching "
			// // + objectPath.get(DistributionMongoDBObject.OBJECT_PATH)
			// // .toString() + " on filter.");
			//
			// bean.addDisplayMessage(
			// DataIDGeneralProperties.MESSAGE_LOG,"Loading objects from: "+
			// objectPath.get(
			// DistributionMongoDBObject.OBJECT_PATH)
			// .toString()+" This might take a time, please be patient.");
			//
			// String sCurrentLine;
			//
			// // loading objects and creating a buffer to send to threads
			// int bufferSize = 100000;
			//
			// // for small distributions reduce the buffer size
			// if (Integer.valueOf(objectPath.get(
			// DistributionMongoDBObject.NUMBER_OF_OBJECTS_TRIPLES)
			// .toString()) < bufferSize) {
			// bufferSize = 500;
			// }
			//
			// String[] buffer = new String[bufferSize];
			//
			// int bufferIndex = 0;
			//
			// while ((sCurrentLine = br.readLine()) != null) {
			// buffer[bufferIndex] = (sCurrentLine);
			// bufferIndex++;
			// int threadIndex = 0;
			//
			// // if buffer is full, start the threads!
			// if (bufferIndex % bufferSize == 0) {
			// Thread[] threads = new Thread[listOfDataThreads.size()];
			// for (DataThread dataThread : listOfDataThreads) {
			//
			// // set some properties so after processing we can
			// // store in mongodb
			// dataThread.distributionObjectPath = objectPath.get(
			// DistributionMongoDBObject.OBJECT_PATH)
			// .toString();
			// dataThread.objectDistributionURI = objectPath.get(
			// DistributionMongoDBObject.ACCESS_URL)
			// .toString();
			// dataThread.objectDatasetURI = objectPath.get(
			// DistributionMongoDBObject.TOP_DATASET)
			// .toString();
			//
			// // don't try to find links between distribution and
			// // itself
			// if
			// (!dataThread.objectDistributionURI.equals(dataThread.subjectDistributionURI))
			// {
			//
			// System.out.println();
			// System.out.println(dataThread.subjectDistributionURI);
			// System.out.println(dataThread.objectDistributionURI);
			//
			// threads[threadIndex] = new Thread(
			// new JobThread(dataThread,
			// buffer.clone(), bufferSize));
			// threads[threadIndex].start();
			// }
			// threadIndex++;
			// }
			//
			// // wait all threads finish and then start load buffer
			// // again
			// for (int d = 0; d < threads.length; d++)
			// threads[d].join();
			//
			// bufferIndex = 0;
			//
			// }
			// }
			//
			// System.out.println();
			// System.out.println();
			// bean.addDisplayMessage(
			// DataIDGeneralProperties.MESSAGE_LOG,
			// "Loaded objects from: "
			// + objectPath.get(
			// DistributionMongoDBObject.OBJECT_PATH)
			// .toString());
			// // save linksets into mongodb
			// saveLinksets(listOfDataThreads);
			// for (DataThread dataThread : listOfDataThreads) {
			// // bean.addDisplayMessage(
			// // DataIDGeneralProperties.MESSAGE_LOG,
			// // dataThread.distributionObjectPath + " -> "
			// // + dataThread.subjectFilterPath
			// // + "    Linksets found: "
			// // + String.valueOf(dataThread.links));
			// // bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
			// // dataThread.subjectFilterPath);
			// // bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
			// // String.valueOf(dataThread.links));
			// dataThread.links = 0;
			// }
			//
			// }
			//
			// cursor.close();
			//
		} catch (Exception e) {
			e.printStackTrace();
		}
		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
				"Time to update linksets: " + t.stopTimer() + "s");
	}

	public void saveLinksets(List<DataThread> dataThreads) {

		for (DataThread dataThread : dataThreads) {
			// if (dataThread.links > 0) {
			LinksetMongoDBObject l = new LinksetMongoDBObject(
					dataThread.objectDistributionURI + "-2-"
							+ dataThread.subjectDistributionURI);
			l.setLinks(dataThread.links);
			l.setObjectsDistributionTarget(dataThread.objectDistributionURI);
			l.setSubjectsDistributionTarget(dataThread.subjectDistributionURI);
			l.setObjectsDatasetTarget(dataThread.objectDatasetURI);
			l.setSubjectsDatasetTarget(dataThread.subjectDatasetURI);
			l.updateObject();
			// }
		}

	}

	// no parallelization method
	public void searchBufferOnFilter(GoogleBloomFilter filter, String[] lines,
			int size) throws Exception {
		BufferedReader br = null;
		ArrayList<String> links = new ArrayList<String>();

		try {
			for (int i = 0; i < size; i++) {
				if (filter.compare(lines[i])) {
					links.add(lines[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}
}

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
import dataid.exceptions.DataIDException;
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

				// find which filters should be opened for this distribution
				ArrayList<DistributionMongoDBObject> q = Queries
						.getDistributionsByAuthority(distribution
								.get(DistributionMongoDBObject.AUTHORITY_OBJECTS));

				// make some validations
				if (distribution.get(DistributionMongoDBObject.OBJECT_PATH) == null
						|| distribution
								.get(DistributionMongoDBObject.OBJECT_PATH)
								.toString().equals("")) {
					throw new DataIDException(
							"distributionObjectPath is empty or null for "
									+ distribution
											.get(DistributionMongoDBObject.ACCESS_URL)
											.toString() + " distribution;");
				}

				int i = 0;
				for (DistributionMongoDBObject a : q) {
					try {
						if (!a.getSubjectFilterPath()
								.equals(distribution
										.get(DistributionMongoDBObject.SUBJECT_FILTER_PATH)
										.toString())) {

							DataThread dataThread = new DataThread();
							// save dataThread object
							GoogleBloomFilter filter = new GoogleBloomFilter();

							try {
								filter.loadFilter(a.getSubjectFilterPath());
							} catch (Exception e) {
								e.printStackTrace();
							}

							dataThread.filter = filter;

							dataThread.subjectFilterPath = a
									.getSubjectFilterPath();
							dataThread.subjectDistributionURI = a
									.getAccessUrl();
							dataThread.subjectDatasetURI = a.getTopDataset();

							dataThread.objectDatasetURI = distribution.get(
									DistributionMongoDBObject.TOP_DATASET)
									.toString();
							dataThread.objectDistributionURI = distribution
									.get(DistributionMongoDBObject.ACCESS_URL)
									.toString();
							dataThread.distributionObjectPath = distribution
									.get(DistributionMongoDBObject.OBJECT_PATH)
									.toString();

							listOfDataThreads.add(dataThread);
						}
					} catch (Exception e) {
						throw new DataIDException(
								"Error while loading bloom filter: "
										+ e.getMessage());
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
				"Time to update linksets: " + t.stopTimer() + "s");
	}

	public void saveLinksets(List<DataThread> dataThreads) {

		for (DataThread dataThread : dataThreads) {
			LinksetMongoDBObject l = new LinksetMongoDBObject(
					dataThread.objectDistributionURI + "-2-"
							+ dataThread.subjectDistributionURI);
			l.setLinks(dataThread.links);
			l.setObjectsDistributionTarget(dataThread.objectDistributionURI);
			l.setSubjectsDistributionTarget(dataThread.subjectDistributionURI);
			l.setObjectsDatasetTarget(dataThread.objectDatasetURI);
			l.setSubjectsDatasetTarget(dataThread.subjectDatasetURI);
			l.updateObject();
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

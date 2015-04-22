package dataid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import dataid.download.CheckWhetherDownload;
import dataid.download.DownloadAndSaveDistribution;
import dataid.exceptions.DataIDException;
import dataid.files.PrepareFiles;
import dataid.filters.FileToFilter;
import dataid.filters.GoogleBloomFilter;
import dataid.models.DistributionModel;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.objects.DistributionObjectDomainsMongoDBObject;
import dataid.mongodb.objects.DistributionSubjectDomainsMongoDBObject;
import dataid.server.DataIDBean;
import dataid.utils.FileUtils;
import dataid.utils.Formats;
import dataid.utils.Timer;

public class Manager {
	final static Logger logger = Logger.getLogger(Manager.class);

	private String name = null;

	// list of subset and their distributions
	public List<DistributionModel> distributionsLinks = new ArrayList<DistributionModel>();

	DataIDModel dataIDModel = new DataIDModel();

	DataIDBean bean;

	public void load() throws Exception {
		// if there is at least one distribution, load them
		Iterator<DistributionModel> distributions = distributionsLinks
				.iterator();

		int counter = 0;

		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO, "Loading "
				+ distributionsLinks.size() + " distributions...");
		logger.info("Loading " + distributionsLinks.size()
				+ " distributions...");

		while (distributions.hasNext()) {
			counter++;

			DistributionModel distribution = distributions.next();
			// loading mongodb distribution object
			DistributionMongoDBObject distributionMongoDBObj = new DistributionMongoDBObject(
					distribution.getDistribution());

			// case there is no such distribution, create one.
			if (distributionMongoDBObj.getStatus() == null) {
				distributionMongoDBObj
						.setStatus(DistributionMongoDBObject.STATUS_WAITING_TO_DOWNLOAD);
			}

			// check is distribution need to be streamed
			boolean needDownload = false;

			if (distributionMongoDBObj.getStatus().equals(
					DistributionMongoDBObject.STATUS_WAITING_TO_DOWNLOAD))
				needDownload = true;
			else if (distributionMongoDBObj.getStatus().equals(
					DistributionMongoDBObject.STATUS_DOWNLOADING))
				needDownload = false;
			else if (distributionMongoDBObj.getStatus().equals(
					DistributionMongoDBObject.STATUS_ERROR))
				needDownload = true;
			else if (new CheckWhetherDownload()
					.checkDistribution(distributionMongoDBObj))
				needDownload = true;

			bean.addDisplayMessage(
					DataIDGeneralProperties.MESSAGE_INFO,
					"Distribution n. " + counter + ": "
							+ distribution.getDistributionURI());
			logger.info("Distribution n. " + counter + ": "
					+ distribution.getDistributionURI());

			if (!needDownload) {
				logger.info("Distribution is already in the last version. No needs to download again. ");
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
						"Distribution is already in the last version. No needs to download again. ");
			}

			// if distribution have not already been handled
			if (needDownload)
				try {

					// uptate status of distribution to downloading
					distributionMongoDBObj
							.setStatus(DistributionMongoDBObject.STATUS_DOWNLOADING);
					distributionMongoDBObj.updateObject(true);
					bean.updateDistributionList = true;

					// now we need to download the distribution
					DownloadAndSaveDistribution downloadedFile = new DownloadAndSaveDistribution(
							distribution.getDistriutionDownloadURL());

					bean.addDisplayMessage(
							DataIDGeneralProperties.MESSAGE_INFO,
							"Downloading distribution.");
					logger.info("Downloading distribution.");

					downloadedFile.downloadDistribution();

					// uptate status of distribution
					distributionMongoDBObj
							.setStatus(DistributionMongoDBObject.STATUS_DOWNLOADED);
					distributionMongoDBObj.updateObject(true);
					bean.updateDistributionList = true;

					bean.addDisplayMessage(
							DataIDGeneralProperties.MESSAGE_INFO,
							"Distribution downloaded. ");
					logger.info("Distribution downloaded. ");

					// check if format is not ntriples
					if (!downloadedFile.extension
							.equals(Formats.DEFAULT_NTRIPLES)) {

						// uptate status of distribution
						distributionMongoDBObj
								.setStatus(DistributionMongoDBObject.STATUS_SEPARATING_SUBJECTS_AND_OBJECTS);
						distributionMongoDBObj.updateObject(true);
						bean.updateDistributionList = true;

						bean.addDisplayMessage(
								DataIDGeneralProperties.MESSAGE_INFO,
								"Separating subjects and objects.");
						logger.info("Separating subjects and objects.");

						PrepareFiles p = new PrepareFiles();
						// separating subjects and objects using rapper and awk
						// error to convert dbpedia files from turtle using
						// rapper
						boolean isDbpedia = false;
						// if (distributionMongoDBObj.getDownloadUrl().contains(
						// "dbpedia"))
						// isDbpedia = true;
						// if (isDbpedia)
						// throw new DataIDException("DBpedia ttl format");
						p.separateSubjectAndObject(downloadedFile.fileName,
								downloadedFile.extension, isDbpedia);
						downloadedFile.objectDomains = p.objectDomains;
						downloadedFile.subjectDomains = p.subjectDomains;
						downloadedFile.objectFilePath = p.objectFile;
						downloadedFile.totalTriples = p.totalTriples;
						downloadedFile.objectLines = p.objectTriples;
					}

					// uptate status of distribution
					distributionMongoDBObj
							.setStatus(DistributionMongoDBObject.STATUS_CREATING_BLOOM_FILTER);
					distributionMongoDBObj.updateObject(true);
					bean.updateDistributionList = true;

					bean.addDisplayMessage(
							DataIDGeneralProperties.MESSAGE_INFO,
							"Creating bloom filter.");
					logger.info("Creating bloom filter.");

					// make a filter with subjects
					GoogleBloomFilter filter;
					if (downloadedFile.subjectLines != 0) {
						if (downloadedFile.subjectLines > 1000000)
							filter = new GoogleBloomFilter(
									(int) downloadedFile.subjectLines, 1.0/downloadedFile.subjectLines);
						else
							filter = new GoogleBloomFilter(
									(int) downloadedFile.subjectLines, 0.000001);
					} else {
						filter = new GoogleBloomFilter(
								(int) downloadedFile.contentLengthAfterDownloaded / 40,
								0.000001);
					}

					// get authority domain
					String authority = getAuthorotyDomainFromSubjectFile(DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
							+ downloadedFile.fileName);

					// load file to filter and take the process time
					FileToFilter f = new FileToFilter();

					Timer timer = new Timer();
					timer.startTimer();

					// Loading file to filter
					f.loadFileToFilter(filter, downloadedFile.fileName);
					distributionMongoDBObj.setTimeToCreateFilter(String
							.valueOf(timer.stopTimer()));

					filter.saveFilter(downloadedFile.fileName);
					// save filter

					// save distribution in a mongodb object
					bean.addDisplayMessage(
							DataIDGeneralProperties.MESSAGE_INFO,
							"Saving mongodb \"Distribution\" document.");
					logger.info("Saving mongodb \"Distribution\" document.");

					distributionMongoDBObj.setNumberOfObjectTriples(String
							.valueOf(downloadedFile.objectLines));
					distributionMongoDBObj.setDownloadUrl(downloadedFile.url
							.toString());
					distributionMongoDBObj.setFormat(downloadedFile.extension
							.toString());
					distributionMongoDBObj.setHttpByteSize(String
							.valueOf((int) downloadedFile.httpContentLength));
					distributionMongoDBObj
							.setHttpFormat(downloadedFile.httpContentType);
					distributionMongoDBObj
							.setHttpLastModified(downloadedFile.httpLastModified);
					distributionMongoDBObj
							.setObjectPath(downloadedFile.objectFilePath);
					distributionMongoDBObj
							.setSubjectFilterPath(filter.fullFilePath);
					distributionMongoDBObj.setTopDataset(distribution
							.getDatasetURI());
					distributionMongoDBObj
							.setNumberOfTriplesLoadedIntoFilter(String
									.valueOf(f.subjectsLoadedIntoFilter));
					distributionMongoDBObj
							.setTriples(downloadedFile.totalTriples);
					distributionMongoDBObj.setDomain(authority);

					// remove old domains object
					ObjectId id = new ObjectId();
					DistributionObjectDomainsMongoDBObject d2 = new DistributionObjectDomainsMongoDBObject(
							id.get().toString());
					d2.setDistributionURI(distributionMongoDBObj.getUri());
					d2.remove();

					// save object domains
					int count = 0;
					Iterator it = downloadedFile.objectDomains.entrySet()
							.iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						String d = (String) pair.getKey();
						// distributionMongoDBObj.addAuthorityObjects(d);
						count++;
						if (count % 100000 == 0) {
							logger.debug(count
									+ " different objects domain saved ("
									+ (downloadedFile.objectDomains.size() - count)
									+ " remaining).");
						}

						id = new ObjectId();
						d2 = new DistributionObjectDomainsMongoDBObject(id
								.get().toString());
						d2.setObjectDomain(d);
						d2.setDistributionURI(distributionMongoDBObj.getUri());

						d2.updateObject(false);
					}

					// remove old subjects domains
					id = new ObjectId();
					DistributionSubjectDomainsMongoDBObject d3 = new DistributionSubjectDomainsMongoDBObject(
							id.get().toString());
					d3.setDistributionURI(distributionMongoDBObj.getUri());
					d3.remove();

					// save subject domains
					count = 0;
					it = downloadedFile.subjectDomains.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						String d = (String) pair.getKey();
						// distributionMongoDBObj.addAuthorityObjects(d);
						count++;
						if (count % 100000 == 0) {
							logger.debug(count
									+ " different subjects domain saved ("
									+ (downloadedFile.subjectDomains.size() - count)
									+ " remaining).");
						}

						id = new ObjectId();
						d3 = new DistributionSubjectDomainsMongoDBObject(id
								.get().toString());
						d3.setSubjectDomain(d);
						d3.setDistributionURI(distributionMongoDBObj.getUri());

						d3.updateObject(false);
					}

					logger.info(downloadedFile.objectDomains.size()
							+ " different objects domain saved.");

					logger.info(downloadedFile.subjectDomains.size()
							+ " different subjects domain saved.");

					distributionMongoDBObj.setSuccessfullyDownloaded(true);
					distributionMongoDBObj.updateObject(true);
					bean.updateDistributionList = true;

					logger.info("Done saving mongodb distribution object.");

					// uptate status of distribution
					distributionMongoDBObj
							.setStatus(DistributionMongoDBObject.STATUS_WAITING_TO_CREATE_LINKSETS);
					distributionMongoDBObj.updateObject(true);

					bean.addDisplayMessage(
							DataIDGeneralProperties.MESSAGE_INFO,
							"Distribution saved!");
					logger.info("Distribution saved! ");

					bean.setDownloadNumberOfDownloadedDistributions(bean
							.getDownloadNumberOfDownloadedDistributions() + 1);
					try {
						DataIDBean.pushDownloadInfo();
					} catch (Exception exc) {
						exc.printStackTrace();
					}

				} catch (DataIDException e) {
					bean.addDisplayMessage(
							DataIDGeneralProperties.MESSAGE_ERROR,
							e.getMessage());
					bean.setDownloadNumberOfDownloadedDistributions(bean
							.getDownloadNumberOfDownloadedDistributions() + 1);
					DataIDBean.pushDownloadInfo();
					bean.updateDistributionList = true;
					e.printStackTrace();
				} catch (Exception e) {
					// uptate status of distribution
					distributionMongoDBObj
							.setStatus(DistributionMongoDBObject.STATUS_ERROR);
					distributionMongoDBObj.setLastErrorMsg(e.getMessage());

					distributionMongoDBObj.updateObject(true);

					bean.addDisplayMessage(
							DataIDGeneralProperties.MESSAGE_ERROR,
							e.getMessage());
					bean.setDownloadNumberOfDownloadedDistributions(bean
							.getDownloadNumberOfDownloadedDistributions() + 1);
					DataIDBean.pushDownloadInfo();
					bean.updateDistributionList = true;
					e.printStackTrace();
					distributionMongoDBObj.setLastErrorMsg(e.getMessage());
					distributionMongoDBObj.setSuccessfullyDownloaded(false);
					distributionMongoDBObj.updateObject(true);
				}

		}
		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
				"We are done reading your distributions.");

	}

	public Manager(List<DistributionModel> distributionsLinks) {
		this.distributionsLinks = distributionsLinks;
		bean = new DataIDBean();
		try {
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Manager(String URL, DataIDBean bean) {
		try {

			this.bean = bean;

			FileUtils.checkIfFolderExists();

			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
					"Loading DataID file URL: " + URL + " url.");
			logger.debug("Loading DataID file URL: " + URL + " url.");

			// check file extension
			FileUtils.acceptedFormats(URL.toString());

			// create jena model
			name = dataIDModel.readModel(URL, bean);

			if (name == null) {
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,
						"Impossible to read dataset. Perhaps that's not a valid DataID file. Dataset: "
								+ name);
				logger.error("Impossible to read dataset. Perhaps that's not a valid DataID file. Dataset: "
						+ name);
				return;
			}

			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
					"We found at least one dataset: " + name);
			logger.info("We found at least one dataset: " + name);

			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
					"Parsing model in order to find distributions...");
			logger.info("Parsing model in order to find distributions...");

			// parse model in order to find distributions
			List<DistributionModel> listOfSubsets = dataIDModel
					.parseDistributions(distributionsLinks, bean);
			int numberOfDistributions = listOfSubsets.size();

			// update view
			if (numberOfDistributions > 0) {
				bean.setDownloadNumberTotalOfDistributions(numberOfDistributions);
				bean.setDownloadDatasetURI(listOfSubsets.get(0).getDatasetURI());
				DataIDBean.pushDownloadInfo();
			}

			if (!dataIDModel.someDownloadURLFound)
				throw new Exception("No dcat:downloadURL property found!");
			else if (numberOfDistributions == 0)
				throw new Exception("### 0 distribution found! ###");
			else
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
						numberOfDistributions + " distribution(s) found");

			// try to load distributions and make filters
			load();

		} catch (Exception e) {
			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,
					e.getMessage());
			logger.error(e.getMessage());
		}
		logger.info("END");
		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO, "end");
	}

	private String getAuthorotyDomainFromSubjectFile(String filePath) {
		String authority = "";
		FileReader namereader;
		try {
			namereader = new FileReader(new File(filePath));
			BufferedReader in = new BufferedReader(namereader);
			String tmp = in.readLine();
			tmp = tmp.substring(1, tmp.length() - 1);
			URL url = new URL(tmp);
			authority = url.getProtocol() + "://" + url.getHost();
			namereader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return authority;
	}

}

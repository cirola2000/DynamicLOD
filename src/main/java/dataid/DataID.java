package dataid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.bson.types.ObjectId;

import dataid.exceptions.DataIDException;
import dataid.files.PrepareFiles;
import dataid.filters.FileToFilter;
import dataid.filters.GoogleBloomFilter;
import dataid.models.DistributionModel;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.objects.DistributionObjectDomainsMongoDBObject;
import dataid.mongodb.objects.DistributionSubjectDomainsMongoDBObject;
import dataid.server.DataIDBean;
import dataid.utils.DownloadAndSave;
import dataid.utils.FileUtils;
import dataid.utils.Formats;
import dataid.utils.Timer;

public class DataID {
	Logger log = Logger.getLogger("MyLog");  
	Handler fh = new ConsoleHandler();
	
	private String name = null;
	public static DataIDBean bean;

	// list of subset and their distributions
	private List<DistributionModel> distributionsLinks = new ArrayList<DistributionModel>();

	DataIDModel dataIDModel = new DataIDModel();

	private void load() throws Exception {
		// if there is at least one distribution, load them
		Iterator<DistributionModel> distributions = distributionsLinks
				.iterator();

		while (distributions.hasNext()) {
			DistributionModel distributionModel = distributions.next();
			// loading mongodb distribution object
			DistributionMongoDBObject distributionMongoDBObj = new DistributionMongoDBObject(
					distributionModel.getDistribution());
			try {
				// check if distribution have not already been handled
				if(!distributionMongoDBObj.getStatus().equals(DistributionMongoDBObject.STATUS_WAITING_TO_DOWNLOAD)){
					throw new DataIDException("Distribution "+distributionMongoDBObj.getDownloadUrl()+" is being downloaded now from other thread.");
				}
				
				// uptate status of distribution to downloading
				distributionMongoDBObj.setStatus(DistributionMongoDBObject.STATUS_DOWNLOADING);
				distributionMongoDBObj.updateObject(true);
				bean.pushDistributionList();
				
				// now we need to download the distribution
				DownloadAndSave downloadedFile = new DownloadAndSave();

				bean.addDisplayMessage(
						DataIDGeneralProperties.MESSAGE_INFO,
						"Downloading distribution: "
								+ distributionModel.getDistriutionDownloadURL() + " (DownloadURL property).");
				
				log.info("Downloading distribution: "
								+ distributionModel.getDistriutionDownloadURL() + " (DownloadURL property).");

				downloadedFile.downloadDistribution(distributionModel
						.getDistribution(), distributionModel
						.getDistriutionDownloadURL(),
						Formats.getEquivalentFormat(distributionMongoDBObj
								.getFormat()), bean);
				
				// uptate status of distribution
				distributionMongoDBObj.setStatus(DistributionMongoDBObject.STATUS_DOWNLOADED);
				distributionMongoDBObj.updateObject(true);
				bean.pushDistributionList();

				// check if format is not ntriples
				if (!downloadedFile.extension.equals(Formats.DEFAULT_NTRIPLES)) {
					
					// uptate status of distribution
					distributionMongoDBObj.setStatus(DistributionMongoDBObject.STATUS_SEPARATING_SUBJECTS_AND_OBJECTS);
					distributionMongoDBObj.updateObject(true);
					bean.pushDistributionList();
					
					PrepareFiles p = new PrepareFiles();
					// separating subjects and objects using rapper and awk
					// error to convert dbpedia files from turtle using rapper
					boolean isDbpedia = false;
					if (distributionMongoDBObj.getDownloadUrl().contains(
							"dbpedia"))
						isDbpedia = true;
					if(isDbpedia) throw new DataIDException("DBpedia ttl format");
					p.separateSubjectAndObject(downloadedFile.fileName,
							downloadedFile.extension, bean, isDbpedia);
					downloadedFile.objectDomains = p.objectDomains;
					downloadedFile.subjectDomains = p.subjectDomains;
					downloadedFile.objectFilePath = p.objectFile;
					downloadedFile.totalTriples = p.totalTriples;
					downloadedFile.objectLines = p.objectTriples;
					bean.setNumberOfTriples(downloadedFile.totalTriples);
					bean.setDownloadNumberOfTriplesLoaded(downloadedFile.totalTriples);

				}

				// uptate status of distribution 
				distributionMongoDBObj.setStatus(DistributionMongoDBObject.STATUS_CREATING_BLOOM_FILTER);
				distributionMongoDBObj.updateObject(true);
				bean.pushDistributionList();
				
				// make a filter with subjects
				GoogleBloomFilter filter;
				if (downloadedFile.subjectLines != 0) {
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
				f.loadFileToFilter(filter, downloadedFile.fileName, bean);
				distributionMongoDBObj.setTimeToCreateFilter(String
						.valueOf(timer.stopTimer()));

				filter.saveFilter(downloadedFile.fileName);
				// save filter

				
				
				// save distribution in a mongodb object
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
						"Saving mongodb \"Distribution\" document.");
				log.info("Saving mongodb \"Distribution\" document.");

				distributionMongoDBObj.setNumberOfObjectTriples(String
						.valueOf(downloadedFile.objectLines));
				distributionMongoDBObj.setDownloadUrl(downloadedFile.url
						.toString());
				distributionMongoDBObj.setFormat(downloadedFile.extension
						.toString());
				distributionMongoDBObj.setHttpByteSize(String
						.valueOf(downloadedFile.httpContentLength));
				distributionMongoDBObj
						.setHttpFormat(downloadedFile.httpContentType);
				distributionMongoDBObj
						.setHttpLastModified(downloadedFile.httpLastModified);
				distributionMongoDBObj
						.setObjectPath(downloadedFile.objectFilePath);
				distributionMongoDBObj
						.setSubjectFilterPath(filter.fullFilePath);
				distributionMongoDBObj.setTopDataset(distributionModel
						.getDatasetURI());
				distributionMongoDBObj
						.setNumberOfTriplesLoadedIntoFilter(String
								.valueOf(f.subjectsLoadedIntoFilter));
				distributionMongoDBObj.setTriples(downloadedFile.totalTriples);
				distributionMongoDBObj.setDomain(authority);
				
				// remove old domains object
				ObjectId id = new ObjectId();
				DistributionObjectDomainsMongoDBObject d2 = new DistributionObjectDomainsMongoDBObject(id.get().toString());
				d2.setDistributionURI(distributionMongoDBObj.getUri());
				d2.remove();
				
				
				// save object domains
				int count = 0;
				Iterator it = downloadedFile.objectDomains.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry)it.next();
					String d = (String) pair.getKey();
						// distributionMongoDBObj.addAuthorityObjects(d);
						count++;
					 	if(count%100000 == 0){
						 	log.info(count +
								 	" different objects domain saved ("+(downloadedFile.objectDomains.size() - count )+" remaining).");
						 	bean.addDisplayMessage(
								 	DataIDGeneralProperties.MESSAGE_INFO,count
								 	+" different objects domain saved ("+(downloadedFile.objectDomains.size() - count )+" remaining).");
					 	}
					 
					 id = new ObjectId();
						d2 = new DistributionObjectDomainsMongoDBObject(id.get().toString());
						d2.setObjectDomain(d);
						d2.setDistributionURI(distributionMongoDBObj.getUri());
					
					d2.updateObject(false);
				}
				
				// remove old subjects domains
				id = new ObjectId();
				DistributionSubjectDomainsMongoDBObject d3 = new DistributionSubjectDomainsMongoDBObject(id.get().toString());
				d3.setDistributionURI(distributionMongoDBObj.getUri());
				d3.remove();
				
				
				// save subject domains
				count = 0;
				 it = downloadedFile.subjectDomains.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry)it.next();
					String d = (String) pair.getKey();
						// distributionMongoDBObj.addAuthorityObjects(d);
						count++;
					 	if(count%100000 == 0){
						 	log.info(count +
								 	" different subjects domain saved ("+(downloadedFile.subjectDomains.size() - count )+" remaining).");
						 	bean.addDisplayMessage(
								 	DataIDGeneralProperties.MESSAGE_INFO,count
								 	+" different subjects domain saved ("+(downloadedFile.subjectDomains.size() - count )+" remaining).");
					 	}
					 
					 id = new ObjectId();
						d3 = new DistributionSubjectDomainsMongoDBObject(id.get().toString());
						d3.setSubjectDomain(d);
						d3.setDistributionURI(distributionMongoDBObj.getUri());
					
					d3.updateObject(false);
				}
				
					
				
				 log.info(downloadedFile.objectDomains.size() +
						 " different objects domain saved.");
				 bean.addDisplayMessage(
						 DataIDGeneralProperties.MESSAGE_INFO,downloadedFile.objectDomains.size() +
						 " different objects domain saved.");
				 
				 log.info(downloadedFile.subjectDomains.size() +
						 " different subjects domain saved.");
				 bean.addDisplayMessage(
						 DataIDGeneralProperties.MESSAGE_INFO,downloadedFile.subjectDomains.size() +
						 " different subjects domain saved.");

				distributionMongoDBObj.setSuccessfullyDownloaded(true);
				distributionMongoDBObj.updateObject(true);
				bean.pushDistributionList();

				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
						"Done saving mongodb distribution object.");
				log.info("Done saving mongodb distribution object.");
				
				
				// uptate status of distribution 
				distributionMongoDBObj.setStatus(DistributionMongoDBObject.STATUS_WAITING_TO_CREATE_LINKSETS);
				distributionMongoDBObj.updateObject(true);
				
				bean.addDisplayMessage(
						DataIDGeneralProperties.MESSAGE_INFO,
						"Distribution saved! "
								+ distributionModel.getDistriutionDownloadURL());
				log.info("Distribution saved! ");
				bean.setDownloadNumberOfDownloadedDistributions(bean.getDownloadNumberOfDownloadedDistributions()+1);
				bean.pushDownloadInfo();

			} catch (DataIDException e) {				
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,
						e.getMessage());
				bean.setDownloadNumberOfDownloadedDistributions(bean.getDownloadNumberOfDownloadedDistributions()+1);
				bean.pushDownloadInfo();
				e.printStackTrace();
			}
			catch (Exception e) {
				// uptate status of distribution 
				distributionMongoDBObj.setStatus(DistributionMongoDBObject.STATUS_ERROR);
				distributionMongoDBObj.updateObject(true);
				
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,
						e.getMessage());
				bean.setDownloadNumberOfDownloadedDistributions(bean.getDownloadNumberOfDownloadedDistributions()+1);
				bean.pushDownloadInfo();
				bean.pushDistributionList();
				e.printStackTrace();
				distributionMongoDBObj.setLastErrorMsg(e.getMessage());
				distributionMongoDBObj.setSuccessfullyDownloaded(false);
				distributionMongoDBObj.updateObject(true);
			}

		}
		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
				"We are done reading your distributions.");
		
	}

	public DataID(String URL, DataIDBean bean) {
		try {
			
			
			this.bean = bean;
			
	        fh.setFormatter(new Formatter() {
	            public String format(LogRecord record) {
	                return record.getLevel() + "  :  "
	                    + record.getSourceClassName() + " -:- "
	                    + record.getSourceMethodName() + " -:- "
	                    + record.getMessage() + "\n";
	              }
	            });  
	        log.addHandler(fh);

			FileUtils.checkIfFolderExists();

			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
					"DataID file URL: " + URL + " url.");

			// check file extension
			FileUtils.acceptedFormats(URL.toString());

			// create jena models
			name = dataIDModel.readModel(URL);

			if (name == null){
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,
						"Impossible to read dataset. Perhaps that's not a valid DataID file. Dataset: "
								+ name);
				log.warning("Impossible to read dataset. Perhaps that's not a valid DataID file. Dataset: "
								+ name);
			}
			else{
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
						"Dataset: " + name);
				log.info("Dataset: " + name);
			}

			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
					"Downloading and parsing distribution.");

			// parse model in order to find distributions
			List<DistributionModel> listOfSubsets = dataIDModel
					.parseDistributions(distributionsLinks, bean);
			int numberOfDistributions = listOfSubsets.size();

			// update view
			if (numberOfDistributions > 0) {
				bean.setDownloadNumberTotalOfDistributions(numberOfDistributions);
				bean.setDownloadDatasetURI(listOfSubsets.get(0).getDatasetURI());
				bean.pushDownloadInfo();
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
			e.printStackTrace();
			log.warning(e.getMessage());
		}
		log.info("END");
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

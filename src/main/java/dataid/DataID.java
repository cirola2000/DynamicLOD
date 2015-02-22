package dataid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import dataid.files.PrepareFiles;
import dataid.filters.FileToFilter;
import dataid.filters.GoogleBloomFilter;
import dataid.jena.DataIDModel;
import dataid.literal.SubsetModel;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.server.DataIDBean;
import dataid.utils.DownloadAndSave;
import dataid.utils.FileUtils;
import dataid.utils.Timer;

public class DataID {
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(DataID.class);

	private String name = null;
	public static DataIDBean bean;

	// list of subset and their distributions
	private List<SubsetModel> distributionsLinks = new ArrayList<SubsetModel>();

	DataIDModel dataIDModel = new DataIDModel();

	private void load() throws Exception {
		// if there is at least one distribution, load them
		Iterator<SubsetModel> distributions = distributionsLinks.iterator();

		while (distributions.hasNext()) {
			try {
				SubsetModel distributionModel = distributions.next();

				PrepareFiles p = new PrepareFiles();

				// now we need to download the distribution
				DownloadAndSave downloadedFile = new DownloadAndSave();
				bean.addDisplayMessage(
						DataIDGeneralProperties.MESSAGE_INFO,
						"Downloading distribution: "
								+ distributionModel.getDistribution() + " url.");

				if (downloadedFile.downloadFile(
						distributionModel.getDistribution(), bean).equals(""))
					break;

				// creating a mongodb distribution object
				DistributionMongoDBObject distributionMongoDBObj = new DistributionMongoDBObject(
						downloadedFile.url.toString());

				// check if format is N-triples
				String ext = FilenameUtils
						.getExtension(downloadedFile.fileName);
				if (!ext.equals("nt")) {
					downloadedFile.fileName = p
							.checkFileFormat(downloadedFile.fileName);

					// separating subjects and objects
					p.separateSubjectAndObject(downloadedFile.fileName);

					downloadedFile.objectFilePath = p.objectFile;

				}

				// make a filter with subjects
				GoogleBloomFilter filter;
				if (downloadedFile.subjectLines != 0) {
					filter = new GoogleBloomFilter(
							(int) downloadedFile.subjectLines, 0.01);
				} else {
					filter = new GoogleBloomFilter(
							(int) downloadedFile.contentLengthAfterDownloaded / 40,
							0.01);
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

				// save filter
				filter.saveFilter(downloadedFile.fileName);

				// save distribution in a mongodb object
				distributionMongoDBObj.setNumberOfObjectTriples(String
						.valueOf(downloadedFile.objectLines));
				distributionMongoDBObj.setAccessUrl(downloadedFile.url
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
				distributionMongoDBObj.setTriples(String.valueOf(bean
						.getDownloadNumberOfTriplesLoaded()));
				distributionMongoDBObj.setAuthority(authority);

				distributionMongoDBObj.updateObject();

				
				// adding authority domain in the authority bloom filter
				GoogleBloomFilter authorityFilter = new GoogleBloomFilter(100000, 0.0001);
				authorityFilter.addAuthorityDomainToFilter(distributionMongoDBObj.getAuthority());
				

			} catch (Exception e) {
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,
						e.getMessage());
			}

		}
	}

	public DataID(String URL, DataIDBean bean) {
		try {
			// BasicConfigurator.configure();

			this.bean = bean;

			FileUtils.checkIfFolderExists();

			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
					"DataID file URL: " + URL + " url.");

			// create jena models
			name = dataIDModel.readModel(URL);

			if (name == null)
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,
						"Impossible to read dataset. Perhaps that's not a valid DataID file. Dataset: "
								+ name);
			else
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
						"Dataset: " + name);

			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
					"Downloading and parsing distribution.");

			// parse model in order to find distributions
			List<SubsetModel> listOfSubsets = dataIDModel
					.parseDistributions(distributionsLinks);
			int numberOfDistributions = listOfSubsets.size();

			// update view
			if (numberOfDistributions > 0) {
				bean.setDownloadNumberTotalOfDistributions(numberOfDistributions);
				bean.setDownloadDatasetURI(listOfSubsets.get(0).getDatasetURI());
				bean.pushDownloadInfo();
			}

			if (!dataIDModel.someAccessURLFound)
				throw new Exception("No dcat:accessURL property found!");
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
		}

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

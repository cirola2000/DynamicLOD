package dataid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import dataid.files.PrepareFiles;
import dataid.filters.FileToFilter;
import dataid.filters.GoogleBloomFilter;
import dataid.jena.DataIDModel;
import dataid.jena.ProcessEntry;
import dataid.literal.DynamicLODCloudEntryModel;
import dataid.literal.SubsetModel;
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

	DataIDModel model = new DataIDModel();

	private void makeLinksets() throws Exception {
		// if there is at least one distribution make the linksets
		Iterator<SubsetModel> iterator = distributionsLinks.iterator();
		
		// instance of dynamic LOD entry to save relevant data
		DynamicLODCloudEntryModel entry = new DynamicLODCloudEntryModel();

		while (iterator.hasNext()) {
			try{
			SubsetModel subsetModel = iterator.next();

			PrepareFiles p = new PrepareFiles();

			// now we need to download the distribution
			DownloadAndSave downloadedFile = new DownloadAndSave();
			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
					"Downloading distribution: " + subsetModel.getDistribution()
							+ " url.");

			downloadedFile.downloadFile(subsetModel.getDistribution());
			entry.setNumberOfObjectTriples(String.valueOf(downloadedFile.objectLines));

			// check if format is ntriples
			String ext = FilenameUtils.getExtension(downloadedFile.fileName);
			if (!ext.equals("nt")) {
				downloadedFile.fileName = p.checkFileFormat(downloadedFile.fileName);

				// separating subjects and objects
				p.separateSubjectAndObject(downloadedFile.fileName);

				downloadedFile.objectFilePath = p.objectFile;

			}

			// make a filter with subjects
			GoogleBloomFilter filter;
			if (downloadedFile.subjectLines != 0) {
				filter = new GoogleBloomFilter((int) downloadedFile.subjectLines, 0.01);
			} else {
				filter = new GoogleBloomFilter(
						(int) downloadedFile.contentLengthAfterDownloaded / 40, 0.01);
			}

			// load file to filter and take the process time
			FileToFilter f = new FileToFilter();

			Timer timer = new Timer();
			timer.startTimer();
			
			// Loading file to filter
			f.loadFileToFilter(filter, downloadedFile.fileName);
			entry.setTimeToCreateFilter(String.valueOf(timer.stopTimer()));
			
			entry.setNumberOfTriplesLoadedIntoFilter(String.valueOf(f.subjectsLoadedIntoFilter));

			// save filter
			filter.saveFilter(downloadedFile.fileName);

			
			entry.setAccessURL(downloadedFile.url.toString());
			entry.setByteSize(downloadedFile.contentLength);
			entry.setDataIDFilePath(downloadedFile.dataIDFilePath);
			entry.setDatasetURI(subsetModel.getDatasetURI());
			entry.setObjectPath(downloadedFile.objectFilePath);
			entry.setSubjectFilterPath(filter.fullFilePath);
			entry.setSubsetURI(subsetModel.getSubsetURI());
 
			// save entry in he mongodb
			ProcessEntry saveEntry = new ProcessEntry();
			saveEntry.saveNewMongoDBEntry(entry);			
			
			}
			catch(Exception e){
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR, e.getMessage());
			}

		}
		ProcessEntry fsMoldel = new ProcessEntry();

		// compare distributions using filters
		fsMoldel.compareAllDistributions();
	}

	public DataID(String URL, DataIDBean bean) {
		try {
			// BasicConfigurator.configure();

			this.bean = bean;

			FileUtils.checkIfFolderExists();

			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
					"DataID file URL: " + URL + " url.");

			// create jena models
			name = model.readModel(URL);

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
			int numberOfDistributions = model.parseDistributions(
					distributionsLinks).size();
			if (!model.someAccessURLFound)
				throw new Exception("No dcat:accessURL property found!");
			else if (numberOfDistributions == 0)
				throw new Exception("### 0 distribution found! ###");
			else
				bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
						numberOfDistributions + " distribution(s) found");

			// merge current dataid with dataID graph
			DataIDModel.mergeCurrentDataIDWithDataIDGraph(URL);

			// try to create linksets
			makeLinksets();

		} catch (Exception e) {
			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,
					e.getMessage());
			e.printStackTrace();
		}

		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO, "end");

	}

}

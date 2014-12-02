package dataid;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.mapred.FileOutputCommitter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import dataid.files.PrepareFiles;
import dataid.filters.FileToFilter;
import dataid.filters.GoogleBloomFilter;
import dataid.jena.DataIDModel;
import dataid.jena.FSModel;
import dataid.literal.SubsetModel;
import dataid.ontology.Dataset;
import dataid.ontology.Distribution;
import dataid.ontology.Linkset;
import dataid.ontology.vocabulary.NS;
import dataid.server.DataIDBean;
import dataid.utils.DownloadFile;
import dataid.utils.FileUtils;

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

		if (iterator.hasNext()) {
			SubsetModel l = iterator.next();

			PrepareFiles p = new PrepareFiles();

			// now we need to download the distribution
			DownloadFile dFile = new DownloadFile();
			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,
					"Downloading distribution: " + l.getDistribution()
							+ " url.");

			dFile.downloadFile(l.getDistribution());

			// check if format is ntriples
			dFile.fileName = p.checkFileFormat(dFile.fileName);
 
			// separating subjects and objects
			p.separateSubjectAndObject(dFile.fileName);

			// make a filter with subjects
			GoogleBloomFilter filter = new GoogleBloomFilter(
					(int) (dFile.contentLength / 40), 0.01);
			if(dFile.contentLength < 1)
				filter = new GoogleBloomFilter(21000000, 0.01);
			

			FileToFilter f = new FileToFilter();
			

			// Loading file to filter
			f.loadFileToFilter(filter, dFile.fileName);

			// save filter
			filter.saveFilter(dFile.fileName);
			
			FSModel fsMoldel = new FSModel();

			// save file metadata
			fsMoldel.addDatasetOnFileSystem(dFile.url.toString(), dFile.saveFilePath,
					dFile.url.toString(), dFile.contentLength,
					filter.fullFileName, p.objectFile, l.getSubset(), l.getDatasetURI());

			// compare distributions using filters
			fsMoldel.compareAllDistributions();
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
		int numberOfDistributions = model.parseDistributions(distributionsLinks).size();
		if(!model.someAccessURLFound)
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

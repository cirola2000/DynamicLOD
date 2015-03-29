package dataid.lov;

import org.apache.log4j.Logger;
import org.junit.Test;

import dataid.download.DownloadLOVVocabularies;
import dataid.filters.FileToFilter;
import dataid.filters.GoogleBloomFilter;

public class UpdateLOVFilter {
	
	final static Logger logger = Logger
			.getLogger(UpdateLOVFilter.class);

	@Test 
	public void UpdateLOVFilter() {
		try {
			DownloadLOVVocabularies d = new DownloadLOVVocabularies();
			
			logger.info("Downloading LOV.");

			d.downloadLOV("http://lov.okfn.org/lov.nq.gz");
			
			logger.info("Creating bloom filter.");

			// make a filter with subjects
			GoogleBloomFilter filter;
				filter = new GoogleBloomFilter(
						(int) d.splitThread.subjectLines, (0.9)/d.splitThread.subjectLines);
			
			
			// load file to filter and take the process time
			FileToFilter f = new FileToFilter();

			// Loading file to filter
			f.loadFileToFilter(filter, d.getFileName());

			filter.saveFilter(d.getFileName());
			// save filter
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

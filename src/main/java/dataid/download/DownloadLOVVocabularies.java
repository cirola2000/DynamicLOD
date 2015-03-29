package dataid.download;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.junit.Test;

import dataid.filters.FileToFilter;
import dataid.filters.GoogleBloomFilter;
import dataid.threads.SplitAndStoreThread;
import dataid.utils.Formats;

public class DownloadLOVVocabularies extends Download {

	final static Logger logger = Logger
			.getLogger(DownloadLOVVocabularies.class);

	ConcurrentLinkedQueue<String> subjectQueue = new ConcurrentLinkedQueue<String>();
	ConcurrentLinkedQueue<String> bufferQueue = new ConcurrentLinkedQueue<String>();
	public ConcurrentHashMap<String, Integer> countSubjectDomainsHashMap = new ConcurrentHashMap<String, Integer>();
	
	public SplitAndStoreThread splitThread = null;
	
	public double contentLengthAfterDownloaded = 0;
	public double countBytesReaded = 0;
	int aux;

	public void downloadLOV(String url) throws Exception {
		InputStream inputStream = getStream(new URL(
				url));

		// allowing bzip2 format
		inputStream = getGZipInputStream(inputStream);
		
		if (!Formats.getEquivalentFormat(getExtension()).equals(
				Formats.DEFAULT_NQUADS)){
			throw new Exception("Format different then expected");
		}

		final byte[] buffer = new byte[BUFFER_SIZE];

		String str = "";
		int n = 0;
		BufferedInputStream b = new BufferedInputStream(inputStream);
		splitThread = new SplitAndStoreThread(bufferQueue,
				subjectQueue, null, getFileName(), false);
		splitThread.start();

			while (-1 != (n = b.read(buffer))) {

				str = new String(buffer, 0, n);
				bufferQueue.add(str);
				str = "";

				// don't allow queue size bigger than 900;
				while (bufferQueue.size() > 900) {
					Thread.sleep(1);
				}
				
				contentLengthAfterDownloaded = contentLengthAfterDownloaded + n;

				countBytesReaded = countBytesReaded + n;

				if (aux % 1000 == 0) {
					logger.info(countBytesReaded / 1024 / 1024
							+ "MB uncompressed/lodaded.");
					aux = 0;
				}
				aux++;
				
			}
			splitThread.setDoneReadingFile(true);
			
		splitThread.join();
	
	}

}

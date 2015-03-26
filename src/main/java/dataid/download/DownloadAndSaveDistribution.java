package dataid.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import dataid.DataIDGeneralProperties;
import dataid.exceptions.DataIDException;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.server.DataIDBean;
import dataid.threads.GetDomainsFromTriplesThread;
import dataid.threads.SplitAndStoreThread;
import dataid.utils.Formats;

public class DownloadAndSaveDistribution extends Download {

	final static Logger logger = Logger
			.getLogger(DownloadAndSaveDistribution.class);

	// Paths
	public String dataIDFilePath = null;
	public String objectFilePath;

	public double contentLengthAfterDownloaded = 0;
	public Integer subjectLines = 0;
	public Integer objectLines = 0;
	public Integer totalTriples;

	// control bytes to show percentage
	public double countBytesReaded = 0;

	public ConcurrentHashMap<String, Integer> objectDomains = new ConcurrentHashMap<String, Integer>();
	public ConcurrentHashMap<String, Integer> subjectDomains = new ConcurrentHashMap<String, Integer>();
	public ConcurrentHashMap<String, Integer> countObjectDomainsHashMap = new ConcurrentHashMap<String, Integer>();
	public ConcurrentHashMap<String, Integer> countSubjectDomainsHashMap = new ConcurrentHashMap<String, Integer>();

	public AtomicInteger aint = new AtomicInteger(0);

	ConcurrentLinkedQueue<String> bufferQueue = new ConcurrentLinkedQueue<String>();
	ConcurrentLinkedQueue<String> objectQueue = new ConcurrentLinkedQueue<String>();
	ConcurrentLinkedQueue<String> subjectQueue = new ConcurrentLinkedQueue<String>();

	boolean doneReadingFile = false;
	boolean doneSplittingString = false;
	boolean doneAuthorityObject = false;

	public void downloadDistribution(String distributionURI, String accessURL,
			String format) throws Exception {

		InputStream inputStream = getStream(new URL(accessURL));

		// get some data from headers
		getMetadataFromHTTPHeaders(httpConn);

		// print http headers
		printHeaders();

		// allowing bzip2 format
		inputStream = getBZip2InputStream(inputStream);
		
		// allowinf zip format
		inputStream = getZipInputStream(inputStream);

		dataIDFilePath = DataIDGeneralProperties.BASE_PATH + getFileName();
		objectFilePath = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
				+ getFileName();
		final byte[] buffer = new byte[BUFFER_SIZE];
		int n = 0;
		int aux = 0;

		setExtension(Formats.getEquivalentFormat(getExtension()));
		

		if (Formats.getEquivalentFormat(getExtension()).equals(
				Formats.DEFAULT_NTRIPLES)) {

			SplitAndStoreThread splitThread = new SplitAndStoreThread(
					bufferQueue, subjectQueue, objectQueue, fileName);
			splitThread.start();

			GetDomainsFromTriplesThread getDomainFromObjectsThread = new GetDomainsFromTriplesThread(
					objectQueue, countObjectDomainsHashMap);
			getDomainFromObjectsThread.start();

			GetDomainsFromTriplesThread getDomainFromSubjectsThread = new GetDomainsFromTriplesThread(
					subjectQueue, countSubjectDomainsHashMap);
			getDomainFromSubjectsThread.start();

			String str = "";
			BufferedInputStream b = new BufferedInputStream(inputStream);
			while (-1 != (n = b.read(buffer))) {

				str = new String(buffer, 0, n);
				bufferQueue.add(str);
				str = "";
				contentLengthAfterDownloaded = contentLengthAfterDownloaded + n;

				countBytesReaded = countBytesReaded + n;

				if (aux % 1000 == 0) {
					logger.info(countBytesReaded / 1024 / 1024
							+ "MB uncompressed/lodaded.");
					aux = 0;
				}
				aux++;

				// don't allow queue size bigger than 900;
				while (bufferQueue.size() > 900) {
					Thread.sleep(1);
				}

			}

			doneReadingFile = true;

			// telling thread that we are done streaming
			splitThread.setDoneReadingFile(true);
			splitThread.join();

//			fileName = splitThread.getFileName();
			objectLines = splitThread.getObjectLines();
			subjectLines = splitThread.getSubjectLines();
			totalTriples = splitThread.getTotalTriples();

			getDomainFromObjectsThread.setDoneSplittingString(true);
			getDomainFromObjectsThread.join();

			getDomainFromSubjectsThread.setDoneSplittingString(true);
			getDomainFromSubjectsThread.join();

			Iterator it = countObjectDomainsHashMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				if ((Integer) pair.getValue() > 50) {
					if (((String) pair.getKey()).length() < 100) {
						objectDomains.put((String) pair.getKey(),
								(Integer) pair.getValue());
					}
				}
				it.remove(); // avoids a ConcurrentModificationException
			}

			it = countSubjectDomainsHashMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				if ((Integer) pair.getValue() > 50) {
					if (((String) pair.getKey()).length() < 100) {
						subjectDomains.put((String) pair.getKey(),
								(Integer) pair.getValue());
					}
				}
				it.remove(); // avoids a ConcurrentModificationException
			}

		} else if (Formats.getEquivalentFormat(extension).equals(
				Formats.DEFAULT_TURTLE)
				|| Formats.getEquivalentFormat(extension).equals(
						Formats.DEFAULT_RDFXML)) {
			int bytesRead = -1;
			FileOutputStream outputStream = new FileOutputStream(dataIDFilePath);
			while (-1 != (bytesRead = inputStream.read(buffer))) {
				outputStream.write(buffer, 0, bytesRead);
				contentLengthAfterDownloaded = contentLengthAfterDownloaded
						+ bytesRead;
				countBytesReaded = countBytesReaded + bytesRead;
			}
			outputStream.close();
		} else {
			httpConn.disconnect();
			throw new Exception("RDF format not supported: " + extension);
		}

		doneReadingFile = true;

		// update file length
		if (httpContentLength < 1) {
			File f = new File(dataIDFilePath);
			httpContentLength = f.length();
		}

		httpConn.disconnect();

	}

}
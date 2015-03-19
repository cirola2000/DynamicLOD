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

public class DownloadAndSave {
	
	final static Logger logger = Logger.getLogger(CheckWhetherDownload.class);
	
	private static final int BUFFER_SIZE = 16384;

	public String fileName = "";

	// HTTP header fields
	public String httpDisposition = null;
	public String httpContentType = null;
	public double httpContentLength;
	public String httpLastModified = "0";

	// Paths
	public String dataIDFilePath = null;
	public String objectFilePath;

	public URL url = null;

	public double contentLengthAfterDownloaded = 0;
	public Integer subjectLines = 0;
	public Integer objectLines = 0;
	public Integer totalTriples;
	public String extension;

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


		url = new URL(accessURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();
		
		logger.debug("Open HTTP connection for URL: "+url.toString());

		// check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {

			logger.debug("Successfuly connected with HTTP OK status.");
					
			// get some data from headers
			getMetadataFromHTTPHeaders(httpConn);

			// extracts file name from header field
			fileName = new FileNameFromURL().getFileName(accessURL, httpDisposition);
			logger.debug("Found file name: "+ fileName);

			// print http headers
			printHeaders();

			// opens input stream from HTTP connection
			InputStream inputStream = httpConn.getInputStream();
			logger.debug("InputStream from http connection opened");

			extension = FilenameUtils.getExtension(fileName);

			// check whether file is bz2 type
			if (extension.equals("bz2")) {
				logger.debug("File extension is bz2, creating BZip2CompressorInputStream...");
				inputStream = new BZip2CompressorInputStream(
						httpConn.getInputStream(), true);
				fileName = fileName.replace(".bz2", "");
				
				logger.debug("Done creating BZip2CompressorInputStream! New file name is "+fileName);
			}

			// check whether file is zip type
			if (extension.equals("zip")) {
				logger.debug("File extension is zip, creating ZipInputStream and checking compressed files...");
				DownloadZipUtils d = new DownloadZipUtils();
				d.checkZipFile(url);
				ZipInputStream zip = new ZipInputStream(
						httpConn.getInputStream());
				ZipEntry entry = zip.getNextEntry();
				fileName = entry.getName();
				inputStream = zip;
				logger.debug("Done, we found a single file: "+fileName);
			}

			dataIDFilePath = DataIDGeneralProperties.BASE_PATH + fileName;
			objectFilePath = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
					+ fileName;
			final byte[] buffer = new byte[BUFFER_SIZE];
			int n = 0;
			int aux = 0;

			checkExtensionFormat(format);

			if (Formats.getEquivalentFormat(extension).equals(Formats.DEFAULT_NTRIPLES)) {

				SplitAndStoreThread splitThread = new SplitAndStoreThread(bufferQueue,
						subjectQueue, objectQueue, fileName);
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
					contentLengthAfterDownloaded = contentLengthAfterDownloaded
							+ n;

					countBytesReaded = countBytesReaded + n;

					if (aux % 1000 == 0) {
						logger.info(countBytesReaded / 1024 / 1024);
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
				
				fileName = splitThread.getFileName();
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


			} else if (Formats.getEquivalentFormat(extension).equals(Formats.DEFAULT_TURTLE)
					|| Formats.getEquivalentFormat(extension).equals(Formats.DEFAULT_RDFXML)) {
				int bytesRead = -1;
				FileOutputStream outputStream = new FileOutputStream(
						dataIDFilePath);
				while (-1 != (bytesRead = inputStream.read(buffer))) {
					outputStream.write(buffer, 0, bytesRead);
					contentLengthAfterDownloaded = contentLengthAfterDownloaded
							+ bytesRead;
					countBytesReaded = countBytesReaded + bytesRead;
				}
				outputStream.close();
			} else {
				httpConn.disconnect();
				throw new Exception("RDF format not supported: "
						+ extension);
			}

			doneReadingFile = true;

			// update file length
			if (httpContentLength < 1) {
				File f = new File(dataIDFilePath);
				httpContentLength = f.length();
			}

			httpConn.disconnect();
		} else {
			httpConn.disconnect();
			throw new Exception(
					"No file to download. Server replied HTTP code: "
							+ responseCode);
		}
	}

	private void getMetadataFromHTTPHeaders(HttpURLConnection httpConn) {

		httpDisposition = httpConn.getHeaderField("Content-Disposition");
		httpContentType = httpConn.getContentType();
		httpContentLength = httpConn.getContentLength();
		if (httpConn.getLastModified() > 0)
			httpLastModified = String.valueOf(httpConn.getLastModified());

	}


	private void printHeaders() {
		DecimalFormat df = new DecimalFormat("#.##");

		logger.debug("Content-Type = " + httpContentType);
		logger.debug("Last-Modified = " + httpLastModified);
		logger.debug("Content-Disposition = " + httpDisposition);
		logger.debug("Content-Length = "
				+ df.format(httpContentLength / 1024 / 1024) + " MB");
		logger.debug("fileName = " + fileName);
	}

	private void checkExtensionFormat(String format) {
		extension = FilenameUtils.getExtension(fileName);
		if (extension.equals("")) {
			if (format.equals(Formats.DEFAULT_NTRIPLES)) {
				extension = Formats.DEFAULT_NTRIPLES;
			}
			if (format.equals(Formats.DEFAULT_RDFXML)) {
				extension = Formats.DEFAULT_RDFXML;
			}
			if (format.equals(Formats.DEFAULT_NTRIPLES)) {
				extension = Formats.DEFAULT_NTRIPLES;
			}

		}
	}

}
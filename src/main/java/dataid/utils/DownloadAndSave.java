package dataid.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FilenameUtils;

import com.hp.hpl.jena.graph.Triple;

import dataid.DataIDGeneralProperties;
import dataid.exceptions.DataIDException;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.server.DataIDBean;
import dataid.threads.AddAuthorityObjectThread;
import dataid.threads.SplitAndStoreThread;

public class DownloadAndSave {
	private static final int BUFFER_SIZE = 2048;

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

	public List<String> authorityDomains = new ArrayList<String>();

	public AtomicInteger aint = new AtomicInteger(0);

	Queue<String> bufferQueue = new ConcurrentLinkedQueue<String>();
	Queue<String> objectQueue = new ConcurrentLinkedQueue<String>();
	boolean doneReadingFile = false;
	boolean doneSplittingString = false;
	boolean doneAuthorityObject = false;

	public void downloadDistribution(String distributionURI, String accessURL,
			String format, DataIDBean bean) throws Exception {

		url = new URL(accessURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		// check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {

			// get some data from headers
			getMetadataFromHTTPHeaders(httpConn);

			// check if distribution already exists
			if (!checkWhetherDownload(accessURL,
					String.valueOf(httpContentLength), httpLastModified)) {
				throw new DataIDException("File previously downloaded: "
						+ accessURL + " No modification found. ");
			}

			// extracts file name from header field
			createFileName(accessURL);

			// print header at stdout
			printHeaders();

			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();

			extension = FilenameUtils.getExtension(fileName);

			// check whether file is bz2 type
			if (extension.equals("bz2")) {
				inputStream = new BZip2CompressorInputStream(
						httpConn.getInputStream(), true);
				fileName = fileName.replace(".bz2", "");
			}

			// check whether file is zip type
			if (extension.equals("zip")) {
				DownloadUtils d = new DownloadUtils();
				d.checkZip(url);
				ZipInputStream zip = new ZipInputStream(
						httpConn.getInputStream());
				ZipEntry entry = zip.getNextEntry();
				fileName = entry.getName();
				inputStream = zip;
			}

			dataIDFilePath = DataIDGeneralProperties.BASE_PATH + fileName;
			objectFilePath = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
					+ fileName;
			final byte[] buffer = new byte[BUFFER_SIZE];
			int n = 0;
			int aux = 0;

			checkExtensionFormat(format);

			if (extension.equals(Formats.DEFAULT_NTRIPLES)) {
//				SplitAndStore r = new SplitAndStore(bean);
//				new Thread(r).start();
				
				SplitAndStoreThread r = new SplitAndStoreThread(bufferQueue, objectQueue, fileName,bean); 
				r.start();

//				AddAuthorityObject r2 = new AddAuthorityObject();
//				new Thread(r2).start();

				AddAuthorityObjectThread r2 = new AddAuthorityObjectThread(objectQueue, authorityDomains);
				r2.start();
				
				
				String str = "";
				while (-1 != (n = inputStream.read(buffer))) {

					str = new String(buffer, 0, n);
					bufferQueue.add(str);
					str = "";
					contentLengthAfterDownloaded = contentLengthAfterDownloaded
							+ n;

					countBytesReaded = countBytesReaded + n;

					if (aux % 8000 == 0) {
						bean.setDownloadedMB(countBytesReaded / 1024 / 1024);
						bean.pushDownloadInfo();
						aux=0;
					}
					aux++;

					// don't allow queue size bigger than 900;
					while (bufferQueue.size() > 900) {
					}

				}
				while (bufferQueue.size() > 0) {
				}
				
				doneReadingFile = true;
				
				// telling thread that we are done streaming
				r.setDoneReadingFile(true);
				r.join();
				System.out.println("ACABO 1");
				fileName = r.getFileName();
				objectLines = r.getObjectLines();
				subjectLines = r.getSubjectLines();
				totalTriples = r.getTotalTriples();
				
				r2.setDoneSplittingString(true);
				
				r2.join();
				System.out.println("ACABO 2");
				
//				while (doneAuthorityObject==false) {};

			} else if (extension.equals(Formats.DEFAULT_TURTLE)
					|| extension.equals(Formats.DEFAULT_RDFXML)) {
				int bytesRead = -1;
				FileOutputStream outputStream = new FileOutputStream(
						dataIDFilePath);
				while (-1 != (bytesRead = inputStream.read(buffer))) {
					outputStream.write(buffer, 0, bytesRead);
					contentLengthAfterDownloaded = contentLengthAfterDownloaded
							+ bytesRead;
					countBytesReaded = countBytesReaded + bytesRead;

					if (aux % 8000 == 0) {
						bean.setDownloadedMB(countBytesReaded / 1024 / 1024);
						bean.pushDownloadInfo();
					}
					aux++;

				}
				bean.setDownloadedMB(countBytesReaded / 1024 / 1024);
				bean.pushDownloadInfo();
				outputStream.close();
			} else {
				httpConn.disconnect();
				throw new DataIDException("RDF format not supported: "
						+ extension);
			}
			
			doneReadingFile = true;
			
			// update file length
			if (httpContentLength < 1) {
				File f = new File(dataIDFilePath);
				httpContentLength = f.length();
			}

			bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
					"File downloaded: " + fileName);
			httpConn.disconnect();
		} else {
			httpConn.disconnect();
			throw new DataIDException(
					"No file to download. Server replied HTTP code: "
							+ responseCode);
		}
	}

	

	private boolean checkWhetherDownload(String uri, String httpContentLength,
			String httpLastModified) {
		try {
			DistributionMongoDBObject distribution = new DistributionMongoDBObject(
					uri);
			if (distribution.getHttpByteSize().equals(httpContentLength)
					&& distribution.getHttpLastModified().equals(
							httpLastModified))
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private void getMetadataFromHTTPHeaders(HttpURLConnection httpConn) {

		httpDisposition = httpConn.getHeaderField("Content-Disposition");
		httpContentType = httpConn.getContentType();
		httpContentLength = httpConn.getContentLength();
		if (httpConn.getLastModified() > 0)
			httpLastModified = String.valueOf(httpConn.getLastModified());

	}

	private void createFileName(String accessURL) {
		if (httpDisposition != null) {
			int index = httpDisposition.indexOf("filename=");
			if (index > 0) {
				fileName = httpDisposition.substring(index + 10,
						httpDisposition.length() - 1);
			}
		} else {
			// extracts file name from URL
			fileName = accessURL.substring(accessURL.lastIndexOf("/") + 1,
					accessURL.length());
		}
	}

	private void printHeaders() {
		DecimalFormat df = new DecimalFormat("#.##");

		System.out.println("Content-Type = " + httpContentType);
		System.out.println("Last-Modified = " + httpLastModified);
		System.out.println("Content-Disposition = " + httpDisposition);
		System.out.println("Content-Length = "
				+ df.format(httpContentLength / 1024 / 1024) + " MB");
		System.out.println("fileName = " + fileName);
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
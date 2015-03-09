package dataid.utils;

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

import dataid.DataIDGeneralProperties;
import dataid.exceptions.DataIDException;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.server.DataIDBean;
import dataid.threads.AddAuthorityObjectThread;
import dataid.threads.SplitAndStoreThread;

public class DownloadAndSave {
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

	public ConcurrentHashMap<String, Integer> authorityDomains = new ConcurrentHashMap<String, Integer>();
	public ConcurrentHashMap<String, Integer> sharedHashMap = new ConcurrentHashMap<String, Integer>();

	public AtomicInteger aint = new AtomicInteger(0);

	ConcurrentLinkedQueue<String> bufferQueue = new ConcurrentLinkedQueue<String>();
	ConcurrentLinkedQueue<String> objectQueue = new ConcurrentLinkedQueue<String>();
	boolean doneReadingFile = false;
	boolean doneSplittingString = false;
	boolean doneAuthorityObject = false;

	DataIDBean bean;

	public void downloadDistribution(String distributionURI, String accessURL,
			String format, DataIDBean bean) throws Exception {

		this.bean = bean;

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

				SplitAndStoreThread r = new SplitAndStoreThread(bufferQueue,
						objectQueue, fileName, bean);
				r.start();

				AddAuthorityObjectThread r2 = new AddAuthorityObjectThread(
						objectQueue, sharedHashMap);
				r2.start();

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
						bean.setDownloadedMB(countBytesReaded / 1024 / 1024);
						bean.pushDownloadInfo();
						aux = 0;
					}
					aux++;

					// don't allow queue size bigger than 900;
					while (bufferQueue.size() > 900) {
						Thread.sleep(1);
					}

				}
				// while (bufferQueue.size() > 0) {
				// Thread.sleep(1);
				// }

				doneReadingFile = true;

				// telling thread that we are done streaming
				r.setDoneReadingFile(true);
				r.join();
				
				fileName = r.getFileName();
				objectLines = r.getObjectLines();
				subjectLines = r.getSubjectLines();
				totalTriples = r.getTotalTriples();

				r2.setDoneSplittingString(true);
				r2.join();


				Iterator it = sharedHashMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					if ((Integer) pair.getValue() > 50) {
						if (((String) pair.getKey()).length() < 100) {
							authorityDomains.put((String) pair.getKey(),
									(Integer) pair.getValue());
						}
					}
					it.remove(); // avoids a ConcurrentModificationException
				}

				// while (doneAuthorityObject==false) {};

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

					if (aux % 1000 == 0) {
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
			// case failed in the last attempt
			if (!distribution.isSuccessfullyDownloaded()) {
				return true;
			}

			if (distribution.getHttpByteSize().equals(httpContentLength)
					|| distribution.getHttpLastModified().equals(
							httpLastModified)) {
				bean.setDownloadNumberOfDownloadedDistributions(bean
						.getDownloadNumberOfDownloadedDistributions() + 1);
				bean.pushDownloadInfo();
				return false;
			}

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
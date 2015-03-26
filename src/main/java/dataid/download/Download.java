package dataid.download;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import dataid.utils.Formats;

public class Download {

	final static Logger logger = Logger.getLogger(Download.class);

	// HTTP header fields
	public String httpDisposition = null;
	public String httpContentType = null;
	public double httpContentLength;
	public String httpLastModified = "0";

	protected static final int BUFFER_SIZE = 16384;
	public URL url = null;

	public String fileName = null;
	public String extension = null;

	HttpURLConnection httpConn = null;

	protected void getMetadataFromHTTPHeaders(HttpURLConnection httpConn) {

		httpDisposition = httpConn.getHeaderField("Content-Disposition");
		httpContentType = httpConn.getContentType();
		httpContentLength = httpConn.getContentLength();
		if (httpConn.getLastModified() > 0)
			httpLastModified = String.valueOf(httpConn.getLastModified());

	}

	protected InputStream getStream(URL url) throws Exception {
		this.url = url;
		openConnection();

		// opens input stream from HTTP connection
		InputStream inputStream = httpConn.getInputStream();
		logger.debug("InputStream from http connection opened");

		return inputStream;

	}

	private void openConnection() throws Exception {
		httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		logger.debug("Open HTTP connection for URL: " + url.toString());

		// check HTTP response code
		if (responseCode != HttpURLConnection.HTTP_OK) {
			httpConn.disconnect();
			throw new Exception(
					"No file to download. Server replied HTTP code: "
							+ responseCode);
		}
		logger.debug("Successfuly connected with HTTP OK status.");

	}

	protected void printHeaders() {
		DecimalFormat df = new DecimalFormat("#.##");

		logger.debug("Content-Type = " + httpContentType);
		logger.debug("Last-Modified = " + httpLastModified);
		logger.debug("Content-Disposition = " + httpDisposition);
		logger.debug("Content-Length = "
				+ df.format(httpContentLength / 1024 / 1024) + " MB");
		logger.debug("fileName = " + fileName);
	}

	protected InputStream getBZip2InputStream(InputStream inputStream) throws Exception {
		
		// check whether file is bz2 type
		if (getExtension().equals("bz2")) {
			logger.debug("File extension is bz2, creating BZip2CompressorInputStream...");
			inputStream = new BZip2CompressorInputStream(
					httpConn.getInputStream(), true);
			setFileName(getFileName().replace(".bz2", ""));

			logger.debug("Done creating BZip2CompressorInputStream! New file name is "
					+ fileName);
		}
		return inputStream;
	}
	
	protected InputStream getZipInputStream(InputStream inputStream) throws Exception {
		// check whether file is zip type
				if (getExtension().equals("zip")) {
					logger.debug("File extension is zip, creating ZipInputStream and checking compressed files...");
					DownloadZipUtils d = new DownloadZipUtils();
					d.checkZipFile(url);
					ZipInputStream zip = new ZipInputStream(httpConn.getInputStream());
					ZipEntry entry = zip.getNextEntry();
					fileName = entry.getName();
					inputStream = zip;
					logger.debug("Done, we found a single file: " + fileName);
				}
		return inputStream;
	}

	

	public String getFileName() {
		if (fileName == null) {
			// extracts file name from header field
			fileName = new FileNameFromURL().getFileName(url.toString(),
					httpDisposition);
			logger.debug("Found file name: " + fileName);
		}
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getExtension() {
		if (extension == null) {
			extension = FilenameUtils.getExtension(getFileName());
		}
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

}

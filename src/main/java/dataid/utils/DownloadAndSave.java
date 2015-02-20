package dataid.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FilenameUtils;

import dataid.DataID;
import dataid.DataIDGeneralProperties;
import dataid.server.DataIDBean;

public class DownloadAndSave {
	private static final int BUFFER_SIZE = 65536;

	public String fileName = "";
	public String disposition = null;
	public String contentType = null;
	public String dataIDFilePath = null;

	public String objectFilePath;

	public URL url = null;

	public double contentLength;
	public double contentLengthAfterDownloaded;
	public int subjectLines = 0;
	public int objectLines = 0;
	
	public AtomicInteger aint = new AtomicInteger(0);

	Queue<String> bufferQueue = new ConcurrentLinkedQueue<String>();
	boolean done = false;

	public String downloadFile(String fileURL) throws Exception {
		url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			disposition = httpConn.getHeaderField("Content-Disposition");
			contentType = httpConn.getContentType();
			contentLength = httpConn.getContentLength();

			if (disposition != null) {
				// extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10,
							disposition.length() - 1);
				}
			} else {
				// extracts file name from URL
				fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
						fileURL.length());
			}
			DecimalFormat df = new DecimalFormat("#.##");

			System.out.println("Content-Type = " + contentType);
			System.out.println("Content-Disposition = " + disposition);
			System.out.println("Content-Length = "
					+ df.format(contentLength / 1024 / 1024) + " MB");
			System.out.println("fileName = " + fileName);

			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();

			String extension = FilenameUtils.getExtension(fileName);

			// check if file is bz2 type
			if (extension.equals("bz2")) {
				inputStream = new BZip2CompressorInputStream(
						httpConn.getInputStream(), true);
				fileName = fileName.replace(".bz2", "");
			}
			
			dataIDFilePath = DataIDGeneralProperties.BASE_PATH + fileName;
			objectFilePath = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
					+ fileName;
			final byte[] buffer = new byte[BUFFER_SIZE];
			int n = 0;

			extension = FilenameUtils.getExtension(fileName);
			if (extension.equals("nt")) {
				split_and_store.start();
				while (-1 != (n = inputStream.read(buffer))) {
					String str = new String(buffer, "UTF-8");
					bufferQueue.add(str);
					contentLengthAfterDownloaded = contentLengthAfterDownloaded
							+ n;
					aint.incrementAndGet();
					while(bufferQueue.size()>900);
				}
				while(bufferQueue.size()>0);
			} else if (extension.equals("ttl") || extension.equals("rdf")) {
				int bytesRead = -1;
				FileOutputStream outputStream = new FileOutputStream(
						dataIDFilePath);
				while (-1 != (bytesRead = inputStream.read(buffer))) {
					outputStream.write(buffer, 0, bytesRead);
					contentLengthAfterDownloaded = contentLengthAfterDownloaded
							+ bytesRead;
					for (int i = 0; i < bytesRead; i++) {
						if (buffer[i] == '\n')
							objectLines++;
					}
				}
			} else
				throw new Exception("RDF format not supported: " + extension);

			done = true;

			// update file length
			if (contentLength < 1) {
				File f = new File(dataIDFilePath);
				contentLength = f.length();
			}

			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
					"File downloaded: " + fileName);

			return dataIDFilePath;
		} else {
			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_WARN,
					"No file to download. Server replied HTTP code: "
							+ responseCode);
		}
		httpConn.disconnect();
		return null;
	}

	Thread split_and_store = new Thread() {

		public synchronized void run() {

			try {
				FileOutputStream subject = new FileOutputStream(
						DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
								+ fileName);
				FileOutputStream object = new FileOutputStream(
						DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
								+ fileName);

				String lastLine = null;
				String tmpLastSubject = "";
				int count = 0;
				String u2[];
				String u3[];
				String tmp;
				String o[];
				while (!done || (bufferQueue.size() > 0)) {
					if(bufferQueue.size() > 0)
					aint.decrementAndGet();
					try {
						o = bufferQueue.remove().split("\n");
						if (lastLine != null) {
							o[0] = lastLine.concat(o[0]);
							lastLine = null;
						}

						for (String u : o) {
							if (!u.startsWith("#")) {
								try {
									u2 = u.split(" ");
									u3 = u.split("> <");

									// checking here offset
									tmp = u3[1];
									tmp = u2[3];

									if (!tmpLastSubject.equals(u2[0])) {
										tmpLastSubject = u2[0];
										subject.write(new String(u2[0] + "\n")
												.getBytes());
										subjectLines++;
									}
									if (!u2[2].startsWith("\"")) {
										object.write(new String(u2[2] + "\n")
												.getBytes());
										objectLines++;
										count++;
									}
									if (count % 100000 == 0) {
										System.out.println(count
												+ " registers written");
										System.out.println("Buffer queue size: "+aint.get());
										DataID.bean.setDownloadNumberOfTriplesLoaded(count);
										DataID.bean.pushDownloadInfo();
									}
								} catch (ArrayIndexOutOfBoundsException e) {
									lastLine = u;
								}
							}
						}

					} catch (NoSuchElementException em) {
						// em.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				DataID.bean.setDownloadNumberOfTriplesLoaded(count);
				DataID.bean.setDownloadNumberOfDownloadedDistributions(DataID.bean.getDownloadNumberOfDownloadedDistributions()+1);
				
				DataID.bean.pushDownloadInfo();
				object.close();
				subject.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}

	};

}
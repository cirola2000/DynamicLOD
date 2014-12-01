package dataid.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import dataid.DataID;
import dataid.DataIDGeneralProperties;

public class DownloadFile {
	private static final int BUFFER_SIZE = 4096;

	public String fileName = "";
	public String disposition = null;
	public String contentType = null;
	public String saveFilePath = null;

	public URL url = null;
	
	public double contentLength;

	public String downloadFile(String fileURL)
			throws Exception {
		url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();
		
		String saveDir = DataIDGeneralProperties.BASE_PATH;

		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			disposition = httpConn.getHeaderField("Content-Disposition");
			contentType = httpConn.getContentType();
			contentLength = httpConn.getContentLength();
			
//			if (contentLength<1)
//				throw new Exception("Impossible to read Content Length value from HTTP connection. Value found: "+contentLength);

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
			 saveFilePath = saveDir + File.separator + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			long startTime = System.currentTimeMillis();
			long tmpTime = 0;
			int tmpBytesRead = 0;
			double tmpBytesMissing = contentLength;

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			
			int showOnDisplay = 0;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
				long timeInSecs = (System.currentTimeMillis() - startTime) / 1000; 
				tmpBytesRead = tmpBytesRead + bytesRead;
				tmpBytesMissing = tmpBytesMissing - bytesRead;

				if (tmpTime != timeInSecs) {
					double speed = tmpBytesRead;
					System.out.println((tmpBytesRead / 1024) + "kbps");
					System.out.println("elapsed time: "
							+ df.format((tmpBytesMissing / 1024 / 1024)
									/ (speed / 1024 / 1024)) + " seconds");
					System.out.println("bytes missing "
							+ df.format(tmpBytesMissing / 1024 / 1024) + " MB");
					System.out.println();

					//show message each 10s
					if(showOnDisplay==10){
						showOnDisplay = 0;
					DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG, 
							"Speed: "+(tmpBytesRead / 1024) + "kbps" + ", elapsed time: "
									+ df.format((tmpBytesMissing / 1024 / 1024)
											/ (speed / 1024 / 1024)) + " seconds" + ", bytes missing "
											+ df.format(tmpBytesMissing / 1024 / 1024) + " MB");
					}
					showOnDisplay++;
					
					tmpTime = timeInSecs;
					tmpBytesRead = 0;
				}

			}

			outputStream.close();
			inputStream.close();

			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"File downloaded: "+fileName);

			return saveFilePath;
		} else {
			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_WARN,"No file to download. Server replied HTTP code: "
							+ responseCode);
		}
		httpConn.disconnect();
		return null;
	}

}
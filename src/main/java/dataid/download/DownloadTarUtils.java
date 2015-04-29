package dataid.download;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.log4j.Logger;

import dataid.exceptions.DataIDException;
import dataid.utils.FileUtils;

public class DownloadTarUtils {
	final static Logger logger = Logger.getLogger(DownloadTarUtils.class);

	static final int BUFFER = 512;

	public boolean checkCompressedDistribution() {

		return false;
	}

	public void checkTarFile(URL url) throws DataIDException {
		TarArchiveInputStream is = null;
		try {
			try {
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			InputStream inputStream = httpConn.getInputStream();
		 
		   is = new TarArchiveInputStream(new BufferedInputStream(inputStream));
		   
		   TarArchiveEntry entry = null;
			int count2 = 1;
			String fileName = null;
			
			logger.info("Testing TAR file...");

				while ((entry = (TarArchiveEntry) is.getNextEntry()) != null) {
					fileName = entry.getName();
					if (count2 > 1) {
						throw new DataIDException(
								"Too many entries compressed! TAR files should contains only the dump file.");
					}
					if (entry.isDirectory()) {
						throw new DataIDException(
								"We found a compressed directory ("
										+ entry.getName()
										+ "). TAR files should contains only the dump file.");
					}
					if (!FileUtils.acceptedFormats(entry.getName())) {
						throw new DataIDException(
								"The file format is invalid. "
										+ entry.getName());
					}
					count2++;
				}
				
				logger.info("TAR file is good to go.");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}

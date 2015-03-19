package dataid.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;

import dataid.exceptions.DataIDException;
import dataid.utils.FileUtils;

public class DownloadZipUtils {

	static final int BUFFER = 512;

	public boolean checkCompressedDistribution() {

		return false;
	}

	public void checkZipFile(URL url) throws DataIDException {
		ZipInputStream zis = null;
		try {
			try {
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			InputStream inputStream = httpConn.getInputStream();
		 
		   zis = new ZipInputStream(new BufferedInputStream(inputStream));
			ZipEntry entry = null;
			int count2 = 1;
			String fileName = null;

				while ((entry = zis.getNextEntry()) != null) {
					fileName = entry.getName();
					if (count2 > 1) {
						throw new DataIDException(
								"Too many entries compressed! ZIP files should contains only the dump file.");
					}
					if (entry.isDirectory()) {
						throw new DataIDException(
								"We found a compressed directory ("
										+ entry.getName()
										+ "). ZIP files should contains only the dump file.");
					}
					if (!FileUtils.acceptedFormats(entry.getName())) {
						throw new DataIDException(
								"The file format is invalid. "
										+ entry.getName());
					}
					count2++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} finally {
			try {
				zis.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}

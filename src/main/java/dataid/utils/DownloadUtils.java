package dataid.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dataid.exceptions.DataIDException;


public class DownloadUtils {

	static final int BUFFER = 512;

	public boolean checkCompressedDistribution() {

		return false;
	}

	public void checkZip(InputStream inputStream) throws DataIDException{
		ZipInputStream zis = null;
		try {			
			zis = new ZipInputStream(
					new BufferedInputStream(inputStream));
			ZipEntry entry;
			int count2 = 1;
			
			try {
				while ((entry = zis.getNextEntry()) != null) {
					if(count2>1){
						throw new DataIDException("Too many entries compressed! ZIP files should contains only the dump file.");
					}
					if (entry.isDirectory()){
						throw new DataIDException("We found a compressed directory ("+entry.getName()+"). ZIP files should contains only the dump file.");
					}
					if(!FileUtils.acceptedFormats(entry.getName())){
						throw new DataIDException("The file format is invalid. "+entry.getName());
					}
					count2++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		finally{
			try {
				zis.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}		
	
}

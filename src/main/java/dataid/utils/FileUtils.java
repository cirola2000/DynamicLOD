package dataid.utils;

import java.io.File;

import dataid.DataIDGeneralProperties;

public class FileUtils {

	public static void checkIfFolderExists(){
		
		// check if folders needed exists
		File f = new File(DataIDGeneralProperties.BASE_PATH);
		if(!f.exists())
			f.mkdirs();
		
		f = new File(DataIDGeneralProperties.FILTER_PATH);
		if(!f.exists())
			f.mkdirs();
		
		f = new File(DataIDGeneralProperties.MODELS_PATH);
		if(!f.exists())
			f.mkdirs();
		
		f = new File(DataIDGeneralProperties.SUBJECT_PATH);
		if(!f.exists())
			f.mkdirs();
		
		f = new File(DataIDGeneralProperties.OBJECT_PATH);
		if(!f.exists())
			f.mkdirs();
		
		f = new File(DataIDGeneralProperties.DATAID_PATH);
		if(!f.exists())
			f.mkdirs();
		
	}
	
}

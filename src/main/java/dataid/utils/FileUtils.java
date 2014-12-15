package dataid.utils;

import java.io.File;
import java.util.ArrayList;

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
	
	// TODO make this method more precise
	public static boolean acceptedFormats(String fileName){
		
		if(fileName.contains(".ttl"))
			return true;
		if(fileName.contains(".nt"))		
		return true;
		
		return false;
	}
	
}

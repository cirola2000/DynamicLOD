package dataid.files;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import dataid.DataID;
import dataid.DataIDGeneralProperties;

public class PrepareFiles {

	public String subjectFile;
	public String objectFile;
	
	public int objectTriples;
	public int totalTriples;
	
	
	public String transformTtlToNTriples(String fileName) throws Exception {
		
		String ext = FilenameUtils.getExtension(fileName);	
		
		
		// case format is turtle convert using rapper
		if(ext.equals("ttl")){
			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"File extension is ttl! Converting ttl to nt using rapper");
	    	totalTriples = RunCommand.run("rapper -g "+DataIDGeneralProperties.BASE_PATH+ fileName+" -o ntriples > "+DataIDGeneralProperties.BASE_PATH+FilenameUtils.getBaseName(fileName)+".nt");	    	
	    	RunCommand.run("rm "+DataIDGeneralProperties.BASE_PATH+ fileName);	    	
	    	return FilenameUtils.getBaseName(fileName)+".nt";			
		}		
		else if(!ext.equals("nt")){
			throw new Exception("Distribution: \""+fileName+"\" has an invalid file format "+ext+ ". File type should be \"nt\".");
		}
		
		return fileName;

	}

	public void separateSubjectAndObject(String fileName) throws Exception {
		
		// creates 2 files, one with subjects and other with objects
		DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Creating subject file: "
				+ DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
				+ fileName);
		RunCommand.run("cat " + DataIDGeneralProperties.BASE_PATH + fileName
				+ " |" + " cut -d\" \" -f1 > "
				+ DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
				+ fileName);
		
		subjectFile = DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
				+ fileName;

		DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Creating object file: "
				+ DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
				+ fileName);
		RunCommand.run("cat " + DataIDGeneralProperties.BASE_PATH + fileName
				+ " |" + " cut -d\" \" -f3 | grep '^<http' >"
				+ DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
				+ fileName);

		objectFile = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
				+ fileName;
		
		
		
	}

}

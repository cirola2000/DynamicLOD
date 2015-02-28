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
	
	public ArrayList<String> domains = new ArrayList<String>();
	
	
	public String transformTtlToNTriples(String fileName) throws Exception {
		
		String ext = FilenameUtils.getExtension(fileName);	
		
		
		// case format is turtle convert using rapper
		if(ext.equals("ttl")){
			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"File extension is ttl! Converting ttl to nt using rapper");
	    	totalTriples = RunCommand.run("rapper -i ntriples "+DataIDGeneralProperties.BASE_PATH+ fileName+" -o ntriples > "+DataIDGeneralProperties.BASE_PATH+FilenameUtils.getBaseName(fileName)+".nt");	    	
//	    	RunCommand.run("rm "+DataIDGeneralProperties.BASE_PATH+ fileName);	    	
	    	return FilenameUtils.getBaseName(fileName)+".nt";			
		}		
		else if(!ext.equals("nt")){
			throw new Exception("Distribution: \""+fileName+"\" has an invalid file format "+ext+ ". File type should be \"nt\".");
		}
		
		return fileName;

	}

	public void separateSubjectAndObject(String fileName) throws Exception {
		
		
		
//		awk {subjects=$1; objects=$3; if(lastlineSubjects!=subjects){ print subjects>"subject"; lastlineSubjects=subjects} if(objects~/^</){print objects>"object"}}
// awk '{subjects=$1; objects=$3; if(lastlineSubjects!=subjects){ print subjects>"subject"; lastlineSubjects=subjects} if(objects~/^</){print objects>"object"; print objects}}' article_categories_en.nt | awk -F/ '{print $1"//"$3"/"$4"/"}' | awk '!x[$0]++'		
//		RunCommand.run("awk '{subjects=$1; objects=$3; if(lastlineSubjects!=subjects){ print subjects>\""+ DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName +"\"; lastlineSubjects=subjects} if(objects~/^</){print objects>\""+DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName+"\"}}' "+DataIDGeneralProperties.BASE_PATH + fileName);
		
		// creates 2 files, one with subjects and other with objects
		DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Creating subject and object files: "
				+ DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
				+ fileName+ DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
				+ fileName);
		
		RunCommand r = new RunCommand();
		ArrayList<String> results = new ArrayList<String>();
		
//		domains = r.runAwk("awk '{subjects=$1; objects=$3; if(lastlineSubjects!=subjects){ print subjects>\""+ DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName +"\"; lastlineSubjects=subjects} if(objects~/^</){print objects>\""+DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName+"\"}}' "+DataIDGeneralProperties.BASE_PATH + fileName + " | awk -F/ '{print $1\"//\"$3\"/\"$4\"/\"}' | awk '!x[$0]++'");
		
		results = r.runAwk("awk 'BEGIN{objcount=0;} {subjects=$1; objects=$3; if(lastlineSubjects!=subjects){ print subjects>\""+DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
				+ fileName+"\"; lastlineSubjects=subjects} if(objects~/^</){print objects>\""+DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+ fileName+"\"; print objects; objcount++}} END{print objcount}' "+DataIDGeneralProperties.BASE_PATH + fileName+" | awk -F/ '{print $1\"//\"$3\"/\"$4\"/\"}' | awk '!x[$0]++'");
		
		objectTriples = Integer.parseInt(results.get(results.size()-1).replace("/", "").toString());
		results.remove(results.size()-1);
		for (String string : results) {
			domains.add(string.substring(1,string.length()));
		}
		
		
		
//		// creates 2 files, one with subjects and other with objects
//		DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Creating subject file: "
//				+ DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName);
//		RunCommand.run("cat " + DataIDGeneralProperties.BASE_PATH + fileName
//				+ " |" + " cut -d\" \" -f1 > "
//				+ DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName);
//		
//		subjectFile = DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName;
//
//		DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Creating object file: "
//				+ DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName);
//		RunCommand.run("cat " + DataIDGeneralProperties.BASE_PATH + fileName
//				+ " |" + " cut -d\" \" -f3 | grep '^<http' >"
//				+ DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName);
//
//		objectFile = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName;
		
		
		
	}

}

package dataid.files;

import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import dataid.DataID;
import dataid.DataIDGeneralProperties;
import dataid.utils.Formats;

public class PrepareFiles {

	public String subjectFile;
	public String objectFile;
	
	public int objectTriples;
	public int totalTriples;
	
	public ArrayList<String> domains = new ArrayList<String>();

	public void separateSubjectAndObject(String fileName, String extension) throws Exception {
		
		String rapperFormat = null;
		
		if (extension.equals(Formats.DEFAULT_TURTLE)) rapperFormat = "turtle";
		else if (extension.equals(Formats.DEFAULT_RDFXML)) rapperFormat = "rdfxml";
		
		
		// creates 2 files, one with subjects and other with objects
		DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Creating subject and object files: "
				+ DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
				+ fileName+ DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
				+ fileName);
		
		RunCommand r = new RunCommand();
		ArrayList<String> results = new ArrayList<String>();
	
		
		results = r.runRapper("rapper -i "+rapperFormat+" "+DataIDGeneralProperties.BASE_PATH+ fileName+
				" -o ntriples | awk 'BEGIN{objcount=0;} {subjects=$1; objects=$3; if(lastlineSubjects!=subjects){ print subjects>\""+
				DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
				+ fileName+"\"; lastlineSubjects=subjects} if(objects~/^</){print objects>\""+
				DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+ fileName+
				"\"; print objects; objcount++}} END{print \"[objectTriples] \" objcount}'  | awk -F/ '{print $1\"//\"$3\"/\"$4\"/\"}' | awk '!x[$0]++'");
		objectFile = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+ fileName;
		
		for (String string : results) {
			if(string.contains("[totalTriples]")){
				String a[] =string.split(" "); 
				totalTriples = Integer.parseInt(a[1]);
			}else if(string.contains("[objectTriples]")){
				String a[] =string.split(" "); 
				objectTriples = Integer.parseInt(a[1].replace("/", ""));
			}
			else{
				domains.add(string.substring(1,string.length()));
			}
		}		
	}
	
}

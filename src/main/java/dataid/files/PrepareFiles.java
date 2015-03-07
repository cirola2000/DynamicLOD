package dataid.files;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import dataid.DataIDGeneralProperties;
import dataid.server.DataIDBean;
import dataid.threads.AddAuthorityObjectThread;
import dataid.utils.Formats;

public class PrepareFiles {

	public String subjectFile;
	public String objectFile;
	
	public int objectTriples;
	public int totalTriples;
	
//	public ArrayList<String> domains = new ArrayList<String>();
	public Queue<String> domains = new ConcurrentLinkedQueue<String>();
	public Queue<String> results = new ConcurrentLinkedQueue<String>();

	public void separateSubjectAndObject(String fileName, String extension,  DataIDBean bean, boolean isDbpedia) throws Exception {
		
		String rapperFormat = null;
		
		if (extension.equals(Formats.DEFAULT_TURTLE)) rapperFormat = "turtle";
		else if (extension.equals(Formats.DEFAULT_RDFXML)) rapperFormat = "rdfxml";
		
		if(extension.equals(Formats.DEFAULT_TURTLE) && isDbpedia) rapperFormat = "ntriples";
		
		
		// creates 2 files, one with subjects and other with objects
		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Creating subject and object files: "
				+ DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
				+ fileName+ DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
				+ fileName);
		
		RunCommand r = new RunCommand();
		
	
		
//		r.runRapper("rapper -i "+rapperFormat+" "+DataIDGeneralProperties.BASE_PATH+ fileName+
//				" -o ntriples | awk 'BEGIN{objcount=0;} {subjects=$1; objects=$3; if(lastlineSubjects!=subjects){ print subjects>\""+
//				DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName+"\"; lastlineSubjects=subjects} if(objects~/^</){print objects>\""+
//				DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+ fileName+
//				"\"; print objects; objcount++}} END{print \"objectTriples \" objcount}'  | awk -F/ '{print $1\"//\"$3\"/\"$4\"/\"}' | awk '{x[$0]++; if(x[$0]==50 || ($0 ~ \"objectTriples\" )){print $0} }'", bean, domains);
		
		r.runRapper("rapper -i "+rapperFormat+" "+DataIDGeneralProperties.BASE_PATH+ fileName+
				" -o ntriples | awk 'BEGIN{objcount=0;} {subjects=$1; objects=$3; if(lastlineSubjects!=subjects){ print subjects>\""+
				DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
				+ fileName+"\"; lastlineSubjects=subjects} if(objects~/^</){print objects>\""+
				DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+ fileName+
				"\"; print objects; objcount++}} END{print \"objectTriples \" objcount}'  | awk -F/ '{print $1\"//\"$3\"/\"$4\"/\"}' | awk '{x[$0]++; if(x[$0]==49 || ($0 ~ \"objectTriples\" )){print $0} }'", bean, domains);
		
		
		
		
		objectFile = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+ fileName;
		
		objectTriples = r.objectTriples;
		totalTriples = r.totalTriples;
		// getting objects domain
		System.out.println("Loading objects domain");
		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Saving objects domain");
		
		System.out.println("Objects domain loaded.");		
		System.out.println();
		
	}
	
}

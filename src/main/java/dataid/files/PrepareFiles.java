package dataid.files;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import dataid.DataID;
import dataid.DataIDGeneralProperties;
import dataid.server.DataIDBean;
import dataid.threads.GetDomainsFromTriplesThread;
import dataid.utils.Formats;

public class PrepareFiles {
	
	final static Logger logger = Logger.getLogger(PrepareFiles.class);
	
	public String subjectFile;
	public String objectFile;
	
	public int objectTriples;
	public int totalTriples;
	
//	public ArrayList<String> domains = new ArrayList<String>();
	public ConcurrentHashMap<String,Integer> objectDomains = new ConcurrentHashMap<String,Integer>();
	public ConcurrentHashMap<String,Integer> subjectDomains = new ConcurrentHashMap<String,Integer>();
	public ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<String>();

	public void separateSubjectAndObject(String fileName, String extension,  boolean isDbpedia) throws Exception {
		
		String rapperFormat = null;
		
		if (Formats.getEquivalentFormat(extension).equals(Formats.DEFAULT_TURTLE)) rapperFormat = "turtle";
		else if (Formats.getEquivalentFormat(extension).equals(Formats.DEFAULT_RDFXML)) rapperFormat = "rdfxml";
		
		if(extension.equals(Formats.DEFAULT_TURTLE) && isDbpedia) rapperFormat = "ntriples";
		
		
		// creates 2 files, one with subjects and other with objects
		logger.info("Creating subject and object files: "
				+ DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
				+ fileName+ DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
				+ fileName);
		
		RunCommand r = new RunCommand();
		
//		r.runRapper("rapper -i "+rapperFormat+" "+DataIDGeneralProperties.BASE_PATH+ fileName+
//				" -o ntriples | awk 'BEGIN{objcount=0;} {subjects=$1; objects=$3; if(lastlineSubjects!=subjects){ print subjects>\""+
//				DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
//				+ fileName+"\"; lastlineSubjects=subjects} if(objects~/^</){print objects>\""+
//				DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+ fileName+
//				"\"; print objects; objcount++}} END{print \"objectTriples \" objcount}'  | awk -F/ '{gsub(\">\",\"\",$0);print $1\"//\"$3\"/\"$4\"/\"}' | awk '{x[$0]++; if(x[$0]==50 || ($0 ~ \"objectTriples\" )){print $0} }'", bean,subjectDomains, objectDomains);
//	
		r.runRapper("rapper -i "+rapperFormat+" "+DataIDGeneralProperties.BASE_PATH+ fileName+
				" -o ntriples | awk 'BEGIN{objcount=0;} {subjects=$1; objects=$3; if(lastlineSubjects!=subjects){ print subjects>\""+
				DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH+ fileName+
				"\"; print \"[subjectDomain]\"subjects; lastlineSubjects=subjects} if(objects~/^</){print objects>\""+
				DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+ fileName+
				"\"; print \"[objectDomain]\"objects; objcount++}} END{print \"objectTriples \" objcount}'  | awk -F/ '{gsub(\">\",\"\",$0);print $1\"//\"$3\"/\"$4\"/\"}' | awk '{x[$0]++; if(x[$0]==50 || ($0 ~ \"objectTriples\" )){print $0} }'", subjectDomains, objectDomains);
	
		
		objectFile = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+ fileName;
		
		objectTriples = r.objectTriples;
		totalTriples = r.totalTriples;
		
	}
}

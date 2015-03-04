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
		
	
		
		results = r.runRapper("rapper -i "+rapperFormat+" "+DataIDGeneralProperties.BASE_PATH+ fileName+
				" -o ntriples | awk 'BEGIN{objcount=0;} {subjects=$1; objects=$3; if(lastlineSubjects!=subjects){ print subjects>\""+
				DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
				+ fileName+"\"; lastlineSubjects=subjects} if(objects~/^</){print objects>\""+
				DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+ fileName+
				"\"; print objects; objcount++}} END{print \"[objectTriples] \" objcount}'  | awk -F/ '{print $1\"//\"$3\"/\"$4\"/\"}' | awk '!x[$0]++'", bean);
		objectFile = DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH+ fileName;
		
		// getting objects domain
		System.out.println("Saving objects domain");
		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Saving objects domain");
		
		for (String string : results) {
			if(string.contains("[totalTriples]")){
				String a[] =string.split(" "); 
				totalTriples = Integer.parseInt(a[1]);
			}else if(string.contains("[objectTriples]")){
				String a[] =string.split(" "); 
				objectTriples = Integer.parseInt(a[1].replace("/", ""));
			}
			else{
//				domains.add(string.substring(1,string.length()));
			}
		}		
		System.out.println("Creating threads");
		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Creating threads");
		
		AddAuthorityObjectThread r2 = new AddAuthorityObjectThread(
				results, domains);
		r2.start();
		AddAuthorityObjectThread r3 = new AddAuthorityObjectThread(
				results, domains);
		r3.start();
		AddAuthorityObjectThread r4 = new AddAuthorityObjectThread(
				results, domains);
		r4.start();

		AddAuthorityObjectThread r5 = new AddAuthorityObjectThread(
				results, domains);
		r5.start();

		AddAuthorityObjectThread r6 = new AddAuthorityObjectThread(
				results, domains);
		r6.start();

		AddAuthorityObjectThread r7 = new AddAuthorityObjectThread(
				results, domains);
		r7.start();
		
		Thread.sleep(190);
		r2.setDoneSplittingString(true);
		r3.setDoneSplittingString(true);
		r4.setDoneSplittingString(true);
		r5.setDoneSplittingString(true);
		r6.setDoneSplittingString(true);
		r7.setDoneSplittingString(true);
		
		r2.join();
		r3.join();
		r4.join();
		r5.join();
		r6.join();
		r7.join();
		
		System.out.println();
		
	}
	
}

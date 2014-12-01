package dataid;

public class DataIDGeneralProperties {

	// defining what path should be used to store files
	public static final String BASE_PATH ="/tmp/dataid/";
	
	// defining filter path
	public static final String FILTER_PATH =BASE_PATH+"filters/";
	
	// defining subject file path
	public static final String SUBJECT_PATH =BASE_PATH+"subjects/";
	
	// defining object file path
	public static final String OBJECT_PATH =BASE_PATH+"objects/";
	
	// defining dataids file path
	public static final String DATAID_PATH =BASE_PATH+"dataid/";
	
	// defining where to store model that describes distributions files in the file system
	public static final String MODELS_PATH =BASE_PATH+"models/";
	
	
	// defining dataset file suffix
	public static final String DISTRIBUTION_SUFFIX ="distribution_";
	
	
	// defining file names for distributions after separate subject and object
	public static final String SUBJECT_FILE_DISTRIBUTION_PATH =SUBJECT_PATH +"subject_distribution_";
	public static final String OBJECT_FILE_DISTRIBUTION_PATH =OBJECT_PATH +"object_distribution_";
	
	// defining file names for filters for subjects and objects
	public static final String SUBJECT_FILE_FILTER_PATH =FILTER_PATH +"subject_filter_";
	public static final String OBJECT_FILE_FILTER_PATH =FILTER_PATH +"object_filter_";
	
	// defining rdf file to store data about saved files
	public static final String FS_MODEL = MODELS_PATH+"fs_model";
	
	// defining dataID graph file
	public static final String DATAID_GRAPH_MODEL_PATH = "src/main/webapp/graph/dataid_graph.ttl";
	
	
	// defining server properties
	public static final String MESSAGE_INFO ="info";
	public static final String MESSAGE_LOG ="log";
	public static final String MESSAGE_WARN ="warn";
	public static final String MESSAGE_ERROR ="error"; 
	
}

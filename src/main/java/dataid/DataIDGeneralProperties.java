package dataid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class DataIDGeneralProperties {

	public void DataIDGeneralProperties() {
	}

	public void loadProperties() {
		try {
			String result = "";
			Properties prop = new Properties();
			String propFileName = "resources/config.properties";

			InputStream inputStream = new FileInputStream(propFileName);

			prop.load(inputStream);

			Date time = new Date(System.currentTimeMillis());

			// get the property value and print it out
			BASE_PATH = prop.getProperty("BASE_PATH");
			MONGODB_HOST = prop.getProperty("MONGODB_HOST");
			MONGODB_PORT = Integer.valueOf(prop.getProperty("MONGODB_PORT"));
			MONGODB_DB = prop.getProperty("MONGODB_DB");
			MONGODB_SECURE_MODE = Boolean.valueOf(prop.getProperty("MONGODB_SECURE_MODE"));
			MONGODB_USERNAME = prop.getProperty("MONGODB_USERNAME");
			MONGODB_PASSWORD = prop.getProperty("MONGODB_PASSWORD");
			LOV_URL = prop.getProperty("LOV_URL");
			
			
			USE_MULTITHREAD = prop.getProperty("USE_MULTITHREAD");
			FILTER_PATH = BASE_PATH + "filters/";
			SUBJECT_PATH = BASE_PATH + "subjects/";
			OBJECT_PATH = BASE_PATH + "objects/";
			DATAID_PATH = BASE_PATH + "dataid/";
			AUTHORITY_FILTER_PATH = BASE_PATH + "authority_filter";
			DISTRIBUTION_PREFIX = "distribution_";
			SUBJECT_FILE_DISTRIBUTION_PATH = SUBJECT_PATH
					+ "subject_distribution_";
			OBJECT_FILE_DISTRIBUTION_PATH = OBJECT_PATH
					+ "object_distribution_";
			SUBJECT_FILE_FILTER_PATH = FILTER_PATH + "subject_filter_";
			OBJECT_FILE_FILTER_PATH = FILTER_PATH + "object_filter_";
			OBJECT_FILE_LOV_PATH = FILTER_PATH + "lov_object";
			SUBJECT_FILE_LOV_PATH = FILTER_PATH + "lov_subject";
			FILTER_FILE_LOV_PATH = FILTER_PATH + "lov_filter";
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// defining what path should be used to store files
	public static String BASE_PATH;

	// defining filter path
	public static String FILTER_PATH;
	public static String AUTHORITY_FILTER_PATH;
	
	// defining subject file path
	public static String SUBJECT_PATH;

	// defining object file path
	public static String OBJECT_PATH;

	// defining dataids file path
	public static String DATAID_PATH;

	// defining dataset file suffix
	public static String DISTRIBUTION_PREFIX;

	// defining file names for distributions after separate subject and object
	public static String SUBJECT_FILE_DISTRIBUTION_PATH;
	public static String OBJECT_FILE_DISTRIBUTION_PATH;

	// defining file names for filters for subjects and objects
	public static String SUBJECT_FILE_FILTER_PATH;
	public static String OBJECT_FILE_FILTER_PATH;
	
	// defining path for LOV
	public static String SUBJECT_FILE_LOV_PATH;
	public static String OBJECT_FILE_LOV_PATH;
	public static String FILTER_FILE_LOV_PATH;

	// defining server properties
	public static final String MESSAGE_INFO = "info";
	public static final String MESSAGE_LOG = "log";
	public static final String MESSAGE_WARN = "warn";
	public static final String MESSAGE_ERROR = "error";

	// mongodb properties
	public static String MONGODB_HOST;
	public static int MONGODB_PORT;
	public static String MONGODB_DB;
	public static Boolean MONGODB_SECURE_MODE;
	public static String MONGODB_USERNAME;
	public static String MONGODB_PASSWORD;

	public static String USE_MULTITHREAD;
	public static String LOV_URL;

}

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
			USE_MULTITHREAD = prop.getProperty("USE_MULTITHREAD");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// defining what path should be used to store files
	public static String BASE_PATH;

	// defining filter path
	public static final String FILTER_PATH = BASE_PATH + "filters/";

	// defining subject file path
	public static final String SUBJECT_PATH = BASE_PATH + "subjects/";

	// defining object file path
	public static final String OBJECT_PATH = BASE_PATH + "objects/";

	// defining dataids file path
	public static final String DATAID_PATH = BASE_PATH + "dataid/";

	// defining dataset file suffix
	public static final String DISTRIBUTION_PREFIX = "distribution_";

	// defining file names for distributions after separate subject and object
	public static final String SUBJECT_FILE_DISTRIBUTION_PATH = SUBJECT_PATH
			+ "subject_distribution_";
	public static final String OBJECT_FILE_DISTRIBUTION_PATH = OBJECT_PATH
			+ "object_distribution_";

	// defining file names for filters for subjects and objects
	public static final String SUBJECT_FILE_FILTER_PATH = FILTER_PATH
			+ "subject_filter_";
	public static final String OBJECT_FILE_FILTER_PATH = FILTER_PATH
			+ "object_filter_";

	// defining server properties
	public static final String MESSAGE_INFO = "info";
	public static final String MESSAGE_LOG = "log";
	public static final String MESSAGE_WARN = "warn";
	public static final String MESSAGE_ERROR = "error";

	// mongodb properties
	public static String MONGODB_HOST;
	public static int MONGODB_PORT;
	public static String MONGODB_DB;

	public static String USE_MULTITHREAD;

}

package dataid.mongodb;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import dataid.DataIDGeneralProperties;
import dataid.exceptions.DataIDException;

abstract public class DataIDDB {

	// defining mongodb connection
	protected static MongoClient mongo = null;

	// defining mongodb database
	static DB db;

	// defining collection name
	protected String collectionName;

	protected DBCollection objectCollection;

	protected BasicDBObject mongoDBObject = new BasicDBObject();

	// defining mongodb keys -> RDF properties
	public final String CREATED_TIMESTAMP = "createdTimestamp";

	public final String MODIFIED_TIMESTAMP = "modifiedTimestamp";

	public static final String URI = "_id";

	protected String uri = null;

	// abstract methods
	abstract public boolean updateObject() throws DataIDException;

	abstract protected boolean loadObject();

	// collectionName is mandatory since we need to know where to save the rdf
	// entry
	public DataIDDB(String collectionName, String uri) {

		try {
			getInstance();

			this.collectionName = collectionName;

			objectCollection = db.getCollection(collectionName);

			mongoDBObject.put(URI, uri);

			this.uri = uri;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static DB getInstance() {
		try {
			if (mongo == null) {
				mongo = new MongoClient(DataIDGeneralProperties.MONGODB_HOST, DataIDGeneralProperties.MONGODB_PORT);
				db = mongo.getDB(DataIDGeneralProperties.MONGODB_DB);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return db;
	}

	protected void insert() throws DataIDException {

		// adding object URI
		if (uri == null)
			throw new DataIDException(
					"Error while saving. Object URI can't be null.");

		// check if URI already exists
		BasicDBObject tmp = new BasicDBObject();
		tmp.put(URI, uri);
		DBCursor d = objectCollection.find(tmp);
		if (d.hasNext())
			throw new DataIDException("Can't save object with URI: " + uri
					+ ". Object already exists.");

		// adding timestamp value
		mongoDBObject.put(CREATED_TIMESTAMP, new Date());

		// saving object to mongodb
		objectCollection.insert(mongoDBObject);
	}

	protected boolean update() throws DataIDException {

		if (uri == null)
			return false;
		
		// adding timestamp value
		mongoDBObject.put(MODIFIED_TIMESTAMP, new Date());

		BasicDBObject query = new BasicDBObject();
		query.put(URI, uri);
		BasicDBObject objectToUpdate = (BasicDBObject) mongoDBObject.clone();
		objectToUpdate.removeField("_id");

		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", objectToUpdate);

		if (objectCollection.update(query, updateObj).isUpdateOfExisting())
			return true;
		else {
			throw new DataIDException("Object with URI: " + uri
					+ " could not be found in database.");
		}
	}

	protected DBObject search() {

		// adding object URI
		if (uri == null)
			return null;

		DBCursor d = objectCollection.find(mongoDBObject);
		if (d.hasNext())
			return d.next();

		return null;
	}

	public String getUri() {
		return uri;
	}
}

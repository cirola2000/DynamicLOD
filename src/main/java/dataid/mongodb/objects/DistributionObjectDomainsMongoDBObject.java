package dataid.mongodb.objects;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dataid.exceptions.DataIDException;
import dataid.mongodb.DataIDDB;

public class DistributionObjectDomainsMongoDBObject extends DataIDDB {

	// Collection name
	public static final String COLLECTION_NAME = "DistributionObjectDomains";

	
	// class properties
	public static final String DISTRIBUTION_URI = "distributionURI";
	
	public static final String OBJECT_DOMAIN = "objectDomain";	
	
	
	private String distributionURI;

	private String objectDomain;
	
	
	public DistributionObjectDomainsMongoDBObject(String uri) {
		
		super(COLLECTION_NAME, uri);
		loadObject();
	}

	public boolean updateObject(boolean checkBeforeInsert) throws DataIDException {

		// save object case it doens't exists
		try {
			// updating subjectsTarget on mongodb
			mongoDBObject.put(DISTRIBUTION_URI, distributionURI);

			// updating objectsTarget on mongodb
			mongoDBObject.put(OBJECT_DOMAIN, objectDomain);
			insert(checkBeforeInsert);
		} catch (Exception e2) {
			// e2.printStackTrace();

			try {
				if (update())
					return true;
				else
					return false;
			} catch (DataIDException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	protected boolean loadObject() {
		DBObject obj = search();

		if (obj != null) {

			distributionURI = (String) obj.get(DISTRIBUTION_URI);

			objectDomain = (String) obj.get(OBJECT_DOMAIN);

			return true;
		}
		return false;
	}
	
	public boolean remove(){
		BasicDBObject tmp = new BasicDBObject();
		tmp.put(DISTRIBUTION_URI, distributionURI);
		DBCursor d = objectCollection.find(tmp);
		objectCollection.remove(tmp);
		return true;
	}

	public String getDistributionURI() {
		return distributionURI;
	}

	public void setDistributionURI(String distributionURI) {
		this.distributionURI = distributionURI;
	}

	public String getObjectDomain() {
		return objectDomain;
	}

	public void setObjectDomain(String objectDomain) {
		this.objectDomain = objectDomain;
	}
	
	

}

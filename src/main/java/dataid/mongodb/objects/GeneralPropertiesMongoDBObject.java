package dataid.mongodb.objects;

import com.mongodb.DBObject;

import dataid.exceptions.DataIDException;
import dataid.mongodb.DataIDDB;

public class GeneralPropertiesMongoDBObject extends DataIDDB {

	// Collection name
	public static final String COLLECTION_NAME = "systemProperties";

	public static final String DOWNLOADED_LOV = "downloadedLOV";

	// class properties

	private Boolean downloadedLOV;

	public GeneralPropertiesMongoDBObject() {
		super(COLLECTION_NAME, COLLECTION_NAME);
		loadObject();
	}

	public boolean updateObject(boolean checkBeforeInsert) {
		try {
			mongoDBObject.put(DOWNLOADED_LOV, downloadedLOV);

			insert(checkBeforeInsert);
			return true;
		} catch (Exception e2) {
			// e2.printStackTrace();

			try {
				if (update())
					return true;
				else
					return false;
			} catch (DataIDException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	protected boolean loadObject() {
		DBObject obj = search();

		if (obj != null) {
			// mongoDBObject = (BasicDBObject) obj;

			downloadedLOV = (Boolean) obj.get(DOWNLOADED_LOV);

			// System.out.println(obj);
			return true;
		}
		return false;
	}

	public Boolean getDownloadedLOV() {
		return downloadedLOV;
	}

	public void setDownloadedLOV(Boolean downloadedLOV) {
		this.downloadedLOV = downloadedLOV;
	}

}

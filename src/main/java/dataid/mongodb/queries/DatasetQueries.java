package dataid.mongodb.queries;

import java.util.ArrayList;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dataid.mongodb.DataIDDB;
import dataid.mongodb.objects.DatasetMongoDBObject;

public class DatasetQueries {
	// return number of datasets
	
	public static int getNumberOfDatasets() {
		int numberOfDatasets = 0;
		try {
			DBCollection collection = DataIDDB.getInstance().getCollection(
					DatasetMongoDBObject.COLLECTION_NAME);
			numberOfDatasets = (int) collection.count();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numberOfDatasets;
	}
	
	// return all datasets
	public static ArrayList<DatasetMongoDBObject> getDatasets() {

		ArrayList<DatasetMongoDBObject> list = new ArrayList<DatasetMongoDBObject>();

		try {
			DBCollection collection = DataIDDB.getInstance().getCollection(
					DatasetMongoDBObject.COLLECTION_NAME);
			DBCursor instances = collection.find();

			for (DBObject instance : instances) {
				list.add(new DatasetMongoDBObject(instance.get(DataIDDB.URI)
						.toString()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}

package dataid.mongodb.queries;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import dataid.mongodb.DataIDDB;
import dataid.mongodb.objects.SubsetMongoDBObject;

public class SubsetQueries {
	// return number of subsets
		public static int getNumberOfSubsets() {
			int numberOfSubsets = 0;
			try {
				DBCollection collection = DataIDDB.getInstance().getCollection(
						SubsetMongoDBObject.COLLECTION_NAME);
				
				BasicDBObject query = new BasicDBObject(SubsetMongoDBObject.DISTRIBUTIONS_URIS+".0", new BasicDBObject("$exists", true));
				DBCursor cursor = collection.find(query);			
				
				numberOfSubsets = (int) cursor.count();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return numberOfSubsets;
		}
}

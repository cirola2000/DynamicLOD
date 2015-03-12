package dataid.mongodb.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dataid.mongodb.DataIDDB;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.objects.LinksetMongoDBObject;

public class LinksetQueries {

	public ArrayList<LinksetMongoDBObject> getLinksets() {

		ArrayList<LinksetMongoDBObject> list = new ArrayList<LinksetMongoDBObject>();

		try {
			DBCollection collection = DataIDDB.getInstance().getCollection(
					LinksetMongoDBObject.COLLECTION_NAME);
			DBCursor instances = collection.find();

			for (DBObject instance : instances) {
				list.add(new LinksetMongoDBObject(instance.get(DataIDDB.URI)
						.toString()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// @Test
	// public void getLinksetsGroupByDatasets() {
	public static ArrayList<LinksetMongoDBObject> getLinksetsGroupByDatasets() {
		AggregationOutput output;
		try {

			DBCollection collection = DataIDDB.getInstance().getCollection(
					LinksetMongoDBObject.COLLECTION_NAME);

			// Now the $group operation
			DBObject groupFields = new BasicDBObject("_id", new BasicDBObject(
					"objectsDatasetTarget", "$objectsDatasetTarget").append(
					"subjectsDatasetTarget", "$subjectsDatasetTarget"));
			groupFields.put("id", new BasicDBObject("$first", "$_id"));

			DBObject group = new BasicDBObject("$group", groupFields);

			// run aggregation
			List<DBObject> pipeline = Arrays.asList(group);
			output = collection.aggregate(pipeline);

			// for (DBObject result : output.results()) {
			// System.out.println(result);
			// }
			ArrayList<LinksetMongoDBObject> linksetList = new ArrayList<LinksetMongoDBObject>();

			for (DBObject result : output.results()) {
				linksetList.add(new LinksetMongoDBObject(result.get("id")
						.toString()));
			}

			return linksetList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<LinksetMongoDBObject> getLinksetsGroupByDistributions() {
		
		try {
			ArrayList<LinksetMongoDBObject> list = new ArrayList<LinksetMongoDBObject>();

				DBCollection collection = DataIDDB.getInstance().getCollection(
						LinksetMongoDBObject.COLLECTION_NAME);
				DBCursor instances = collection.find();

				for (DBObject instance : instances) {
					list.add(new LinksetMongoDBObject(instance
							.get(DataIDDB.URI).toString()));
				}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<LinksetMongoDBObject> getLinksetsInDegreeByDistribution(String url) {
		ArrayList<LinksetMongoDBObject> list = new ArrayList<LinksetMongoDBObject>();
		try {
			
			DBCollection collection = DataIDDB.getInstance().getCollection(
					LinksetMongoDBObject.COLLECTION_NAME);
						
			BasicDBObject query = new BasicDBObject(LinksetMongoDBObject.OBJECTS_DISTRIBUTION_TARGET, url);
			DBCursor d = collection.find(query);

			while(d.hasNext()) {
			    list.add(new LinksetMongoDBObject(d.next().get(DataIDDB.URI).toString()));
			}
				
			return list;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<LinksetMongoDBObject> getLinksetsOutDegreeByDistribution(String url) {
		ArrayList<LinksetMongoDBObject> list = new ArrayList<LinksetMongoDBObject>();
		try {
			
			DBCollection collection = DataIDDB.getInstance().getCollection(
					LinksetMongoDBObject.COLLECTION_NAME);
						
			BasicDBObject query = new BasicDBObject(LinksetMongoDBObject.SUBJECTS_DISTRIBUTION_TARGET, url);
			DBCursor d = collection.find(query);

			while(d.hasNext()) {
			    list.add(new LinksetMongoDBObject(d.next().get(DataIDDB.URI).toString()));
			}
				
			return list;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

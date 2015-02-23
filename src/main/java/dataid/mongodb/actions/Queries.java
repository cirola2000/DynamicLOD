package dataid.mongodb.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dataid.mongodb.DataIDDB;
import dataid.mongodb.objects.DatasetMongoDBObject;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.objects.LinksetMongoDBObject;
import dataid.mongodb.objects.SubsetMongoDBObject;

public class Queries {
	
	// find distributions by authority
	public static ArrayList<DistributionMongoDBObject> getDistributionsByAuthority(Object authority){
		ArrayList<DistributionMongoDBObject> list = new ArrayList<DistributionMongoDBObject>();
		
		DBCollection collection = DataIDDB.getInstance().getCollection(
				DistributionMongoDBObject.COLLECTION_NAME);
		
		BasicDBObject query = new BasicDBObject("authority", new BasicDBObject("$in", authority));

		DBCursor cursor = collection.find(query);
		try {
		    while (cursor.hasNext()) {
		    	
		    	DistributionMongoDBObject obj = new DistributionMongoDBObject(cursor.next().get(DistributionMongoDBObject.ACCESS_URL).toString());
		    	
		        list.add(obj);
		    }
		} finally {
		    cursor.close();
		}
		return list;
		
	}

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

	// return number of subsets
	public static int getNumberOfSubsets() {
		int numberOfSubsets = 0;
		try {
			DBCollection collection = DataIDDB.getInstance().getCollection(
					SubsetMongoDBObject.COLLECTION_NAME);
			numberOfSubsets = (int) collection.count();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numberOfSubsets;
	}
	
	// return number of distributions
	public static int getNumberOfDistributions() {
		int numberOfDistributions = 0;
		try {
			DBCollection collection = DataIDDB.getInstance().getCollection(
					DistributionMongoDBObject.COLLECTION_NAME);
			numberOfDistributions = (int) collection.count();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numberOfDistributions;
	}

	// return all DataIDs file
	public static String getDataIDs() {
		String str = "";

		try {
			DBCollection collection = DataIDDB.getInstance().getCollection(
					DatasetMongoDBObject.COLLECTION_NAME);
			DBCursor instances = collection.find();

			for (DBObject instance : instances) {

				str = str + instance.get(DataIDDB.URI).toString() + "<br>";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	// return all datasets
	public ArrayList<DatasetMongoDBObject> getDatasets() {

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
	public ArrayList<LinksetMongoDBObject> getLinksetsGroupByDatasets() {
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

	public void getDatasetsLinksets() {
		try {

			DBCollection collection = DataIDDB.getInstance().getCollection(
					LinksetMongoDBObject.COLLECTION_NAME);

			CommandResult commandResult = DataIDDB
					.getInstance()
					.command(
							"db.Linkset.group({key: { 'objectsDatasetTarget': 1, 'subjectsDatasetTarget': 1 },"
									+ "reduce: function( curr, result ) { "
									+ "result.total += curr.links; "
									+ " },  "
									+ " initial: { total : 0} " + "})");

			// System.out.println(commandResult);

			// build the $projection operation
			DBObject fields = new BasicDBObject("objectsDatasetTarget", 1);
			fields.put("subjectsDatasetTarget", 1);
			DBObject project = new BasicDBObject("$project", fields);

			// Now the $group operation
			DBObject groupFields = new BasicDBObject("_id", new BasicDBObject(
					"objectsDatasetTarget", "$objectsDatasetTarget").append(
					"subjectsDatasetTarget", "$subjectsDatasetTarget"));
			DBObject group = new BasicDBObject("$group", groupFields);

			// Finally the $sort operation
			DBObject sort = new BasicDBObject("$sort", new BasicDBObject(
					"objectsDatasetTarget", 1).append("subjectsDatasetTarget",
					1));

			// run aggregation
			List<DBObject> pipeline = Arrays.asList(project, group, sort);
			AggregationOutput output = collection.aggregate(pipeline);

			for (DBObject result : output.results()) {
				System.out.println(result);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

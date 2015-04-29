package dataid.mongodb.actions;

import java.util.Arrays;
import java.util.List;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dataid.mongodb.DataIDDB;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.objects.LinksetMongoDBObject;

public class Queries {
	

	// return all DataIDs file
	public static String getHTMLDistributionStatus() {
		String str = "";

		try {
			DBCollection collection = DataIDDB.getInstance().getCollection(
					DistributionMongoDBObject.COLLECTION_NAME);
			DBCursor instances = collection.find().limit(1000);

			str = str + "<table><tr><th style=\"width:570px\">DownloadURL</th><th style=\"margin-left:15px\">Status</th></tr>";
			for (DBObject instance : instances) {
				str = str + "<tr><td>";
				str = str + instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString();
				str = str + "</td><td>";
				str = str + "<span style=\"color:green\"> "+instance.get(DistributionMongoDBObject.STATUS).toString()+"</span>";
//				str = str + "</td><td>";
//				if(instance.get(DistributionMongoDBObject.SUCCESSFULLY_DOWNLOADED).toString() == "true"){
////					str = str + " <span style=\"color:green\"> OK! </span>";
//				}
//				else{
//					if(instance.get(DistributionMongoDBObject.LAST_ERROR_MSG) != null)
//						str = str +" <span style=\"color:red\">"+ instance.get(DistributionMongoDBObject.LAST_ERROR_MSG).toString()+"</span>";
//					
//				}
				str = str + "</tr></td>";
			}
			str = str + "</table>";

		} catch (Exception e) {
			str = str + "</table>";
			e.printStackTrace();
		}
		return str;
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

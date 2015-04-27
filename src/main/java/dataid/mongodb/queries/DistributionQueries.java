package dataid.mongodb.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.taglibs.standard.lang.jstl.test.Bean1;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dataid.mongodb.DataIDDB;
import dataid.mongodb.actions.MakeLinksets;
import dataid.mongodb.objects.DistributionMongoDBObject;
import dataid.mongodb.objects.DistributionObjectDomainsMongoDBObject;
import dataid.mongodb.objects.DistributionSubjectDomainsMongoDBObject;
import dataid.mongodb.objects.LinksetMongoDBObject;

public class DistributionQueries {
	
	final static Logger logger = Logger.getLogger(DistributionQueries.class);

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
	

	public static ArrayList<DistributionMongoDBObject> getDistributionsByAuthority(
			String distributionAccessURL) {
		ArrayList<DistributionMongoDBObject> list = new ArrayList<DistributionMongoDBObject>();
		try {

			DBCollection collection = DataIDDB.getInstance().getCollection(
					DistributionObjectDomainsMongoDBObject.COLLECTION_NAME);

			// get all subject domain from distribution got as parameter
			BasicDBObject query = new BasicDBObject(
					DistributionObjectDomainsMongoDBObject.DISTRIBUTION_URI,
					distributionAccessURL);

			BasicDBObject fields = new BasicDBObject(
					DistributionObjectDomainsMongoDBObject.OBJECT_DOMAIN, 1);
			fields.append("_id", 0);
			DBCursor cursor = collection.find(query, fields);

			ArrayList<String> vals = new ArrayList<String>();
			while (cursor.hasNext()) {
				vals.add((String) cursor.next().get(
						DistributionObjectDomainsMongoDBObject.OBJECT_DOMAIN));
			}

			BasicDBObject fields2 = new BasicDBObject(
					DistributionSubjectDomainsMongoDBObject.DISTRIBUTION_URI, 1);
			fields2.append("_id", 0);

			// find distributions with subjects
			BasicDBObject query2 = new BasicDBObject(
					DistributionSubjectDomainsMongoDBObject.SUBJECT_DOMAIN,
					new BasicDBObject("$in", vals));

			collection = DataIDDB.getInstance().getCollection(
					DistributionSubjectDomainsMongoDBObject.COLLECTION_NAME);

			cursor = collection.find(query2, fields2);

			while (cursor.hasNext()) {
				DistributionMongoDBObject obj = new DistributionMongoDBObject(
						cursor.next()
								.get(DistributionSubjectDomainsMongoDBObject.DISTRIBUTION_URI)
								.toString());

					list.add(obj);
//				logger.debug("Returned: " + obj.getDownloadUrl());
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// return number of triples
	public static int getNumberOfTriples() {
		int numberOfTriples = 0;
		try {
			DBCollection collection = DataIDDB.getInstance().getCollection(
					DistributionMongoDBObject.COLLECTION_NAME);

			BasicDBObject select = new BasicDBObject("$match",
					new BasicDBObject(
							DistributionMongoDBObject.SUCCESSFULLY_DOWNLOADED,
							true));

			BasicDBObject groupFields = new BasicDBObject("_id", null);

			groupFields.append("sum", new BasicDBObject("$sum", "$triples"));

			DBObject group = new BasicDBObject("$group", groupFields);

			// run aggregation
			List<DBObject> pipeline = Arrays.asList(select, group);
			AggregationOutput output = collection.aggregate(pipeline);

			for (DBObject result : output.results()) {
				numberOfTriples = Integer.valueOf(result.get("sum").toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return numberOfTriples;
	}

	// return all distributions
	public static ArrayList<DistributionMongoDBObject> getDistributions() {

		ArrayList<DistributionMongoDBObject> list = new ArrayList<DistributionMongoDBObject>();

		try {
			DBCollection collection = DataIDDB.getInstance().getCollection(
					DistributionMongoDBObject.COLLECTION_NAME);
			DBCursor instances = collection.find();

			for (DBObject instance : instances) {
				list.add(new DistributionMongoDBObject(instance.get(
						DataIDDB.URI).toString()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static DistributionMongoDBObject getByDownloadURL(String url) {

		DistributionMongoDBObject dist = null;
		try {

			DBCollection collection = DataIDDB.getInstance().getCollection(
					DistributionMongoDBObject.COLLECTION_NAME);

			BasicDBObject query = new BasicDBObject(
					DistributionMongoDBObject.DOWNLOAD_URL, url);
			DBCursor d = collection.find(query);

			if (d.hasNext()) {
				dist = new DistributionMongoDBObject(d.next()
						.get(DistributionMongoDBObject.URI).toString());
				return dist;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// return all distributions
	public static ArrayList<DistributionMongoDBObject> getDistributionsWithLinks() {

		ArrayList<DistributionMongoDBObject> list = new ArrayList<DistributionMongoDBObject>();
		ArrayList<String> distributions = new ArrayList<String>();
		
		try {
			DBCollection collection = DataIDDB.getInstance().getCollection(
					DistributionMongoDBObject.COLLECTION_NAME);
			DBCollection collection2 = DataIDDB.getInstance().getCollection(
					LinksetMongoDBObject.COLLECTION_NAME);
			DBCursor instances = collection.find();

			for (DBObject instance : instances) {					
				BasicDBObject query = new BasicDBObject(LinksetMongoDBObject.SUBJECTS_DISTRIBUTION_TARGET, instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString());
				DBCursor d = collection2.find(query);
				
				if(d.hasNext()){
					if(!distributions.contains(instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString())){
					distributions.add(instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString());
//					System.out.println(instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString());
					}
				}
			}
			

			for (DBObject instance : instances) {
				
				BasicDBObject query = new BasicDBObject(LinksetMongoDBObject.OBJECTS_DISTRIBUTION_TARGET, instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString());
				DBCursor d = collection2.find(query);
				
				if(d.hasNext()){
					if(!distributions.contains(instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString())){
						distributions.add(instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString());
					}
				}
			}
			
			for (DBObject instance : instances) {
				if(distributions.contains(instance.get(DistributionMongoDBObject.DOWNLOAD_URL)))
					list.add(new DistributionMongoDBObject(instance.get(DistributionMongoDBObject.URI).toString()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	// return all distributions
	public static ArrayList<DistributionMongoDBObject> getDistributionsWithLinksFilterByDataset(String dataset) {

		ArrayList<DistributionMongoDBObject> list = new ArrayList<DistributionMongoDBObject>();
		ArrayList<String> distributions = new ArrayList<String>();
		
		try {
			DBCollection collection = DataIDDB.getInstance().getCollection(
					DistributionMongoDBObject.COLLECTION_NAME);
			DBCollection collection2 = DataIDDB.getInstance().getCollection(
					LinksetMongoDBObject.COLLECTION_NAME);
			DBCursor instances = collection.find(new BasicDBObject(DistributionMongoDBObject.TOP_DATASET, dataset));

			for (DBObject instance : instances) {					
				BasicDBObject query = new BasicDBObject(LinksetMongoDBObject.SUBJECTS_DISTRIBUTION_TARGET, instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString());
				DBCursor d = collection2.find(query);
				
				if(d.hasNext()){
					if(!distributions.contains(instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString())){
					distributions.add(instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString());
//					System.out.println(instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString());
					}
				}
			}
			

			for (DBObject instance : instances) {
				
				BasicDBObject query = new BasicDBObject(LinksetMongoDBObject.OBJECTS_DISTRIBUTION_TARGET, instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString());
				DBCursor d = collection2.find(query);
				
				if(d.hasNext()){
					if(!distributions.contains(instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString())){
						distributions.add(instance.get(DistributionMongoDBObject.DOWNLOAD_URL).toString());
					}
				}
			}
			
			for (DBObject instance : instances) {
				if(distributions.contains(instance.get(DistributionMongoDBObject.DOWNLOAD_URL)))
					list.add(new DistributionMongoDBObject(instance.get(DistributionMongoDBObject.URI).toString()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}

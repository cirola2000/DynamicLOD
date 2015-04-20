package dataid.mongodb.objects;

import java.util.ArrayList;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import dataid.exceptions.DataIDException;
import dataid.mongodb.DataIDDB;

public class EvaluationMongoDBObject  extends DataIDDB {
	
	// Collection name
		public static final String COLLECTION_NAME = "Evaluation";

		public static final String DS_OBJECT = "dsObject";

		public static final String DS_SUBJECT = "dsSubject";

		public static final String DS_OBJECT_TRIPLES = "dsObjectTriples";

		public static final String DS_SUBJECT_TRIPLES = "dsSubjectTriples";

		public static final String TIME_CREATE_TREE = "timeCreateTree";
		
		public static final String TIME_SEARCH_TREE = "timeSearchTree";
		
		public static final String TIME_CREATE_HASH = "timeCreateHash";
		
		public static final String TIME_SEARCH_HASH= "timeSearchHash";
		
		public static final String TIME_CREATE_BLOOM = "timeCreateBloom";
		
		public static final String TIME_SEARCH_BLOOM = "timeSearchBloom";
		
		public static final String TRUE_POSITIVES = "truePositives";
		
		public static final String POSITIVES_HASH = "positivesHash";
		
		public static final String POSITIVES_BLOOM = "positivesBloom";
		
		

		// class properties

		private String dsObject;

		private String dsSubject;

		private String dsObjectTriples;

		private String dsSubjectTriples;

		private String timeCreateTree;

		private String timeSearchTree;

		private String timeCreateHash;

		private String timeSearchHash;

		private String timeCreateBloom;
		
		private String timeSearchBloom;
		
		private String truePositives;

		private String positivesHash;

		private String positivesBloom;

		public EvaluationMongoDBObject(String uri) {
			super(COLLECTION_NAME, uri);
			loadObject();
		}

		public boolean updateObject(boolean checkBeforeInsert) {
			try {
				mongoDBObject.put(DS_OBJECT, dsObject);
				
				mongoDBObject.put(DS_SUBJECT, dsSubject);
		
				mongoDBObject.put(DS_OBJECT_TRIPLES, dsObjectTriples);

				mongoDBObject.put(DS_SUBJECT_TRIPLES, dsSubjectTriples);

				mongoDBObject.put(TIME_CREATE_TREE, timeCreateTree);
				
				mongoDBObject.put(TIME_CREATE_BLOOM, timeCreateBloom);
				
				mongoDBObject.put(TIME_CREATE_HASH, timeCreateHash);
				
				mongoDBObject.put(TIME_SEARCH_TREE, timeSearchTree);
				
				mongoDBObject.put(TIME_SEARCH_HASH, timeSearchHash);
				
				mongoDBObject.put(TIME_SEARCH_BLOOM, timeSearchBloom);
				
				mongoDBObject.put(TRUE_POSITIVES, truePositives);
				
				mongoDBObject.put(POSITIVES_HASH, positivesHash);
				
				mongoDBObject.put(POSITIVES_BLOOM, positivesBloom);
				
				
				insert(checkBeforeInsert);
				return true;
			} catch (Exception e2) {
//				e2.printStackTrace();

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

				dsObject = (String) obj.get(DS_OBJECT);

				dsSubject = (String) obj.get(DS_SUBJECT);
				
				dsObjectTriples = (String) obj.get(DS_OBJECT_TRIPLES);
				
				dsSubjectTriples = (String) obj.get(DS_SUBJECT_TRIPLES);
				
				timeCreateTree = (String) obj.get(TIME_CREATE_TREE);
				
				timeCreateBloom = (String) obj.get(TIME_CREATE_BLOOM);
				
				timeCreateHash = (String) obj.get(TIME_CREATE_HASH);
				
				timeSearchTree = (String) obj.get(TIME_SEARCH_TREE);
				
				timeSearchHash = (String) obj.get(TIME_SEARCH_HASH);
				
				timeSearchBloom = (String) obj.get(TIME_SEARCH_BLOOM);
				
				truePositives = (String) obj.get(TRUE_POSITIVES);
				
				positivesHash = (String) obj.get(POSITIVES_HASH);
				
				positivesBloom = (String) obj.get(POSITIVES_BLOOM);
				

//				System.out.println(obj);
				return true;
			}
			return false;
		}

		public String getDsObject() {
			return dsObject;
		}

		public void setDsObject(String dsObject) {
			this.dsObject = dsObject;
		}

		public String getDsSubject() {
			return dsSubject;
		}

		public void setDsSubject(String dsSubject) {
			this.dsSubject = dsSubject;
		}

		public String getDsObjectTriples() {
			return dsObjectTriples;
		}

		public void setDsObjectTriples(String dsObjectTriples) {
			this.dsObjectTriples = dsObjectTriples;
		}

		public String getDsSubjectTriples() {
			return dsSubjectTriples;
		}

		public void setDsSubjectTriples(String dsSubjectTriples) {
			this.dsSubjectTriples = dsSubjectTriples;
		}

		public String getTimeCreateTree() {
			return timeCreateTree;
		}

		public void setTimeCreateTree(String timeCreateTree) {
			this.timeCreateTree = timeCreateTree;
		}

		public String getTimeSearchTree() {
			return timeSearchTree;
		}

		public void setTimeSearchTree(String timeSearchTree) {
			this.timeSearchTree = timeSearchTree;
		}

		public String getTimeCreateHash() {
			return timeCreateHash;
		}

		public void setTimeCreateHash(String timeCreateHash) {
			this.timeCreateHash = timeCreateHash;
		}

		public String getTimeSearchHash() {
			return timeSearchHash;
		}

		public void setTimeSearchHash(String timeSearchHash) {
			this.timeSearchHash = timeSearchHash;
		}

		public String getTimeCreateBloom() {
			return timeCreateBloom;
		}

		public void setTimeCreateBloom(String timeCreateBloom) {
			this.timeCreateBloom = timeCreateBloom;
		}

		public String getTimeSearchBloom() {
			return timeSearchBloom;
		}

		public void setTimeSearchBloom(String timeSearchBloom) {
			this.timeSearchBloom = timeSearchBloom;
		}

		public String getTruePositives() {
			return truePositives;
		}

		public void setTruePositives(String truePositives) {
			this.truePositives = truePositives;
		}

		public String getPositivesHash() {
			return positivesHash;
		}

		public void setPositivesHash(String positivesHash) {
			this.positivesHash = positivesHash;
		}

		public String getPositivesBloom() {
			return positivesBloom;
		}

		public void setPositivesBloom(String positivesBloom) {
			this.positivesBloom = positivesBloom;
		}

}

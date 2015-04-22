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
		
		public static final String FILTER_SIZE = "filterSize";
		
		public static final String HASH_SIZE = "hashSize";
		
		public static final String TREE_SIZE = "treeSize";
		
		public static final String PRECISION = "precision";
		
		public static final String RECALL = "recall";
		
		public static final String FMEASURE = "fmeasure";
		
		
		

		// class properties

		private String dsObject;

		private String dsSubject;

		private int dsObjectTriples;

		private int dsSubjectTriples;

		private String timeCreateTree;

		private String timeSearchTree;

		private String timeCreateHash;

		private String timeSearchHash;

		private String timeCreateBloom;
		
		private String timeSearchBloom;
		
		private int truePositives;

		private int positivesHash;

		private int positivesBloom;

		private long filterSize;

		private long hashSize;

		private long treeSize;
		
		private double precision;
		
		private double recall;
		
		private double fmeasure;
		
		

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
				
				mongoDBObject.put(FILTER_SIZE, filterSize);
				
				mongoDBObject.put(HASH_SIZE, hashSize);
				
				mongoDBObject.put(TREE_SIZE, treeSize);
				
				mongoDBObject.put(PRECISION, precision);
				
				mongoDBObject.put(RECALL, recall);
				
				mongoDBObject.put(FMEASURE, fmeasure);
				
				
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
				
				dsObjectTriples = ((Number) (obj.get(DS_OBJECT_TRIPLES))).intValue();
				
				dsSubjectTriples =((Number)(obj.get(DS_SUBJECT_TRIPLES))).intValue();
				
				timeCreateTree = (String) obj.get(TIME_CREATE_TREE);
				
				timeCreateBloom = (String) obj.get(TIME_CREATE_BLOOM);
				
				timeCreateHash = (String) obj.get(TIME_CREATE_HASH);
				
				timeSearchTree = (String) obj.get(TIME_SEARCH_TREE);
				
				timeSearchHash = (String) obj.get(TIME_SEARCH_HASH);
				
				timeSearchBloom = (String) obj.get(TIME_SEARCH_BLOOM);
				
				truePositives = ((Number)(obj.get(TRUE_POSITIVES))).intValue();
				
				positivesHash = ((Number)(obj.get(POSITIVES_HASH))).intValue();
				
				positivesBloom = ((Number)(obj.get(POSITIVES_BLOOM))).intValue();
				
				filterSize = ((Number)(obj.get(FILTER_SIZE))).longValue();
				
				hashSize = ((Number)(obj.get(HASH_SIZE))).longValue();
				
				treeSize =((Number)(obj.get(TREE_SIZE))).longValue();

				precision = ((Number)(obj.get(PRECISION))).doubleValue();
				
				recall = ((Number)(obj.get(RECALL))).doubleValue();
				
				fmeasure = ((Number)(obj.get(FMEASURE))).doubleValue();
				
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

		public int getDsObjectTriples() {
			return dsObjectTriples;
		}

		public void setDsObjectTriples(int dsObjectTriples) {
			this.dsObjectTriples = dsObjectTriples;
		}

		public int getDsSubjectTriples() {
			return dsSubjectTriples;
		}

		public void setDsSubjectTriples(int dsSubjectTriples) {
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

		public int getTruePositives() {
			return truePositives;
		}

		public void setTruePositives(int truePositives) {
			this.truePositives = truePositives;
		}

		public int getPositivesHash() {
			return positivesHash;
		}

		public void setPositivesHash(int positivesHash) {
			this.positivesHash = positivesHash;
		}

		public int getPositivesBloom() {
			return positivesBloom;
		}

		public void setPositivesBloom(int positivesBloom) {
			this.positivesBloom = positivesBloom;
		}

		public long getFilterSize() {
			return filterSize;
		}

		public void setFilterSize(long filterSize) {
			this.filterSize = filterSize;
		}

		public long getHashSize() {
			return hashSize;
		}

		public void setHashSize(long hashSize) {
			this.hashSize = hashSize;
		}

		public long getTreeSize() {
			return treeSize;
		}

		public void setTreeSize(long treeSize) {
			this.treeSize = treeSize;
		}

		public double getPrecision() {
			return precision;
		}

		public void setPrecision(double precision) {
			this.precision = precision;
		}

		public double getRecall() {
			return recall;
		}

		public void setRecall(double recall) {
			this.recall = recall;
		}

		public double getFmeasure() {
			return fmeasure;
		}

		public void setFmeasure(double fmeasure) {
			this.fmeasure = fmeasure;
		}
		
		

}

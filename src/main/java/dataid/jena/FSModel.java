package dataid.jena;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import dataid.DataID;
import dataid.DataIDGeneralProperties;
import dataid.filters.FileToFilter;
import dataid.filters.GoogleBloomFilter;
import dataid.literal.SubsetModel;
import dataid.ontology.Dataset;
import dataid.ontology.FileSystem;
import dataid.ontology.Linkset;

public class FSModel {

//	 jena model to store data on file system
	private static Model fsModel = ModelFactory.createDefaultModel();		

	public boolean addDatasetOnFileSystem(String subject, String path, String accessURL,
			double  contentLenght, String subjectFilter, String objectFile, String subsetURI, String datasetURI) {

		try {
			fsModel = ModelFactory.createDefaultModel();
			try {
				File f = new File(DataIDGeneralProperties.FS_MODEL);
				if (f.exists())
					fsModel.read(DataIDGeneralProperties.FS_MODEL,"N-TRIPLES");
			} catch (Exception e) {
				e.printStackTrace();
			}

			Resource r = fsModel.createResource(subject);
			

			r.addProperty(FileSystem.accessURL, accessURL);
			r.addProperty(FileSystem.byteSize, String.valueOf(contentLenght));
			r.addProperty(FileSystem.dataIDFilePath, path);
			r.addProperty(FileSystem.objectPath, objectFile);
			r.addProperty(FileSystem.subjectFilterPath, subjectFilter);
			r.addProperty(FileSystem.dataIDUpdatedFilePath, path+ ".updated"); 
			r.addProperty(FileSystem.subsetURI, subsetURI);
			r.addProperty(FileSystem.datasetURI, datasetURI);
			

			fsModel.write(new FileOutputStream(new File(
					DataIDGeneralProperties.FS_MODEL)), "N-TRIPLES");

			fsModel.close();
			
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void updateDataID(String dataID, String dataIDUpdated,
			int links,String subsetURL, String linkURL, String datasetURI ){
		
		Model m =  ModelFactory.createDefaultModel();
		
		m.read(DataIDGeneralProperties.DATAID_GRAPH_MODEL_PATH, "TURTLE");
		
		
			System.out.println("Links: ");
			System.out.println("dataid file: "+dataID);
			System.out.println("dataid updated file: "+dataIDUpdated);
			System.out.println("links: "+links);
			System.out.println("link url: "+linkURL);
			System.out.println("subset url: "+subsetURL);	
			System.out.println("dataset url: "+subsetURL);	

			
			Resource subset = m.getResource(subsetURL);
			Resource dataset = m.getResource(datasetURI);
			Random randomGenerator = new Random();

			String linksetSubject = subsetURL 
					+ randomGenerator.nextInt();
			
			// creating a linkset
			Resource linkSet = m.createResource(linksetSubject);
			linkSet.addProperty(Linkset.issued, "05-12-1988");
			linkSet.addProperty(Linkset.modified, "05-12-1988");
			linkSet.addProperty(Dataset.dataIDType, Linkset.voidLinkset);
			linkSet.addLiteral(Linkset.triples, links);
			linkSet.addProperty(Linkset.subjectsTarget, dataset);
			linkSet.addProperty(Linkset.objectsTarget, m.getResource(linkURL));
			
			linkSet.addLiteral(Linkset.linkPredicate, "");

			subset.addProperty(Linkset.voidSubset, linkSet);
			
			
			try {
				m.write(new FileOutputStream(DataIDGeneralProperties.DATAID_GRAPH_MODEL_PATH),"TURTLE");
				m.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}	
	
	public void compareAllDistributions(){
		
		try {
			System.out.println("Opening base model");
			fsModel = ModelFactory.createDefaultModel();
			try {
				File f = new File(DataIDGeneralProperties.FS_MODEL);
				if (f.exists())
					fsModel.read(DataIDGeneralProperties.FS_MODEL,"N-TRIPLES");
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			System.out.println("searching distributions to compare");
			// compare each distribution with filters			
			StmtIterator objectIterator = fsModel.listStatements(null, FileSystem.objectPath, (RDFNode) null);
			
			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO,"");
			
			while (objectIterator.hasNext()){
				FileToFilter f = new FileToFilter();
				
				System.out.println("Distributions found!");
				Statement stmtObject = objectIterator.next();
				
				// get filter path
				StmtIterator filterPathIterator = fsModel.listStatements(null, FileSystem.subjectFilterPath, (RDFNode) null);
				
				while(filterPathIterator.hasNext()){
					Statement filterPath = filterPathIterator.next();
					
					// do not compare distribution with itself
					if(stmtObject.getSubject().toString().equals(filterPath.getSubject().toString())){
						System.out.println("No needs to compare the distribution with itself.");
						System.out.println("Distribution 1: "+stmtObject.getSubject().toString());
						System.out.println("Distribution 2: "+filterPath.getSubject().toString());
						
					}
					else{
					
					System.out.println("Comparing: "+
					stmtObject.getObject().toString());
					System.out.println(
					"with filter "+
							filterPath.getObject().toString());
					GoogleBloomFilter g = new GoogleBloomFilter();
				
				
					System.out.println("Opening filter: "+filterPath.getObject().toString());
					try{
						g.loadFilter(filterPath.getObject().toString());
					}
					catch(Exception e){
						e.printStackTrace();
					}
					
					System.out.println("Searching file  "+stmtObject.getObject().toString());
				
					int numbersOfTriples = f.searchFileOnFilter(g,stmtObject.getObject().toString());
				
					DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_INFO, 
							"Number of links comparing "+stmtObject.getSubject().toString()+
							" with "+filterPath.getSubject().toString()+": "+ 
							numbersOfTriples);
					
					// case there are links, update dataID file
					if(numbersOfTriples>0){
						
						String dataIDPath="";
						String dataIDUpdatedPath="";
						String subsetURL="";
						String datasetURL="";
						String linkURL="";
						
						
						// get dataID path
						StmtIterator s = fsModel.listStatements(stmtObject.getSubject(), FileSystem.dataIDFilePath, (RDFNode) null);
						if(s.hasNext())
							dataIDPath = s.next().getObject().toString();

						// get dataIDUpdated path
						s = fsModel.listStatements(stmtObject.getSubject(), FileSystem.dataIDUpdatedFilePath, (RDFNode) null);
						if(s.hasNext())
							dataIDUpdatedPath = s.next().getObject().toString();
						
						// get dataIDUpdated path
						s = fsModel.listStatements(stmtObject.getSubject(), FileSystem.subsetURI, (RDFNode) null);
						if(s.hasNext())
							subsetURL = s.next().getObject().toString();
						
						// get target dataset link
						s = fsModel.listStatements(filterPath.getSubject(), FileSystem.datasetURI, (RDFNode) null);
						if(s.hasNext())
							linkURL = s.next().getObject().toString();
						
						// get dataseturl
						s = fsModel.listStatements(stmtObject.getSubject(), FileSystem.datasetURI, (RDFNode) null);
						if(s.hasNext())
							datasetURL = s.next().getObject().toString();
						
						updateDataID(dataIDPath, dataIDUpdatedPath, numbersOfTriples, subsetURL, linkURL, datasetURL );

						}
					}
				}
			}

			fsModel.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

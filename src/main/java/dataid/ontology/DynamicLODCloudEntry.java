package dataid.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class DynamicLODCloudEntry {
	
	// URI of the distribution
	public static final Property accessURL = ResourceFactory
			.createProperty("http://www.w3.org/ns/dcat#accessURL");
	
	// URI of the distribution's subset
	public static final Property subsetURI = ResourceFactory
			.createProperty("http://rdfs.org/ns/void#subset");
	// URI of the distribution's dataset
	public static final Property datasetURI = ResourceFactory
			.createProperty("http://rdfs.org/ns/void#Dataset");
	
	public static final Property byteSize = ResourceFactory
			.createProperty("http://www.w3.org/ns/dcat#byteSize");
	
	// filter's path
	public static final Property subjectFilterPath = ResourceFactory
			.createProperty("http://localhost:8080/ontology#subjectFilter");
	
	// object's path
	public static final Property objectPath = ResourceFactory
			.createProperty("http://localhost:8080/ontology#objectPath");
	
	// dataid file path
	public static final Property dataIDFilePath = ResourceFactory
			.createProperty("http://localhost:8080/ontology#dataIDFilePath");
	
	
	public static final Property timeToCreateFilter = ResourceFactory
			.createProperty("http://localhost:8080/ontology#timeToCreateFilter");
	
	
	
	

}

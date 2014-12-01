package dataid.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class FileSystem {
	public static final Property accessURL = ResourceFactory
			.createProperty("http://www.w3.org/ns/dcat#accessURL");
	public static final Property mediaType = ResourceFactory
			.createProperty("http://www.w3.org/ns/dcat#mediaType");
	public static final Property subjectFilterPath = ResourceFactory
			.createProperty("http://path.org#subjectFilter");
	public static final Property objectPath = ResourceFactory
			.createProperty("http://path.org#objectPath");
	public static final Property dataIDFilePath = ResourceFactory
			.createProperty("http://path.org#dataIDFilePath");
	public static final Property dataIDUpdatedFilePath = ResourceFactory
			.createProperty("http://path.org#dataIDUpdatedFilePath");
	public static final Property subsetURI = ResourceFactory
			.createProperty("http://rdfs.org/ns/void#Dataset");

}

package dataid.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class Void {

	public static final Resource voidDataset = resource(NS.VOID_URI, "Dataset");
	public static final Property dataIDType = RDF.type;
	
	public static final Property title = property(NS.DCT_URI, "title");
	public static final Property dataDump = property(NS.VOID_URI, "dataDump");
	
	
	
	protected static final Resource resource(String ns, String local) {
		return ResourceFactory.createResource(ns + local);
	}

	protected static final Property property(String ns, String local) {
		return ResourceFactory.createProperty(ns, local);
	}

}

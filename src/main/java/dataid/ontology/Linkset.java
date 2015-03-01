package dataid.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class Linkset {

	public static final Property issued = property(NS.DCT_URI, "issued");
	public static final Property modified = property(NS.DCT_URI, "modified");
	public static final Property exampleResource = property(NS.VOID_URI, "exampleResource");
	public static final Property linkPredicate = property(NS.VOID_URI, "linkPredicate");
	public static final Property triples = property(NS.VOID_URI, "triples");
	public static final Property target = property(NS.VOID_URI, "target");
	public static final Property subjectsTarget = property(NS.VOID_URI, "subjectsTarget");
	public static final Property objectsTarget = property(NS.VOID_URI, "objectsTarget");
	public static final Property voidSubset = property(NS.VOID_URI, "subset");
	public static final Property voidDataset = property(NS.VOID_URI, "Dataset");
	public static final Property voidLinkset = property(NS.VOID_URI, "Linkset");
	

	public static final Property dataidContainsLinks = property(NS.DATAID_URI,
			"containsLinks");
	
	

	protected static final Property property(String ns, String local) {
		return ResourceFactory.createProperty(ns, local);
	}

	protected static final Resource resource(String ns, String local) {
		return ResourceFactory.createResource(ns + local);
	}

}

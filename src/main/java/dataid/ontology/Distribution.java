package dataid.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import dataid.ontology.vocabulary.NS;

public class Distribution {

	public static final Property title = property(NS.DCT_URI, "title");
	public static final Property accessURL = property(NS.DCAT_URI, "accessURL");
	
	public static final Property dataIDDistribution = property(NS.DATAID_URI, "Distribution");
	public static final Property dcatDistribution = property(NS.DCAT_URI, "distribution");
	

	protected static final Property property(String ns, String local) {
		return ResourceFactory.createProperty(ns, local);
	}
	
	protected static final Resource resource(String ns, String local) {
		return ResourceFactory.createResource(ns + local);
	}
	
}

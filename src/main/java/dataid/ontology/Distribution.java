package dataid.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class Distribution {

	public static final Property title = property(NS.DCT_URI, "title");
	public static final Property downloadURL = property(NS.DCAT_URI, "downloadURL");
	public static final Property format = property(NS.DCT_URI, "format");
	
	public static final Property dataIDDistribution = property(NS.DATAID_URI, "Distribution");
	public static final Property dcatDistribution = property(NS.DCAT_URI, "distribution");
	

	protected static final Property property(String ns, String local) {
		return ResourceFactory.createProperty(ns, local);
	}
	
	protected static final Resource resource(String ns, String local) {
		return ResourceFactory.createResource(ns + local);
	}
	
}

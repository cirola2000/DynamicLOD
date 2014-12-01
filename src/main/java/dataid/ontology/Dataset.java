package dataid.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

import dataid.ontology.vocabulary.NS;

public class Dataset {

	public static final Resource dataIDDataset = resource(NS.DATAID_URI, "Dataset");
	public static final Property dataIDType = RDF.type;
	
	public static final Property title = property(NS.DCT_URI, "title");
	public static final Property label = property(NS.RDFS_URI, "label");

	public static final Property distribution= property(NS.DCAT_URI, "distribution");	
	public static final Property subset= property(NS.VOID_URI, "subset");	 	
	
	
	protected static final Resource resource(String ns, String local) {
		return ResourceFactory.createResource(ns + local);
	}

	protected static final Property property(String ns, String local) {
		return ResourceFactory.createProperty(ns, local);
	}

}
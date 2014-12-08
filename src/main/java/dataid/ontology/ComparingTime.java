package dataid.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class ComparingTime {

	public static final Property ObjectURI = ResourceFactory
			.createProperty("http://localhost:8080/ontology#objectURI");
	
	public static final Property FilterURI = ResourceFactory
			.createProperty("http://localhost:8080/ontology#filterURI");
	
	public static final Property Time = ResourceFactory
			.createProperty("http://localhost:8080/ontology#timeComparing");
	
	
	
}

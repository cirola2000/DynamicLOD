package dataid.utils;

import java.util.ArrayList;

public class Formats {
	
	public static final String DEFAULT_TURTLE = "ttl";
	
	public static final String DEFAULT_NTRIPLES = "nt";
	
	public static final String DEFAULT_NQUADS = "nq";
	
	public static final String DEFAULT_RDFXML = "rdf";

	private static final ArrayList<String> TURTLE_FORMATS = new ArrayList<String>(){{
	    add("ttl");
	}};
	
	private static final ArrayList<String> NTRIPLES_FORMATS = new ArrayList<String>(){{
	    add("nt");
	    add("application/x-ntriples");
	    add("ntriples");
	}};
	private static final ArrayList<String> RDFXML_FORMATS = new ArrayList<String>(){{
	    add("application/rdf+xml");
	    add("rdf");
	    add("rdfxml");
	}};
	private static final ArrayList<String> NQUADS_FORMATS = new ArrayList<String>(){{
	    add("application/x-nquads");
	    add("nq");
	}};
	
	public static String getEquivalentFormat(String str){
		if(TURTLE_FORMATS.contains(str)) return DEFAULT_TURTLE;
		else if (NTRIPLES_FORMATS.contains(str)) return DEFAULT_NTRIPLES;
		else if (RDFXML_FORMATS.contains(str)) return DEFAULT_RDFXML;
		else if (NQUADS_FORMATS.contains(str)) return DEFAULT_NQUADS;
		else return "";
	}

}

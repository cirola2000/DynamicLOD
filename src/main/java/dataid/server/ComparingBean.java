package dataid.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import dataid.DataIDGeneralProperties;
import dataid.ontology.ComparingTime;
import dataid.ontology.Distribution;

@ViewScoped
@ManagedBean
public class ComparingBean implements Serializable {

	private static final long serialVersionUID = -6239437588285327644L;

	private String url = "http://localhost:8080/dataids_example/dataid-kore50.ttl";

	private List<Comparing> comparing = new ArrayList<Comparing>();

	public ComparingBean() {
		
		Model m = ModelFactory.createDefaultModel();
//		m.read(DataIDGeneralProperties.FS_MODEL,"N-TRIPLES");
		StmtIterator i = m.listStatements(null, ComparingTime.ObjectURI, (RDFNode) null);
		
		while (i.hasNext()){
			Statement comparing= i.next();
			
			String object = comparing.getObject().toString();
			Resource r = m.getResource(comparing.getSubject().toString());
			String filter = r.getProperty(ComparingTime.FilterURI).getObject().toString();
			String time = r.getProperty(ComparingTime.Time).getObject().toString();
			
//			String objectTriples = m.getResource(object).getProperty(DynamicLODCloudEntry.numberOfObjectTriples).getObject().toString();
//			String filterTriples = m.getResource(filter).getProperty(DynamicLODCloudEntry.numberOfTriplesLoadedIntoFilter).getObject().toString();
			
//			this.comparing.add(new Comparing(object+" (triples: "+objectTriples+")", filter+" (triples: "+filterTriples+")", time));
			
		}
		
	}

	public String register() {
		return "registrationInfo";
	}

	public List<Comparing> getComparing() {
		Collections.sort(comparing, new Comparator<Comparing>() {
	        
	        public int compare(Comparing  comparing1, Comparing  comparing2)
	        {

	            return  comparing1.object.compareTo(comparing2.object);
	        }
	    });
		return comparing;
	}

	public void setComparing(List<Comparing> comparing) {
		this.comparing = comparing;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
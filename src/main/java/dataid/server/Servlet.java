package dataid.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import dataid.DataID;
import dataid.ontology.Distribution;
 
public class Servlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html"); 
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("List of distributions: "+request.getParameter("dataidAddress")+"<br><br>");

        
//        DataID dataid = new DataID(request.getParameter("dataidAddress")); 
       
        
//        try{
//        String uri = request.getParameter("dataidAddress");
//		final Model model = ModelFactory.createDefaultModel();
//		model.read(uri,null,"TTL");
//		
//		ResIterator iter = model.listSubjectsWithProperty(Distribution.accessURL);	
//		
//		while(iter.hasNext()){
//			Resource r = iter.nextResource();
//			response.getWriter().println("<br><a href=\""+r.getURI()+" \">"+r.getURI()+ "</a>");
//			
//		}
//		
//		RDFDataMgr.write(System.out,model, RDFFormat.TURTLE);
//        }
//        catch (Exception e){
//        	response.getWriter().println("<br>Sorry, we got an exception!<br><br>");
//        	response.getWriter().println(e.getMessage());
//        	
//        }
        
        
    }
}

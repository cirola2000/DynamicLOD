package dataid.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import dataid.Manager;
import dataid.download.DownloadAndSaveDistribution;
import dataid.models.DistributionModel;

public class DBPediaLinks { 
	
	static public ArrayList<String> links = new ArrayList<String>() {
		{

			add("article_categories_en.nt.bz2");
			add("article_templates_en.nt.bz2"); 
			add("category_labels_en.nt.bz2");
			add("disambiguations_en.nt.bz2");
			add("disambiguations_unredirected_en.nt.bz2");
			add("external_links_en.nt.bz2");
			add("flickrwrappr_links_en.nt.bz2");
			add("freebase_links_en.nt.bz2");
			add("geo_coordinates_en.nt.bz2");
			add("geonames_links_en.nt.bz2");
			add("homepages_en.nt.bz2");
			add("images_en.nt.bz2");
			add("instance_types_en.nt.bz2");
			add("instance_types_heuristic_en.nt.bz2");
			add("interlanguage_links_chapters_en.nt.bz2");
			add("interlanguage_links_en.nt.bz2");
			add("iri_same_as_uri_en.nt.bz2"); 
			add("labels_en.nt.bz2"); 
			add("long_abstracts_en.nt.bz2");
			add("mappingbased_properties_cleaned_en.nt.bz2");
			add("mappingbased_properties_en.nt.bz2");
			add("mappingbased_properties_unredirected_en.nt.bz2");
			add("old_interlanguage_links_en.nt.bz2");
			add("old_interlanguage_links_same_as_chapters_en.nt.bz2");
			add("old_interlanguage_links_same_as_en.nt.bz2");
			add("old_interlanguage_links_see_also_chapters_en.nt.bz2");
			add("old_interlanguage_links_see_also_en.nt.bz2");
			add("page_ids_en.nt.bz2");  
			add("page_in_link_counts_en.nt.bz2");
			add("page_links_en.nt.bz2"); 
			add("page_links_unredirected_en.nt.bz2");  
			add("page_out_link_counts_en.nt.bz2");
			add("persondata_en.nt.bz2");
			add("persondata_unredirected_en.nt.bz2");
			add("pnd_en.nt.bz2");
			add("raw_infobox_properties_en.nt.bz2");
			add("raw_infobox_properties_unredirected_en.nt.bz2");
			add("raw_infobox_property_definitions_en.nt.bz2");
			add("redirects_en.nt.bz2");
			add("redirects_transitive_en.nt.bz2");
			add("revision_ids_en.nt.bz2"); 
			add("revision_uris_en.nt.bz2"); 
			add("short_abstracts_en.nt.bz2");
			add("skos_categories_en.nt.bz2");
			add("specific_mappingbased_properties_en.nt.bz2");
			add("topical_concepts_en.nt.bz2");
			add("topical_concepts_unredirected_en.nt.bz2");
			add("wikipedia_links_en.nt.bz2"); 
		}
	};

	@Test
	public void createDistribitons(){
		
		List<DistributionModel> distributionsLinks = new ArrayList<DistributionModel>();
		
		try {
	
		DownloadAndSaveDistribution dist1 = null;
		
		for (String link : links) {
			
			String l = "http://downloads.dbpedia.org/3.9/en/"+link;
			
			DistributionModel d = new DistributionModel(0, "http://dbpedia.org/", "http://downloads.dbpedia.org/3.9/en/", 
					l, l);
			distributionsLinks.add(d);
			
		}
		
		new Manager(distributionsLinks);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

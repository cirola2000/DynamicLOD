package dataid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

public class Teste {
	private ConcurrentHashMap<String, Integer> sharedHashMap = new ConcurrentHashMap<String, Integer>();
	
	public ConcurrentLinkedQueue<String> authorityDomains = new  ConcurrentLinkedQueue<String>();
	
	
	
	
	@Test
	public void Teste() {
		try {

			FileReader f = new FileReader(
					new File(
							"/home/ciro/dataid/objects/object_distribution_external_links_en.ttl"));
			final byte[] buffer = new byte[4096];

			int count = 0;
			BufferedReader br = new BufferedReader(f);
			int n = 0;
			String str;
			while ((str = br.readLine()) != null) {


				String obj = str;

				obj = obj.substring(1, obj.length() - 1);
				String[] ar = obj.split("/");
				if (ar.length > 3)
					obj = ar[0] + "//" + ar[2] + "/" + ar[3] + "/";
				else if (ar.length > 2)
					obj = ar[0] + "//" + ar[2] + "/";
				else {
					System.out.println(obj);
					obj = "";
				}
				
				String authority = obj;
				
				if (!authority.equals("")) {
					
					
					
					
					
					if (!sharedHashMap.containsKey(authority)) {
						sharedHashMap.put(authority, 1);
					} else {
						if (sharedHashMap.get(authority) == 49) {
							if (!authorityDomains.contains(authority)) {
								authorityDomains.add(authority);
								count++;
							}
						} else {
							sharedHashMap.replace(authority,
									sharedHashMap.get(authority) + 1);
						}
//					}
				}
			}
				
				
				
				
				

			}
			System.out.println(count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

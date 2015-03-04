package dataid.threads;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class AddAuthorityObjectThread extends Thread{
	
	private boolean doneSplittingString;
	
	private Queue<String> objectQueue = null;
	
	public List<String> authorityDomains = null;
	
	
	public AddAuthorityObjectThread(Queue<String> objectQueue, List<String> authorityDomains) {
		this.objectQueue = objectQueue;
		this.authorityDomains = authorityDomains;
		
	}
	
	public boolean isDoneSplittingString() {
		return doneSplittingString;
	}

	public void setDoneSplittingString(boolean doneSplittingString) {
		this.doneSplittingString = doneSplittingString;
	}
	
	public synchronized void run() {
		int aux = 0;
		int aux2 = 0;
		int aux3=0;
		String obj = "";
		while (!doneSplittingString) {
			while (objectQueue.size() > 0) {
				try {
				aux++;
				if(aux%10000==0){
					System.out.println("c1 "+objectQueue.size());
					aux=1;
					System.out.println(authorityDomains.size());
				}
				obj = objectQueue.remove();
				String authority = "";

				URL url;
					obj = obj.substring(1, obj.length() - 1);
					url = new URL(obj);
					authority = url.getProtocol() + "://" + url.getHost();
				if (!authority.equals(""))
					if (!authorityDomains.contains(authority)) {
						authorityDomains.add(authority);
//						System.out.println(authority);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			aux2++;
			if(aux2%480000000==0){
				System.out.println("c2 "+aux3);
				aux2=1;
				aux3++;
			}
		}
		System.out.println("acabo?");
		System.out.println(objectQueue.size());
	}
	
}

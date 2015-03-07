package dataid.threads;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class AddAuthorityObjectThread extends Thread {

	private boolean doneSplittingString;

	private Queue<String> objectQueue = null;

	public Queue<String> authorityDomains = null;

	private ConcurrentHashMap<String, Integer> sharedHashMap = null;

	public AddAuthorityObjectThread(Queue<String> objectQueue,
			Queue<String> authorityDomains,
			ConcurrentHashMap<String, Integer> sharedHashMap) {
		this.objectQueue = objectQueue;
		this.authorityDomains = authorityDomains;
		this.sharedHashMap = sharedHashMap;

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
		int aux3 = 0;
		String obj = "";
		while (!doneSplittingString) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (objectQueue.size() > 0) {
				try {
					obj = objectQueue.remove();
					String authority = "";

					URL url;
					obj = obj.substring(1, obj.length() - 1);
					String[] ar = obj.split("/");
					if(ar.length>3)
						obj = ar[0] + "//" + ar[2]+"/"+ar[3]+"/";
					else if(ar.length>2)
						obj = ar[0] + "//" + ar[2]+"/";
					else{
						System.out.println(obj);
						obj="";
					}
					
					if (obj.length() < 100) {
//						url = new URL(obj);
//						authority = url.getProtocol() + "://" + url.getHost();

						authority = obj;
						if (!authority.equals("")) {
							if (!sharedHashMap.containsKey(authority)) {
								sharedHashMap.put(authority, 1);
							} else {
								if (sharedHashMap.get(authority) == 51) {
									if (!authorityDomains.contains(authority)) {
										authorityDomains.add(authority);
									}
								} else {
									sharedHashMap.put(authority,
											sharedHashMap.get(authority) + 1);
								}
							}
						}
					}

					// if (!authority.equals(""))
					// if (!authorityDomains.contains(authority)) {
					// authorityDomains.add(authority);
					// }
				} catch (NoSuchElementException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();

				} 
//				catch (MalformedURLException e) {
//				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// aux2++;
			// if(aux2%480000000==0){
			// System.out.println("c2 "+aux3);
			// aux2=1;
			// aux3++;
			// }
		}
		System.out.println("Ending AddAuthorityObjectThread.");
	}

}

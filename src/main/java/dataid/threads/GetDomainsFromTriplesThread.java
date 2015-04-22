package dataid.threads;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import dataid.Manager;

public class GetDomainsFromTriplesThread extends Thread {
	
	final static Logger logger = Logger.getLogger(GetDomainsFromTriplesThread.class);

	private boolean doneSplittingString;

	private ConcurrentLinkedQueue<String> resourceQueue = null;

	private ConcurrentHashMap<String, Integer> countHashMap = null;

	public GetDomainsFromTriplesThread(ConcurrentLinkedQueue<String> resourceQueue,
			ConcurrentHashMap<String, Integer> countHashMap) {
		this.resourceQueue = resourceQueue;
		this.countHashMap = countHashMap;

	}

	public boolean isDoneSplittingString() {
		return doneSplittingString;
	}

	public void setDoneSplittingString(boolean doneSplittingString) {
		this.doneSplittingString = doneSplittingString;
	}

	public synchronized void run() {
		
		logger.debug("Starting GetDomainsFromTriplesThread class.");
		
		String obj = "";
		while (!doneSplittingString) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			while (resourceQueue.size() > 0) {
				try {
					obj = resourceQueue.remove();
					

					obj = obj.substring(1, obj.length() - 1);
					String[] ar = obj.split("/");
					if (ar.length > 3)
						obj = ar[0] + "//" + ar[2] + "/" + ar[3] + "/";
					else if (ar.length > 2)
						obj = ar[0] + "//" + ar[2] + "/";
					else {
						obj = "";
					}
					
					if (!obj.equals("")) {
						countHashMap.putIfAbsent(obj, 1);
						countHashMap.replace(obj,
								countHashMap.get(obj) + 1);

					}

				} catch (NoSuchElementException e) {
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		logger.debug("Ending GetDomainsFromTriplesThread class.");
	}

}

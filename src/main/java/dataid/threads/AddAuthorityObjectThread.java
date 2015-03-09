package dataid.threads;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AddAuthorityObjectThread extends Thread {

	private boolean doneSplittingString;

	private ConcurrentLinkedQueue<String> objectQueue = null;

	private ConcurrentHashMap<String, Integer> sharedHashMap = null;

	public AddAuthorityObjectThread(ConcurrentLinkedQueue<String> objectQueue,
			ConcurrentHashMap<String, Integer> sharedHashMap) {
		this.objectQueue = objectQueue;
		this.sharedHashMap = sharedHashMap;

	}

	public boolean isDoneSplittingString() {
		return doneSplittingString;
	}

	public void setDoneSplittingString(boolean doneSplittingString) {
		this.doneSplittingString = doneSplittingString;
	}

	public synchronized void run() {
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
					
					
					if (!obj.equals("")) {
						sharedHashMap.putIfAbsent(obj, 1);
						sharedHashMap.replace(obj,
								sharedHashMap.get(obj) + 1);

					}

				} catch (NoSuchElementException e) {
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Ending AddAuthorityObjectThread.");
	}

}

package dataid.mongodb.actions;

import dataid.filters.GoogleBloomFilter;
import dataid.threads.DataModelThread;


public class JobThread implements Runnable {
	int size;
	String[] lines;
	DataModelThread dataThread = null;

	public JobThread(DataModelThread dataThread, String[] lines, int size) {

		this.size = size;
		this.lines = lines;
		this.dataThread = dataThread;

	}

	public void run() {
		try {
			for (int i = 0; i < size; i++) {
					
				if (dataThread.filter.compare(lines[i])) {
//					if(dataThread.ontologyFilter.compare(lines[i]))
//						dataThread.ontologyLinks++;
//					else
						dataThread.links++;		
				}
			}

		} catch (Exception e) {
			// DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,e.getMessage());
			e.printStackTrace();
		}
	}
}

package dataid.mongodb.actions;


public class JobThread implements Runnable {
	int size;
	String[] lines;
	DataThread dataThread = null;

	public JobThread(DataThread dataThread, String[] lines, int size) {

		this.size = size;
		this.lines = lines;
		this.dataThread = dataThread;

	}

	public void run() {

		try {

			for (int i = 0; i < size; i++) {
				if (dataThread.filter.compare(lines[i])) {
					// don't save same link multiple times
//					if (!dataThread.links.contains(lines[i]))
						dataThread.links++;
				}
			}

		} catch (Exception e) {
			// DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,e.getMessage());
			e.printStackTrace();

		}
	}
}

package dataid.threads;

import java.io.FileOutputStream;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dataid.DataIDGeneralProperties;
import dataid.server.DataIDBean;

public class SplitAndStoreThread extends Thread {

	private String fileName;

	private boolean doneReadingFile = false;

	Queue<String> bufferQueue = null;

	Queue<String> objectQueue = null;

	Queue<String> subjectQueue = null;

	public Integer subjectLines = 0;

	public Integer objectLines = 0;

	public Integer totalTriples = 0;

	public SplitAndStoreThread(Queue<String> bufferQueue,
			 Queue<String> subjectQueue, Queue<String> objectQueue, String fileName) {
		this.bufferQueue = bufferQueue;
		this.objectQueue = objectQueue;
		this.subjectQueue = subjectQueue;
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setDoneReadingFile(boolean doneReadingFile) {
		this.doneReadingFile = doneReadingFile;
	}

	public boolean isDoneReadingFile() {
		return doneReadingFile;
	}

	public Integer getSubjectLines() {
		return subjectLines;
	}

	public Integer getObjectLines() {
		return objectLines;
	}

	public Integer getTotalTriples() {
		return totalTriples;
	}

	public synchronized void run() {

		try {

			// creates subject file in disk
			FileOutputStream subject = new FileOutputStream(
					DataIDGeneralProperties.SUBJECT_FILE_DISTRIBUTION_PATH
							+ fileName);

			// creates object file in disk
			FileOutputStream object = new FileOutputStream(
					DataIDGeneralProperties.OBJECT_FILE_DISTRIBUTION_PATH
							+ fileName);

			String lastLine = "";
			String tmpLastSubject = "";

			// starts reading buffer queue
			while (!doneReadingFile) {
				while (bufferQueue.size() > 0) {
					// aint.decrementAndGet();
					try {
						String o[];
						o = bufferQueue.remove().split("\n");
						if (!lastLine.equals("")) {
							o[0] = lastLine.concat(o[0]);

							lastLine = "";
						}
						// System.out.println(o[0]);

						for (int q = 0; q < o.length; q++) {
							String u = o[q];
							if (!u.startsWith("#")) {
								try {

									Pattern pattern = Pattern
											.compile("^(<[^>]+>)\\s+(<[^>]+>)\\s(.*)(\\s\\.)");

									// System.out.println(u);
									Matcher matcher = pattern.matcher(u);
									if (!matcher.matches()) {
										throw new ArrayIndexOutOfBoundsException();
									}

									// get subject and save to file
									if (!tmpLastSubject
											.equals(matcher.group(1))) {
										tmpLastSubject = matcher.group(1);
										subject.write(new String(matcher
												.group(1) + "\n").getBytes());
										while (subjectQueue.size()>1000) {}
										subjectQueue.add(matcher.group(1));
										subjectLines++;
										
									}

									// get object (make sure that its a
									// resource
									// and not a literal), add to queue and
									// save
									// to file
									if (!matcher.group(3).startsWith("\"")) {
										object.write(new String(matcher
												.group(3) + "\n").getBytes());

										// add object to object queue (the
										// queue
										// is read by other thread)
										while (objectQueue.size()>1000) {}
										objectQueue.add(matcher.group(3));
										objectLines++;
									}
									totalTriples++;

								} catch (ArrayIndexOutOfBoundsException e) {
									// e.printStackTrace();
									lastLine = u;
								}
							}
						}

					} catch (NoSuchElementException em) {
						// em.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}

			DataIDBean.pushDownloadInfo();
			object.close();
			subject.close();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}

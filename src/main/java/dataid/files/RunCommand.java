package dataid.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

import dataid.DataID;
import dataid.DataIDGeneralProperties;
import dataid.exceptions.DataIDException;
import dataid.server.DataIDBean;

public class RunCommand {

	int objectTriples = 0;
	int totalTriples = 0;

	public void runRapper(String c, DataIDBean bean, Queue<String> domains)
			throws Exception {

		String[] cmd = { "/bin/sh", "-c", c };

		bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
				"<b>Running rapper command:</b> <i>" + c + "</i>");
		System.out.println("Running rapper command: " + c);

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(cmd);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				proc.getErrorStream()));

		// read the output from the command
		String string = null;
		File f = new File("/tmp/1");
		FileOutputStream fo = new FileOutputStream(f);
		while ((string = stdInput.readLine()) != null) {

			if (string.contains("[totalTriples]")) {
				String a[] = string.split(" ");
				totalTriples = Integer.parseInt(a[1]);
			} else if (string.contains("objectTriples")) {
				String a[] = string.split(" ");
				objectTriples = Integer.parseInt(a[1].replace("/", ""));
			} else {
				string = string.substring(1, string.length());
				string = string.replace("> ", "");
				String[] ar = string.split("/");
				if (ar.length > 3)
					string = ar[0] + "//" + ar[2] + "/" + ar[3] + "/";
				else if (ar.length > 2)
					string = ar[0] + "//" + ar[2] + "/";
				else {
					System.out.println(string);
					string = "";
				}
				if (string.length() < 100) {
					if (!string.equals("")) {
						domains.add(string.substring(1, string.length()));
//						System.out.println(string);
						string = string+"\n";
						fo.write(string.getBytes());
					}
				}
			}
		}

		// read any errors from the attempted command
		// dont show more than 100 errors.
		int errorCount = 0;
		while ((string = stdError.readLine()) != null) {
			if (string.contains("Error")) {
				proc.destroy();
				throw new Exception(string);
			}
			if (errorCount < 100) {
				errorCount++;
				System.out.println(string);
				DataID.bean.addDisplayMessage(
						DataIDGeneralProperties.MESSAGE_ERROR, string);
				if (c.contains("rapper")) {
					if (string.contains("returned")) {
						String a[] = string.split(" ");
						totalTriples = Integer.parseInt(a[3]);
					}
				}
				System.out.println("Rapper output: " + string);
			} else {
				proc.destroy();
				throw new Exception("Too many errors while parsing.");
			}
		}

		proc.waitFor();
		if (proc.exitValue() != 0)
			throw new Exception(
					"Something went wrong while running AWK. Check LOG file.");

		stdInput.close();
		stdError.close();
		proc.destroy();
		System.out.println("Process closed");

	}

}

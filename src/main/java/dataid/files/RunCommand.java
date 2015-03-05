package dataid.files;

import java.io.BufferedReader;
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

	public Queue<String> runRapper(String c, DataIDBean bean) throws Exception {

		Queue<String> results = new ConcurrentLinkedQueue<String>();
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
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			results.add(s);
//			System.out.println("Awk output: " + s);
		}

		// read any errors from the attempted command
		// dont show more than 100 errors.
		int errorCount = 0;
		while ((s = stdError.readLine()) != null) {
			if(s.contains("syntax error ")){
				proc.destroy();
				throw new DataIDException(s);
			}
			if (errorCount < 100) {
				errorCount++;
				System.out.println(s);
				DataID.bean.addDisplayMessage(
						DataIDGeneralProperties.MESSAGE_ERROR, s);
				if (c.contains("rapper")) {
					if (s.contains("returned")) {
						String a[] = s.split(" ");
						results.add("[totalTriples] " + Integer.parseInt(a[3]));
					}
				}
				System.out.println("Rapper output: " + s);
			}
			else{
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
		

		return results;
	}

}

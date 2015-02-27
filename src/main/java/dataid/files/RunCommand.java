package dataid.files;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.Test;

import dataid.DataID;
import dataid.DataIDGeneralProperties;
import dataid.exceptions.DataIDException;

public class RunCommand {
	
	public ArrayList<String> runAwk(String c) throws Exception{
		
		ArrayList<String> domains = new ArrayList<String>();
		String[] cmd = { "/bin/sh", "-c", c };

		DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
				"<b>Running awk command:</b> <i>" + c + "</i>");

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(cmd);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				proc.getErrorStream()));

		// read the output from the command
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			domains.add(s.substring(1,s.length()));
			System.out.println("Object domain found: "+s.substring(1,s.length()));
		}

		// read any errors from the attempted command
		while ((s = stdError.readLine()) != null) {
			DataID.bean.addDisplayMessage(
					DataIDGeneralProperties.MESSAGE_ERROR, s);
			System.out.println(s);
		}

		proc.waitFor();
		if (proc.exitValue() != 0)
			throw new DataIDException("Something went wrong while running AWK. Check LOG file.");

		stdInput.close();
		stdError.close();
		proc.destroy();
		
		return domains;
	}
	
	public static int run(String c) throws Exception {

		String[] cmd = { "/bin/sh", "-c", c };

		DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
				"<b>Running command:</b> <i>" + c + "</i>");

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(cmd);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				proc.getErrorStream()));

		// read the output from the command
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,
					s);
		}

		// read any errors from the attempted command
		while ((s = stdError.readLine()) != null) {
			DataID.bean.addDisplayMessage(
					DataIDGeneralProperties.MESSAGE_ERROR, s);
			if(c.contains("rapper")){
				if(s.contains("returned")){
					String a[] =s.split(" "); 
					return Integer.parseInt(a[3]);
				}
			}
		}

		proc.waitFor();
		if (proc.exitValue() != 0)
			throw new DataIDException("Something went wrong. Check LOG file.");

		stdInput.close();
		stdError.close();
		proc.destroy();
		
		return 0;
	}
}

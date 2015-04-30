package dataid.files;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class RunCommand {
	
	final static Logger logger = Logger.getLogger(RunCommand.class);

	int objectTriples = 0;
	int totalTriples = 0;

	public void runRapper(String c, ConcurrentHashMap<String,Integer> subjectDomains,ConcurrentHashMap<String,Integer> objectDomains)
			throws Exception {

		String[] cmd = { "/bin/sh", "-c", c };

		logger.debug("<b>Running rapper command:</b> <i>" + c + "</i>");
		System.out.println("Running rapper command: " + c);

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(cmd);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				proc.getErrorStream()));

		// read the output from the command
		String string = null;
		while ((string = fixEncoding(stdInput.readLine())) != null) {

			if (string.contains("[totalTriples]")) {
				String a[] = string.split(" ");
				totalTriples = Integer.parseInt(a[1]);
			} else if (string.contains("objectTriples")) {
				String a[] = string.split(" ");
				objectTriples = Integer.parseInt(a[1].replace("/", ""));
			} else if (string.contains("[objectDomain]")) {
				string = string.replace("[objectDomain]", "");
				string = string.substring(1, string.length()-1);
				string = string.replace(">", "");
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
						objectDomains.putIfAbsent(string,1);
					}
				}
			}   else if (string.contains("[subjectDomain]")) {
				string = string.replace("[subjectDomain]", "");
				string = string.substring(1, string.length()-1);
				string = string.replace(">", "");
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
						subjectDomains.putIfAbsent(string,1);
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
				logger.error(string);
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
	
	
	 public static String fixEncoding(String latin1) {
		  try {
		   byte[] bytes = latin1.getBytes("ISO-8859-1");
		   if (!validUTF8(bytes))
		    return latin1;   
		   return new String(bytes, "UTF-8");  
		  } catch (UnsupportedEncodingException e) {
		   // Impossible, throw unchecked
		   throw new IllegalStateException("No Latin1 or UTF-8: " + e.getMessage());
		  }

		 }

		 public static boolean validUTF8(byte[] input) {
		  int i = 0;
		  // Check for BOM
		  if (input.length >= 3 && (input[0] & 0xFF) == 0xEF
		    && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
		   i = 3;
		  }

		  int end;
		  for (int j = input.length; i < j; ++i) {
		   int octet = input[i];
		   if ((octet & 0x80) == 0) {
		    continue; // ASCII
		   }

		   // Check for UTF-8 leading byte
		   if ((octet & 0xE0) == 0xC0) {
		    end = i + 1;
		   } else if ((octet & 0xF0) == 0xE0) {
		    end = i + 2;
		   } else if ((octet & 0xF8) == 0xF0) {
		    end = i + 3;
		   } else {
		    // Java only supports BMP so 3 is max
		    return false;
		   }

		   while (i < end) {
		    i++;
		    octet = input[i];
		    if ((octet & 0xC0) != 0x80) {
		     // Not a valid trailing byte
		     return false;
		    }
		   }
		  }
		  return true;
		 }

}

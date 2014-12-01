package dataid.files;

import dataid.DataID;
import dataid.DataIDGeneralProperties;

public class RunCommand {
	public static void run(String c) {
		Process p;
		try {
			String[] cmd = { "/bin/sh", "-c", c };

			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"<b>Running command:</b> <i>"+c+ "</i>");
			p = Runtime.getRuntime().exec(cmd);
			

			p.waitFor();

			if(p.exitValue() == 0)
				DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Command successfully ran - Exit value: " + p.exitValue());
			else
				DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,"Something went wrong! Exit format was: "+p.exitValue());
			p.destroy();

		} catch (Exception e) {
			e.printStackTrace();
			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,e.getMessage());
		}
				
	}

	public void run2(String c) {
		Process p;
		try {
			String[] cmd = { "/bin/sh", "-c", c };

			System.out.println("Running command: "+c);
			p = Runtime.getRuntime().exec(cmd);
			

			p.waitFor();

//			if(p.exitValue() == 0)
//				DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_LOG,"Command successfully ran - Exit value: " + p.exitValue());
//			else
//				DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,"Something went wrong! Exit format was: "+p.exitValue());
			p.destroy();

		} catch (Exception e) {
			e.printStackTrace();
//			DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,e.getMessage());
		}
	}
}




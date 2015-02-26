package dataid.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import dataid.DataIDGeneralProperties;
import dataid.exceptions.DataIDException;
import dataid.server.DataIDBean;

public class FileUtils {

	public static void checkIfFolderExists() {

		// check if folders needed exists
		File f = new File(DataIDGeneralProperties.BASE_PATH);
		if (!f.exists())
			f.mkdirs();

		f = new File(DataIDGeneralProperties.FILTER_PATH);
		if (!f.exists())
			f.mkdirs();

		f = new File(DataIDGeneralProperties.SUBJECT_PATH);
		if (!f.exists())
			f.mkdirs();

		f = new File(DataIDGeneralProperties.OBJECT_PATH);
		if (!f.exists())
			f.mkdirs();

		f = new File(DataIDGeneralProperties.DATAID_PATH);
		if (!f.exists())
			f.mkdirs();
	}

	// TODO make this method more precise
	public static boolean acceptedFormats(String fileName)
			throws DataIDException {

		if (fileName.contains(".ttl"))
			return true;
		else if (fileName.contains(".nt"))
			return true;
		else if (fileName.contains(".zip"))
			return true;
		else if (fileName.contains(".bzip"))
			return true;
		else {
			throw new DataIDException("File format not accepted: " + fileName);
		}
	}

	public static String stringToHash(String str) {
		String original = str;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(original.getBytes());
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}

			System.out.println("original:" + original);
			System.out.println("digested(hex):" + sb.toString());
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}

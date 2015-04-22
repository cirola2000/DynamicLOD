package dataid.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;

import dataid.Manager;
import dataid.DataIDGeneralProperties;

public class GoogleBloomFilter implements DataIDFilterInterface {
	
	final static Logger logger = Logger.getLogger(GoogleBloomFilter.class);

	public BloomFilter<byte[]> filter = null;

	private Funnel<byte[]> funnel = Funnels.byteArrayFunnel();

	public String fullFilePath;

	public GoogleBloomFilter(int insertions, double fpp) {
		create(insertions, fpp);
	}

	public GoogleBloomFilter() {
		create(98, 0.01);
	}

	public boolean create(int insertions, double fpp) {
		if (filter == null)
			filter = BloomFilter.create(funnel, insertions, fpp);

		return true;
	}

	public boolean add(String s) {
		return filter.put(s.getBytes());

	}

	public boolean compare(String s) throws Exception {
		return filter.mightContain(s.getBytes());
	}

	public boolean saveFilter(String distributionName) {

		String path = DataIDGeneralProperties.SUBJECT_FILE_FILTER_PATH
				+ distributionName;
		fullFilePath = path;

		try {
			filter.writeTo(new FileOutputStream(new File(path)));

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("Filter saved: "+path);
		
		return true;
	}

	public boolean loadFilter(String path) {
		try {
			filter = BloomFilter.readFrom(new FileInputStream(new File(path)),
					funnel);
			fullFilePath = path;
			
			logger.debug("Filter loaded from file: "+ fullFilePath);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// DataID.bean.addDisplayMessage(DataIDGeneralProperties.MESSAGE_ERROR,e.getMessage());
			e.printStackTrace();
		}
		return true;
	}
}

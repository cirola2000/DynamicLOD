package dataid.filters;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;
import org.apache.hadoop.util.hash.Hash;

import dataid.DataIDGeneralProperties;

public class HadoopBloomFilter implements DataIDFilterInterface {

	BloomFilter filter = null;

	public HadoopBloomFilter(int insertions, double fpp) {
		create(insertions, fpp);
	}
	
	public boolean create(int insertions, double fpp) {

		// Got the next equation from here: http://hur.st/bloomfilter?n=1500000&p=1.0E-20
		// m is the number of bits in the filter
		Double m = Math.ceil((insertions * Math.log(fpp))
				/ Math.log(1.0 / (Math.pow(2.0, Math.log(2.0)))));

		// k is the number of hash filters
		Long k = Math.round(Math.log(2.0) * m / insertions);
		
		System.out.println(m.intValue()+" "+k);

		if (filter == null)
			filter = new BloomFilter(m.intValue(), k.intValue(), Hash.MURMUR_HASH);

		return true;
	}

	public boolean add(String s) {
		filter.add(new Key(s.getBytes()));
		return true;
	}

	public boolean compare(String s) {
		filter.membershipTest(new Key(s.getBytes()));
		return true;
	}

	public boolean saveFilter(String distributionName) {
		
		String path = DataIDGeneralProperties.SUBJECT_FILE_FILTER_PATH+distributionName;
		System.out.println("Saving filter to: "+path);
		
		try {
			filter.write((DataOutput) new FileOutputStream(new File(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return false;
	}

	public boolean loadFilter(String name) {
		try {
			filter.readFields((DataInput) new FileInputStream(new File("/tmp/filter")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}


}

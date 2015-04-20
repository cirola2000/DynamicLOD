package dataid.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeObject {
	
	String fileName = null;
	File f = null;
	
	public long getFileSize(){
		return f.length();
	}
	
	public SerializeObject(String fileName) {
		this.fileName = fileName;
	}
	
	public void save(Object o) throws IOException{
		f = new File(fileName);
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(o);
		oos.close();
	}
	
	public Object load() throws Exception{
		f = new File(fileName);
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object o =  ois.readObject();
		ois.close();
		return o;
	}
	
}

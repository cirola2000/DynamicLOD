package dataid.evaluation;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface SearchAlgorithm {

	
	public void AddElements(String file) throws FileNotFoundException;
	
	public void SearchElements(String file) throws FileNotFoundException;
	
	public void Save(String file)  throws IOException;
	
	public int getPositives();
	
	public long getFileSize();
	
	public String getTimeToCreate();
	
	public String getTimeToSearch();
	
	
}

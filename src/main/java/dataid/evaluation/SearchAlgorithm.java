package dataid.evaluation;

import java.io.FileNotFoundException;

public interface SearchAlgorithm {

	
	public void AddElements(String file) throws FileNotFoundException;
	
	public void SearchElements(String file) throws FileNotFoundException;
	
	public int getPositives();
	
	
}

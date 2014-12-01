package dataid.filters;


public interface DataIDFilterInterface {

	public boolean create(int insertions, double fpp);
	
	public boolean add(String s);
	
	public boolean compare(String s) throws Exception;
	
	public boolean saveFilter(String distributionName);
	
	public boolean loadFilter(String distributionName);
	
	
}

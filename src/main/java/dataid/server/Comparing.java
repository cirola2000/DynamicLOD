package dataid.server;

public class Comparing {

	public String object;
	private String filter;
	private String time;
	
	public Comparing(String object, String filter, String time) {
		this.object = object;
		this.filter = filter;
		this.time = time;
	}
	
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
}

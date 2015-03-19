package dataid.download;

public class FileNameFromURL {
	
	public String getFileName(String accessURL, String httpDisposition) {
		String fileName = null;
		if (httpDisposition != null) {
			int index = httpDisposition.indexOf("filename=");
			if (index > 0) {
				fileName = httpDisposition.substring(index + 10,
						httpDisposition.length() - 1);
			}
		} else {
			// extracts file name from URL
			fileName = accessURL.substring(accessURL.lastIndexOf("/") + 1,
					accessURL.length());
		}
		
		return fileName;
	}
	
}

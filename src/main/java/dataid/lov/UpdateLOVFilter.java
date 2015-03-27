package dataid.lov;

import dataid.download.DownloadLOVVocabularies;

public class UpdateLOVFilter {

	public UpdateLOVFilter() {
		try {
			DownloadLOVVocabularies d = new DownloadLOVVocabularies();
			d.downloadLOV();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

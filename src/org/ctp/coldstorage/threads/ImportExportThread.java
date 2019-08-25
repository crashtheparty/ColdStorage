package org.ctp.coldstorage.threads;

import org.ctp.coldstorage.utils.StorageUtils;

public class ImportExportThread implements Runnable{

	@Override
	public void run() {
		try {
		StorageUtils.setImportChests();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		try {
			StorageUtils.setExportChests();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	
	
}

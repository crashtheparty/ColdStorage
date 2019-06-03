package org.ctp.coldstorage.threads;

import org.ctp.coldstorage.utils.StorageUtils;

public class ImportExportThread implements Runnable{

	@Override
	public void run() {
		StorageUtils.setImportChests();
		StorageUtils.setExportChests();
	}	
	
}

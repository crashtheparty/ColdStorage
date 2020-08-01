package org.ctp.coldstorage.database;

import org.ctp.crashapi.CrashAPIPlugin;
import org.ctp.crashapi.db.BackupDB;

public class CSBackup extends BackupDB {

	public CSBackup(CrashAPIPlugin instance) {
		super(instance, "backups");
	}

}

package org.ctp.coldstorage.database;

import java.util.logging.Level;

import org.ctp.coldstorage.ColdStorage;

public class Error {
	public static void execute(ColdStorage plugin, Exception ex) {
		plugin.getLogger().log(Level.SEVERE,
				"Couldn't execute MySQL statement: ", ex);
	}

	public static void close(ColdStorage plugin, Exception ex) {
		plugin.getLogger().log(Level.SEVERE,
				"Failed to close MySQL connection: ", ex);
	}
}
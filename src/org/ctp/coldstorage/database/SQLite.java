package org.ctp.coldstorage.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.database.tables.StorageTable;
import org.ctp.coldstorage.database.tables.Table;

public class SQLite extends Database {

	String dbname;

	public ArrayList<Table> tables = new ArrayList<Table>();

	public SQLite(ColdStorage instance) {
		super(instance);

		tables.add(new StorageTable(this));

		dbname = "cold_storage"; // Set the table name here e.g player_kills
	}
	
	public <T> Table getTable(Class<T> cls) {
		for(Table table : tables) {
			if(table.getClass().equals(cls)) {
				return table;
			}
		}
		return null;
	}

	// SQL creation stuff, You can leave the blow stuff untouched.
	public Connection getSQLConnection() {
		File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");
		if (!dataFolder.exists()) {
			try {
				dataFolder.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().log(Level.SEVERE,
						"File write error: " + dbname + ".db");
			}
		}
		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:"
					+ dataFolder);
			return connection;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE,
					"SQLite exception on initialize", ex);
		} catch (ClassNotFoundException ex) {
			plugin.getLogger()
					.log(Level.SEVERE,
							"You need the SQLite JBDC library. Google it. Put it in /lib folder.");
		}
		return null;
	}

	public void load() {
		connection = getSQLConnection();
		for (Table t : tables) {
			t.createTable(connection);
		}
		initialize();
	}
	
	public ColdStorage getPlugin() {
		return plugin;
	}
}
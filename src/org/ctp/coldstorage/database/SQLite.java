package org.ctp.coldstorage.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.database.tables.CacheTable;
import org.ctp.coldstorage.database.tables.ChestTable;
import org.ctp.coldstorage.database.tables.ChestTypeTable;
import org.ctp.coldstorage.database.tables.GlobalPermissionsTable;
import org.ctp.coldstorage.database.tables.PermissionsTable;
import org.ctp.coldstorage.database.tables.StorageTypeTable;
import org.ctp.coldstorage.database.tables.Table;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;

public class SQLite extends Database {

	String dbname;

	public ArrayList<Table> tables = new ArrayList<Table>();

	public SQLite(ColdStorage instance) {
		super(instance);

		tables.add(new CacheTable(this));
		tables.add(new ChestTable(this));
		tables.add(new ChestTypeTable(this));
		tables.add(new GlobalPermissionsTable(this));
		tables.add(new PermissionsTable(this));
		tables.add(new StorageTypeTable(this));

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
				setNewInitialized(true);
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

	@Override
	public void addDefault() {
		StorageType type = new StorageType("Basic", 0, 0, 1000, new ItemStack(Material.DIAMOND, 4), 20000000);
		
		DatabaseUtils.addStorageType(Bukkit.getOfflinePlayer(UUID.fromString("58ca0e55-7809-4d91-9431-7889209cc77e")), type);
	}

	@Override
	public void migrateTables() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StorageType type = ColdStorage.getPlugin().getConfiguration().getLegacyData();
		List<Storage> storages = new ArrayList<Storage>();
		if(type != null) {
			try {
				conn = getSQLConnection();
				ps = conn.prepareStatement("SELECT * FROM cold_storage;");

				rs = ps.executeQuery();
				while (rs.next()) {
					ChatUtils.sendInfo("Amount: " + rs.getInt("amount"));
					storages.add(new Storage(Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("player"))), 
							rs.getString("storage_unique"), rs.getString("material"), rs.getString("metadata"), 
							type.getType(), rs.getString("material"), rs.getInt("amount"), rs.getInt("order_by"), true));
				}
			} catch (SQLException ex) {
				
			} finally {
				try {
					if (ps != null)
						ps.close();
					if (conn != null)
						conn.close();
				} catch (SQLException ex) {
					getPlugin().getLogger().log(Level.SEVERE,
							Errors.sqlConnectionClose(), ex);
				}
			}
			DatabaseUtils.addStorageType(Bukkit.getOfflinePlayer(UUID.fromString("58ca0e55-7809-4d91-9431-7889209cc77e")), type);
			if(storages.size() > 0) {
				for(Storage storage : storages) {
					DatabaseUtils.addCache(Bukkit.getOfflinePlayer(UUID.fromString("58ca0e55-7809-4d91-9431-7889209cc77e")), storage);
					DatabaseUtils.updateCache(Bukkit.getOfflinePlayer(UUID.fromString("58ca0e55-7809-4d91-9431-7889209cc77e")), storage);
				}
			}

			try {
				conn = getSQLConnection();
				ps = conn.prepareStatement("DROP TABLE IF EXISTS cold_storage;");

				ps.execute();
			} catch (SQLException ex) {
				
			} finally {
				try {
					if (ps != null)
						ps.close();
					if (conn != null)
						conn.close();
				} catch (SQLException ex) {
					getPlugin().getLogger().log(Level.SEVERE,
							Errors.sqlConnectionClose(), ex);
				}
			}
		}
	}
}
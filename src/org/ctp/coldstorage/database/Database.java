package org.ctp.coldstorage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.storage.Cache;
import org.ctp.coldstorage.storage.StorageList;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.MaterialUtils;
import org.ctp.coldstorage.utils.yamlconfig.YamlConfig;

public abstract class Database {
	public ColdStorage plugin;
	Connection connection;
	// The name of the table we created back in SQLite class.
	public String table = "storages";
	public int tokens = 0;
	private boolean newInitialized = false;

	public Database(ColdStorage instance) {
		plugin = instance;
	}

	public abstract Connection getSQLConnection();

	public abstract void load();
	
	public abstract void addDefault();
	
	public abstract void migrateTables();

	public void initialize() {
		connection = getSQLConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean retrieveConnection = true;
		try {
			ps = connection.prepareStatement("SELECT * FROM "
					+ table);
			rs = ps.executeQuery();
			close(ps, rs);

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE,
					"Unable to retreive connection", ex);
			retrieveConnection = false;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
			if(!retrieveConnection) return;
		}
		
		if(newInitialized) {
			addDefault();
		} else {
			migrateTables();
		}
		
		migrateData();
	}
	
	public void migrateData() {
		YamlConfig config = ColdStorage.getPlugin().getConfiguration().getMainConfig();
		if(config.getBoolean("migrate_material_names")) {
			List<OfflinePlayer> players = DatabaseUtils.getOfflinePlayers();
			for(OfflinePlayer player : players) {
				StorageList storageList = StorageList.getList(player);
				List<Cache> caches = new ArrayList<Cache>();
				caches.addAll(storageList.getDrafts());
				caches.addAll(storageList.getStorages());
				for(Cache cache : caches) {
					cache.setMaterial(MaterialUtils.migrateMaterial(cache.getMaterialName()));
					DatabaseUtils.updateCache(player, cache);
				}
			}
			
			config.set("migrate_material_names", false);
			config.saveConfig();
		}
	}
	
	public void close(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			Error.close(plugin, ex);
		}
	}

	public boolean isNewInitialized() {
		return newInitialized;
	}

	public void setNewInitialized(boolean newInitialized) {
		this.newInitialized = newInitialized;
	}
}
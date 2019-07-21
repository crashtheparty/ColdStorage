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
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM "
					+ table);
			ResultSet rs = ps.executeQuery();
			close(ps, rs);

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE,
					"Unable to retreive connection", ex);
			return;
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
	
	public boolean hasRecord(String key, String table){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean found = false;
		try {
			String query = "SELECT (count(*) > 0) as found FROM " + table + " WHERE player LIKE ?";
			conn = getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, key);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				found = rs.getBoolean(1); // "found" column
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return found;
	}
	
	public Integer getInteger(String table, String key, String field){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer integer = 0;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + table
					+ " WHERE player = '" + key + "';");

			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString("player").equals(
						key)) { 
					integer = rs.getInt(field);
					break;
				}
			}
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
					ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return integer;
	}
	
	public void setInteger(String table, String key, String field, Integer integer){
		Connection conn = null;
		PreparedStatement ps = null;
		boolean hasRecord = hasRecord(key, table);
		try {
			conn = getSQLConnection();
			if(hasRecord){
				ps = conn.prepareStatement("UPDATE " + table + " SET " + field + " = ? WHERE player = ?");
	
				ps.setInt(1, integer); 
	
				ps.setString(2, key);
			}else{
				ps = conn.prepareStatement("INSERT INTO " + table + " (player, " + field + ") VALUES (?, ?)");
				
				ps.setInt(2, integer); 
	
				ps.setString(1, key);
			}
			ps.executeUpdate();
			return;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
					ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return;
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
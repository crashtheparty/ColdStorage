package org.ctp.coldstorage.database;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.database.tables.*;
import org.ctp.coldstorage.storage.*;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.MaterialUtils;
import org.ctp.crashapi.CrashAPIPlugin;
import org.ctp.crashapi.config.yaml.YamlConfig;
import org.ctp.crashapi.db.Errors;
import org.ctp.crashapi.db.SQLite;

public class CSDatabase extends SQLite {
	private boolean newInitialized = false;

	public CSDatabase(CrashAPIPlugin instance) {
		super(instance, "cold_storage");

		addTable(new CacheTable(this));
		addTable(new ChestTable(this));
		addTable(new ChestTypeTable(this));
		addTable(new GlobalPermissionsTable(this));
		addTable(new PermissionsTable(this));
		addTable(new StorageTypeTable(this));
	}

	public <T> T getTable(Class<T> cls) {
		return super.getTable(cls);
	}

	public void addDefault() {
		StorageType type = new StorageType("Basic", 0, 0, 1000, new ItemStack(Material.DIAMOND, 4), 2000000);
		
		DatabaseUtils.addStorageType(Bukkit.getOfflinePlayer(UUID.fromString("58ca0e55-7809-4d91-9431-7889209cc77e")), type);
	}

	@Override
	public Connection getSQLConnection() {
		File dataFolder = new File(getInstance().getDataFolder(), getDBName() + ".db");
		if (!dataFolder.exists()) 
			newInitialized = true;
		return super.getSQLConnection();
	}
	
	@Override
	protected void initialize() {
		super.initialize("storages");
		
		if(newInitialized) addDefault();
		else
			migrateTables();
		
		migrateData();
	}

	public void migrateTables() {
		Connection conn = null;
		PreparedStatement ps = null;
		Connection conn2 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		StorageType type = ColdStorage.getPlugin().getConfiguration().getLegacyData();
		List<Storage> storages = new ArrayList<Storage>();
		if(type != null) {
			try {
				conn = getSQLConnection();
				ps = conn.prepareStatement("SELECT * FROM storages");

				rs = ps.executeQuery();
				while (rs.next())
					storages.add(new Storage(Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("player"))), 
							rs.getString("storage_unique"), rs.getString("material"), rs.getString("metadata"), 
							type.getType(), rs.getString("material"), rs.getInt("amount"), rs.getInt("order_by"), true));
			} catch (SQLException ex) {
				
			} finally {
				try {
					if (ps != null)
						ps.close();
					if (rs != null)
						rs.close();
					if (conn != null)
						conn.close();
				} catch (SQLException ex) {
					getInstance().getLogger().log(Level.SEVERE,
							Errors.sqlConnectionClose(), ex);
				}
			}
			DatabaseUtils.addStorageType(Bukkit.getOfflinePlayer(UUID.fromString("58ca0e55-7809-4d91-9431-7889209cc77e")), type);
			if(storages.size() > 0) for(Storage storage : storages) {
				DatabaseUtils.addCache(Bukkit.getOfflinePlayer(UUID.fromString("58ca0e55-7809-4d91-9431-7889209cc77e")), storage);
				DatabaseUtils.updateCache(Bukkit.getOfflinePlayer(UUID.fromString("58ca0e55-7809-4d91-9431-7889209cc77e")), storage);
			}

			try {
				conn2 = getSQLConnection();
				ps2 = conn2.prepareStatement("DROP TABLE IF EXISTS cold_storage;");

				ps2.execute();
			} catch (SQLException ex) {
				
			} finally {
				try {
					if (ps2 != null)
						ps2.close();
					if (conn2 != null)
						conn2.close();
				} catch (SQLException ex) {
					getInstance().getLogger().log(Level.SEVERE,
							Errors.sqlConnectionClose(), ex);
				}
			}
		}
	}
	
	public void migrateData() {
		YamlConfig config = ColdStorage.getPlugin().getConfigurations().getConfig().getConfig();
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
}

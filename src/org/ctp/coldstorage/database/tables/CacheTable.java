package org.ctp.coldstorage.database.tables;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.storage.Cache;
import org.ctp.coldstorage.storage.Draft;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.crashapi.db.Errors;
import org.ctp.crashapi.db.SQLite;
import org.ctp.crashapi.db.tables.Table;
import org.ctp.crashapi.utils.ChatUtils;

public class CacheTable extends Table {

	public CacheTable(SQLite db) {
		super(db, "storages", Arrays.asList("player", "storage_unique"));
		addColumn("player", "varchar", "\"\"");
		addColumn("storage_unique", "varchar", "\"\"");
		addColumn("storage_type", "varchar", "\"\"");
		addColumn("is_draft", "int", "0");
		addColumn("material", "varchar", "\"\"");
		addColumn("stored_amount", "int", "0");
		addColumn("metadata", "varchar", "\"\"");
		addColumn("name", "varchar", "\"\"");
		addColumn("can_insert_all", "int", "1");
		addColumn("created_at", "varchar", "\"\"");
		addColumn("created_by", "varchar", "\"\"");
		addColumn("updated_at", "varchar", "\"\"");
		addColumn("modified_by", "varchar", "\"\"");
		addColumn("order_by", "int", "0");
	}

	public List<OfflinePlayer> getOfflinePlayers() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT DISTINCT(player) FROM " + getName() + ";");

			rs = ps.executeQuery();
			while (rs.next())
				players.add(Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("player"))));
		} catch (SQLException ex) {
			ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return players;
	}

	public List<Cache> getPlayerStorage(OfflinePlayer player, boolean isDraft) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Cache> storages = new ArrayList<Cache>();
		String uuid = player.getUniqueId().toString();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + " WHERE player = '" + uuid + "' ORDER BY order_by asc, created_at asc;");

			rs = ps.executeQuery();
			while (rs.next())
				if (!isDraft && !rs.getBoolean("is_draft")) {
					Storage storage = new Storage(player, rs.getString("storage_unique"), rs.getString("material"), rs.getString("metadata"), rs.getString("storage_type"), rs.getString("name"), rs.getInt("stored_amount"), rs.getInt("order_by"), rs.getBoolean("can_insert_all"));
					storages.add(storage);
				} else if (isDraft && rs.getBoolean("is_draft")) {
					Draft draft = new Draft(player, rs.getString("storage_unique"), rs.getString("material"), rs.getString("metadata"), rs.getString("storage_type"), rs.getString("name"));
					storages.add(draft);
				}
		} catch (SQLException ex) {
			ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return storages;
	}

	public Cache getStorage(OfflinePlayer player, String unique, boolean isDraft) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Cache storage = null;
		String uuid = player.getUniqueId().toString();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + " WHERE player = '" + uuid + "' AND storage_unique = '" + unique + "';");

			rs = ps.executeQuery();
			while (rs.next())
				if (!isDraft && !rs.getBoolean("is_draft")) storage = new Storage(player, rs.getString("storage_unique"), rs.getString("material"), rs.getString("metadata"), rs.getString("storage_type"), rs.getString("name"), rs.getInt("stored_amount"), rs.getInt("order_by"), rs.getBoolean("can_insert_all"));
				else if (isDraft && rs.getBoolean("is_draft")) storage = new Draft(player, rs.getString("storage_unique"), rs.getString("material"), rs.getString("metadata"), rs.getString("storage_type"), rs.getString("name"));
		} catch (SQLException ex) {
			ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return storage;
	}

	public boolean hasStorageRecord(Cache storage) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean found = false;
		try {
			String query = "SELECT (count(*) > 0) as found FROM " + this.getName() + " WHERE player LIKE ? AND storage_unique LIKE ?";
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, storage.getPlayer().getUniqueId().toString());
			ps.setString(2, storage.getUnique());
			rs = ps.executeQuery();

			if (rs.next()) found = rs.getBoolean(1); // "found" column
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return found;
	}

	public void setPlayerStorage(Cache cache, OfflinePlayer player) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean hasRecord = hasStorageRecord(cache);
		if (hasRecord) try {
			LocalDateTime date = LocalDateTime.now();
			String dateString = date.toString();
			conn = getDb().getSQLConnection();
			if (cache instanceof Storage) {
				Storage storage = (Storage) cache;

				ps = conn.prepareStatement("UPDATE " + this.getName() + " SET stored_amount = ?, order_by = ?, storage_type = ?, is_draft = ?, " + "name = ?, can_insert_all = ?, material = ?, modified_by = ?, updated_at = ? WHERE player = ? AND storage_unique = ?");

				ps.setInt(1, storage.getStoredAmount());
				ps.setInt(2, storage.getOrderBy());
				ps.setString(3, storage.getStorageTypeString());
				ps.setBoolean(4, false);
				ps.setString(5, storage.getName());
				ps.setBoolean(6, storage.canInsertAll());
				ps.setString(7, storage.getMaterialName());
				ps.setString(8, player.getUniqueId().toString());
				ps.setString(9, dateString);

				ps.setString(10, storage.getPlayer().getUniqueId().toString());
				ps.setString(11, storage.getUnique());

				ps.executeUpdate();
			} else if (cache instanceof Draft) {
				Draft draft = (Draft) cache;

				ps = conn.prepareStatement("UPDATE " + this.getName() + " SET storage_type = ?, is_draft = ?, " + "name = ?, metadata = ?, material = ?, modified_by = ?, updated_at = ? WHERE player = ? AND storage_unique = ?");

				ps.setString(1, draft.getStorageTypeString());
				ps.setBoolean(2, true);
				ps.setString(3, draft.getName());
				ps.setString(4, draft.getMeta());
				ps.setString(5, draft.getMaterialName());
				ps.setString(6, player.getUniqueId().toString());
				ps.setString(7, dateString);

				ps.setString(8, draft.getPlayer().getUniqueId().toString());
				ps.setString(9, draft.getUnique());
				ps.executeUpdate();
			}
		} catch (SQLException ex) {
			ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		else {
			ColdStorage.getPlugin().getLogger().log(Level.WARNING, "Missing possible record with storage: " + cache.toString());
			if (player.isOnline()) Chatable.get().sendMessage(player.getPlayer(), Chatable.get().getMessage(ChatUtils.getCodes(), "database.issue"));
		}
		return;
	}

	public void addPlayerStorage(Cache cache, OfflinePlayer created) {
		OfflinePlayer player = cache.getPlayer();
		Material material = cache.getMaterial();
		String metadata = cache.getMeta();
		String name = cache.getName();
		String storageType = cache.getStorageTypeString();
		int order = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDb().getSQLConnection();
			LocalDateTime date = LocalDateTime.now();
			String dateString = date.toString();
			ps = conn.prepareStatement("INSERT INTO " + this.getName() + " (player, material, stored_amount, metadata, storage_unique, name, storage_type, is_draft, created_at, created_by, " + "updated_at, modified_by, order_by)" + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			ps.setString(1, player.getUniqueId().toString());
			ps.setString(2, material.name());
			ps.setInt(3, 0);
			ps.setString(4, metadata);
			if (cache.getUnique() == null) {
				UUID id = UUID.randomUUID();
				ps.setString(5, id.toString());
				cache.setUnique(id.toString());
			} else
				ps.setString(5, cache.getUnique());
			ps.setString(6, name);
			ps.setString(7, storageType);
			ps.setBoolean(8, cache instanceof Draft);
			ps.setString(9, dateString);
			ps.setString(10, created.getUniqueId().toString());
			ps.setString(11, dateString);
			ps.setString(12, created.getUniqueId().toString());
			ps.setInt(13, order);

			ps.execute();
		} catch (SQLException ex) {
			ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return;
	}

	public void deletePlayerStorage(Cache cache) {
		OfflinePlayer player = cache.getPlayer();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("DELETE FROM " + this.getName() + " WHERE player = ? AND storage_unique = ?");

			ps.setString(1, player.getUniqueId().toString());
			ps.setString(2, cache.getUnique());

			ps.execute();
		} catch (SQLException ex) {
			ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ColdStorage.getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return;
	}
}

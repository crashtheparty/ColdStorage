package org.ctp.coldstorage.database.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.ctp.coldstorage.database.Errors;
import org.ctp.coldstorage.database.SQLite;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.Storage;

public class StorageTable extends Table{
	
	public StorageTable(SQLite db) {
		super(db, "cold_storage", Arrays.asList("player", "storage_unique"));
		addColumn("player", "varchar", "\"\"");
		addColumn("storage_unique", "varchar", "\"\"");
		addColumn("material", "varchar", "\"\"");
		addColumn("amount", "int", "0");
		addColumn("metadata", "varchar", "\"\"");
		addColumn("created_at", "varchar", "\"\"");
		addColumn("created_by", "varchar", "\"\"");
		addColumn("updated_at", "varchar", "\"\"");
		addColumn("modified_by", "varchar", "\"\"");
		addColumn("order_by", "int", "0");
	}
	
	public List<Storage> getPlayerStorage(OfflinePlayer player){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Storage> storages = new ArrayList<Storage>();
		String uuid = player.getUniqueId().toString();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName()
					+ " WHERE player = '" + uuid + "' ORDER BY order_by asc, created_at asc;");

			rs = ps.executeQuery();
			while (rs.next()) {
				storages.add(new Storage(player, rs.getString("storage_unique"), Material.valueOf(rs.getString("material")), rs.getInt("amount"), rs.getString("metadata"), rs.getInt("order_by")));
			}
		} catch (SQLException ex) {
			getDb().getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
					ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				getDb().getPlugin().getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return storages;
	}
	
	public Storage getStorage(OfflinePlayer player, String unique){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Storage storage = null;
		String uuid = player.getUniqueId().toString();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName()
					+ " WHERE player = '" + uuid + "' AND storage_unique = '" + unique + "';");

			rs = ps.executeQuery();
			while (rs.next()) {
				storage = new Storage(player, rs.getString("storage_unique"), Material.valueOf(rs.getString("material")), rs.getInt("amount"), rs.getString("metadata"), rs.getInt("order_by"));
			}
		} catch (SQLException ex) {
			getDb().getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
					ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				getDb().getPlugin().getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return storage;
	}
	
	public boolean hasStorageRecord(Storage storage) {
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
				getDb().getPlugin().getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return found;
	}
	
	public void setPlayerStorage(Storage storage, OfflinePlayer player) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean hasRecord = hasStorageRecord(storage);
		if(hasRecord) {
			try {
				LocalDateTime date = LocalDateTime.now();
				String dateString = date.toString();
				conn = getDb().getSQLConnection();
				ps = conn.prepareStatement("UPDATE " + this.getName() + " SET amount = ?, order_by = ?, modified_by = ?, updated_at = ? WHERE player = ? AND storage_unique = ?");
	
				ps.setInt(1, storage.getAmount()); 
				ps.setInt(2, storage.getOrderBy());  
				ps.setString(3, storage.getPlayer().getUniqueId().toString()); 
				ps.setString(4, dateString); 
	
				ps.setString(5, storage.getPlayer().getUniqueId().toString());
				ps.setString(6, storage.getUnique());
				ps.executeUpdate();
			} catch (SQLException ex) {
				getDb().getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
						ex);
			} finally {
				try {
					if (ps != null)
						ps.close();
					if (conn != null)
						conn.close();
				} catch (SQLException ex) {
					getDb().getPlugin().getLogger().log(Level.SEVERE,
							Errors.sqlConnectionClose(), ex);
				}
			}
		} else {
			getDb().getPlugin().getLogger().log(Level.WARNING, "Missing possible record with storage: " + storage.toString());
			if(player.isOnline()) {
				ChatUtils.sendMessage(player.getPlayer(), "Issue with the plugin. Please contact an administrator to get this resolved.");
			}
		}
		return;
	}
	
	public void addPlayerStorage(Storage storage, Player created) {
		OfflinePlayer player = storage.getPlayer();
		Material material = storage.getMaterial();
		String metadata = storage.getMeta();
		int order = storage.getOrderBy();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDb().getSQLConnection();
			LocalDateTime date = LocalDateTime.now();
			String dateString = date.toString();
			ps = conn.prepareStatement("INSERT INTO " + this.getName() + 
					" (player, material, amount, metadata, storage_unique, created_at, created_by, updated_at, modified_by, order_by)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			ps.setString(1, player.getUniqueId().toString());
			ps.setString(2, material.name());
			ps.setInt(3, 0);
			ps.setString(4, metadata);
			if(storage.getUnique() == null) {
				ps.setString(5, UUID.randomUUID().toString());
				storage.setUnique(UUID.randomUUID().toString());
			} else {
				ps.setString(5, storage.getUnique());
			}
			ps.setString(6, dateString);
			ps.setString(7, created.getUniqueId().toString());
			ps.setString(8, dateString);
			ps.setString(9, created.getUniqueId().toString());
			ps.setInt(10, order);
			
			ps.execute();
		} catch (SQLException ex) {
			getDb().getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
					ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				getDb().getPlugin().getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return;
	}
	
	public void deletePlayerStorage(Storage storage) {
		OfflinePlayer player = storage.getPlayer();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("DELETE FROM " + this.getName() + 
					" WHERE player = ? AND storage_unique = ?");
			
			ps.setString(1, player.getUniqueId().toString());
			ps.setString(2, storage.getUnique());
			
			ps.execute();
		} catch (SQLException ex) {
			getDb().getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
					ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				getDb().getPlugin().getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return;
	}
}

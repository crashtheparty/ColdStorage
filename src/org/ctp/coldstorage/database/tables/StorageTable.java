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
import org.bukkit.entity.Player;
import org.ctp.coldstorage.database.Errors;
import org.ctp.coldstorage.database.SQLite;
import org.ctp.coldstorage.utils.ChatUtilities;
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
	}
	
	public List<Storage> getPlayerStorage(Player player){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Storage> storages = new ArrayList<Storage>();
		String uuid = player.getUniqueId().toString();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName()
					+ " WHERE player = '" + uuid + "' ORDER BY created_at asc;");

			rs = ps.executeQuery();
			while (rs.next()) {
				storages.add(new Storage(player, rs.getString("storage_unique"), Material.valueOf(rs.getString("material")), rs.getInt("amount"), rs.getString("metadata")));
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
	
	public Storage getStorage(Player player, String unique){
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
				storage = new Storage(player, rs.getString("storage_unique"), Material.valueOf(rs.getString("material")), rs.getInt("amount"), rs.getString("metadata"));
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
	
	public void setPlayerStorage(Storage storage) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean hasRecord = hasStorageRecord(storage);
		if(hasRecord) {
			try {
				conn = getDb().getSQLConnection();
				ps = conn.prepareStatement("UPDATE " + this.getName() + " SET amount = ? WHERE player = ? AND storage_unique = ?");
	
				ps.setInt(1, storage.getAmount()); 
	
				ps.setString(2, storage.getPlayer().getUniqueId().toString());
				ps.setString(3, storage.getUnique());
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
			ChatUtilities.sendMessage(storage.getPlayer(), "Issue with the plugin. Please contact an administrator to get this resolved.");
		}
		return;
	}
	
	public void addPlayerStorage(Player player, Material material, String metadata) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDb().getSQLConnection();
			LocalDateTime date = LocalDateTime.now();
			String dateString = date.toString();
			ps = conn.prepareStatement("INSERT INTO " + this.getName() + " (player, material, amount, metadata, storage_unique, created_at) VALUES (?, ?, ?, ?, ?, ?)");
			
			ps.setString(1, player.getUniqueId().toString());
			ps.setString(2, material.name());
			ps.setInt(3, 0);
			ps.setString(4, metadata);
			ps.setString(5, UUID.randomUUID().toString());
			ps.setString(6, dateString);
			
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

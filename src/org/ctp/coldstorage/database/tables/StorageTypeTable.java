package org.ctp.coldstorage.database.tables;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.crashapi.db.Errors;
import org.ctp.crashapi.db.SQLite;
import org.ctp.crashapi.db.tables.Table;
import org.ctp.crashapi.utils.ChatUtils;

public class StorageTypeTable extends Table {

	public StorageTypeTable(SQLite db) {
		super(db, "storage_types", Arrays.asList("type"));
		addColumn("type", "varchar", "\"\"");
		addColumn("max_import", "int", "0");
		addColumn("max_export", "int", "0");
		addColumn("vault_price", "real", "0.00");
		addColumn("item_price", "varchar", "\"\"");
		addColumn("max_amount_base", "int", "0");
		addColumn("permissions", "varchar", "\"\"");
		addColumn("created_at", "varchar", "\"\"");
		addColumn("created_by", "varchar", "\"\"");
		addColumn("updated_at", "varchar", "\"\"");
		addColumn("modified_by", "varchar", "\"\"");
	}

	public List<StorageType> getAllTypes() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<StorageType> storageTypes = new ArrayList<StorageType>();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + ";");

			rs = ps.executeQuery();
			while (rs.next())
				storageTypes.add(new StorageType(rs.getString("type"), rs.getInt("max_export"), rs.getInt("max_import"), rs.getDouble("vault_price"), ColdStorage.getPlugin().getItemSerial().stringToItem(rs.getString("item_price")), rs.getInt("max_amount_base")));
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
		return storageTypes;
	}

	public StorageType getType(String type) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StorageType storageType = null;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + " WHERE type = '" + type + "';");

			rs = ps.executeQuery();
			while (rs.next())
				storageType = new StorageType(type, rs.getInt("max_export"), rs.getInt("max_import"), rs.getDouble("vault_price"), ColdStorage.getPlugin().getItemSerial().stringToItem(rs.getString("item_price")), rs.getInt("max_amount_base"));
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
		return storageType;
	}

	public boolean hasStorageType(StorageType storageType) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean found = false;
		try {
			String query = "SELECT (count(*) > 0) as found FROM " + this.getName() + " WHERE type LIKE ?";
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, storageType.getType());
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

	public void setStorageType(StorageType storageType, OfflinePlayer player) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean hasRecord = hasStorageType(storageType);
		if (hasRecord) try {
			LocalDateTime date = LocalDateTime.now();
			String dateString = date.toString();
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("UPDATE " + this.getName() + " SET max_import = ?, max_export = ?, vault_price = ?, item_price = ?, " + "max_amount_base = ?, modified_by = ?, updated_at = ? WHERE type = ?");

			ps.setInt(1, storageType.getMaxImport());
			ps.setInt(2, storageType.getMaxExport());
			ps.setDouble(3, storageType.getVaultCost());
			ps.setString(4, ColdStorage.getPlugin().getItemSerial().itemToString(storageType.getItemCost()));
			ps.setInt(5, storageType.getMaxAmountBase());
			ps.setString(6, player.getUniqueId().toString());
			ps.setString(7, dateString);

			ps.setString(8, storageType.getType());

			ps.executeUpdate();
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
			ColdStorage.getPlugin().getLogger().log(Level.WARNING, "Missing possible record with storage type: " + storageType.getType());
			if (player.isOnline()) Chatable.get().sendMessage(player.getPlayer(), Chatable.get().getMessage(ChatUtils.getCodes(), "database.issue"));
		}
		return;
	}

	public void addStorageType(StorageType storageType, OfflinePlayer player) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDb().getSQLConnection();
			LocalDateTime date = LocalDateTime.now();
			String dateString = date.toString();
			ps = conn.prepareStatement("INSERT INTO " + this.getName() + " (type, max_import, max_export, vault_price, item_price, max_amount_base, created_at, created_by, updated_at, modified_by)" + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			ps.setString(1, storageType.getType());
			ps.setInt(2, storageType.getMaxImport());
			ps.setInt(3, storageType.getMaxExport());
			ps.setDouble(4, storageType.getVaultCost());
			ps.setString(5, ColdStorage.getPlugin().getItemSerial().itemToString(storageType.getItemCost()));
			ps.setInt(6, storageType.getMaxAmountBase());
			ps.setString(7, dateString);
			ps.setString(8, player.getUniqueId().toString());
			ps.setString(9, dateString);
			ps.setString(10, player.getUniqueId().toString());

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

	public void deleteStorageType(StorageType storageType) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("DELETE FROM " + this.getName() + " WHERE type = ?");

			ps.setString(1, storageType.getType());

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

	public boolean removePermissionFromTypes(String permission, OfflinePlayer player) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean issues = false;
		List<StorageType> types = new ArrayList<StorageType>();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + ";");

			rs = ps.executeQuery();
			while (rs.next())
				types.add(new StorageType(rs.getString("type"), rs.getInt("max_export"), rs.getInt("max_import"), rs.getDouble("vault_price"), ColdStorage.getPlugin().getItemSerial().stringToItem(rs.getString("item_price")), rs.getInt("max_amount_base")));
		} catch (SQLException ex) {
			issues = true;
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
		if (!issues) for(StorageType type: types)
			if (getPermissions(type).contains(permission)) if (!removePermission(type, permission, player)) issues = true;
		return !issues;
	}

	public List<String> getPermissions(StorageType type) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean hasRecord = hasStorageType(type);
		List<String> permissions = new ArrayList<String>();
		if (hasRecord) try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + " WHERE type = ?;");

			ps.setString(1, type.getType());

			rs = ps.executeQuery();
			while (rs.next()) {
				String permissionString = rs.getString("permissions");
				String[] permissionList = permissionString.split(", ");
				for(String str: permissionList)
					permissions.add(str);
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
		return permissions;
	}

	public void addPermission(StorageType type, String permission, OfflinePlayer player) {
		Connection conn = null;
		PreparedStatement ps = null;
		List<String> permissions = getPermissions(type);
		if (!permissions.contains(permission)) permissions.add(permission);

		boolean hasRecord = hasStorageType(type);
		if (hasRecord) try {
			LocalDateTime date = LocalDateTime.now();
			String dateString = date.toString();
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("UPDATE " + this.getName() + " SET permissions = ?, modified_by = ?, updated_at = ? WHERE type = ?");

			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < permissions.size(); i++) {
				if (i > 0) sb.append(", ");
				sb.append(permissions.get(i));
			}

			ps.setString(1, sb.toString());
			ps.setString(2, player.getUniqueId().toString());
			ps.setString(3, dateString);

			ps.setString(4, type.getType());

			ps.executeUpdate();
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

	public boolean removePermission(StorageType type, String permission, OfflinePlayer player) {
		Connection conn = null;
		PreparedStatement ps = null;
		List<String> permissions = getPermissions(type);
		if (permissions.contains(permission)) permissions.remove(permission);
		boolean removed = false;

		boolean hasRecord = hasStorageType(type);
		if (hasRecord) try {
			LocalDateTime date = LocalDateTime.now();
			String dateString = date.toString();
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("UPDATE " + this.getName() + " SET permissions = ?, modified_by = ?, updated_at = ? WHERE type = ?");

			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < permissions.size(); i++) {
				if (i > 0) sb.append(", ");
				sb.append(permissions.get(i));
			}

			ps.setString(1, sb.toString());
			ps.setString(2, player.getUniqueId().toString());
			ps.setString(3, dateString);

			ps.setString(4, type.getType());

			ps.executeUpdate();
			removed = true;
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
		return removed;
	}

	public void setPermissions(StorageType type, List<String> permissions, OfflinePlayer player) {
		Connection conn = null;
		PreparedStatement ps = null;

		boolean hasRecord = hasStorageType(type);
		if (hasRecord) try {
			LocalDateTime date = LocalDateTime.now();
			String dateString = date.toString();
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("UPDATE " + this.getName() + " SET permissions = ?, modified_by = ?, updated_at = ? WHERE type = ?");

			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < permissions.size(); i++) {
				if (i > 0) sb.append(", ");
				sb.append(permissions.get(i));
			}

			ps.setString(1, sb.toString());
			ps.setString(2, player.getUniqueId().toString());
			ps.setString(3, dateString);

			ps.setString(4, type.getType());

			ps.executeUpdate();
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

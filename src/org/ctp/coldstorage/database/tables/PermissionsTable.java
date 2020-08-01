package org.ctp.coldstorage.database.tables;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.permissions.Permission;
import org.ctp.crashapi.db.Errors;
import org.ctp.crashapi.db.SQLite;
import org.ctp.crashapi.db.tables.Table;

public class PermissionsTable extends Table {

	public PermissionsTable(SQLite db) {
		super(db, "permissions", Arrays.asList("permission"));
		addColumn("permission", "varchar", "\"\"");
		addColumn("num_storage", "int", "0");
		addColumn("check_order", "int", "0");
	}

	public int getAmount(List<String> permissions) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int checkOrder = -1;
		int amount = -1;
		try {
			conn = getDb().getSQLConnection();
			for(String permission: permissions) {
				ps = conn.prepareStatement("SELECT * FROM " + getName() + " WHERE permission = '" + permission + "';");

				rs = ps.executeQuery();
				while (rs.next())
					if (rs.getInt("check_order") > checkOrder) {
						checkOrder = rs.getInt("check_order");
						amount = rs.getInt("num_storage");
					}
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
		return amount;
	}

	public List<Permission> getPermissions() {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		List<Permission> permissions = new ArrayList<Permission>();
		try {
			String query = "SELECT * FROM " + this.getName() + " ORDER BY check_order asc;";
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			while (rs.next())
				permissions.add(new Permission(rs.getString("permission"), rs.getInt("check_order"), rs.getInt("num_storage")));
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
		return permissions;
	}

	public Permission getPermission(String permission) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Permission perm = null;
		try {
			String query = "SELECT * FROM " + this.getName() + " WHERE permission LIKE ?";
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, permission);
			rs = ps.executeQuery();

			if (rs.next()) perm = new Permission(rs.getString("permission"), rs.getInt("check_order"), rs.getInt("num_storage"));
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
		return perm;
	}

	public boolean hasPermission(Permission permission) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean found = false;
		try {
			String query = "SELECT (count(*) > 0) as found FROM " + this.getName() + " WHERE permission LIKE ?";
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, permission.getPermission());
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

	public void setPermission(Permission permission) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean hasRecord = hasPermission(permission);
		if (hasRecord) try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("UPDATE " + this.getName() + " SET num_storage = ?, check_order = ? WHERE permission = ?");

			ps.setInt(1, permission.getNumStorages());
			ps.setInt(2, permission.getCheckOrder());

			ps.setString(3, permission.getPermission());

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
		else
			ColdStorage.getPlugin().getLogger().log(Level.WARNING, "Missing possible record with permission: " + permission);
		return;
	}

	public void addPermission(Permission permission) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("INSERT INTO " + this.getName() + " (permission, num_storage, check_order)" + " VALUES (?, ?, ?)");

			ps.setString(1, permission.getPermission());
			ps.setInt(2, permission.getNumStorages());
			ps.setInt(3, permission.getCheckOrder());

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

	public void deletePermission(String permission) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("DELETE FROM " + this.getName() + " WHERE permission = ?");

			ps.setString(1, permission);

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

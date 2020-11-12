package org.ctp.coldstorage.database.tables;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.storage.Chest;
import org.ctp.coldstorage.storage.Chest.ChestType;
import org.ctp.coldstorage.storage.ChestTypeRecord;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.crashapi.db.Errors;
import org.ctp.crashapi.db.SQLite;
import org.ctp.crashapi.db.tables.Table;

public class ChestTypeTable extends Table {

	public ChestTypeTable(SQLite db) {
		super(db, "chest_types", Arrays.asList("chest_unique", "storage_unique"));
		addColumn("chest_unique", "varchar", "\"\"");
		addColumn("storage_unique", "varchar", "\"\"");
		addColumn("type", "varchar", "\"\"");
	}

	public ChestType getChestType(Chest chest, Storage storage) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ChestType type = null;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + " WHERE chest_unique = '" + chest.getUnique() + "' AND storage_unique = '" + storage.getUnique() + "';");

			rs = ps.executeQuery();
			while (rs.next())
				type = ChestType.valueOf(rs.getString("type"));
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
		return type;
	}

	public List<ChestTypeRecord> getChestTypes(Chest chest) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ChestTypeRecord> chests = new ArrayList<ChestTypeRecord>();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + " WHERE chest_unique='" + chest.getUnique() + "';");

			rs = ps.executeQuery();
			while (rs.next())
				chests.add(new ChestTypeRecord(rs.getString("chest_unique"), rs.getString("storage_unique"), ChestType.valueOf(rs.getString("type"))));
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
		return chests;
	}

	public List<ChestTypeRecord> getChestTypes(Storage storage, ChestType type) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ChestTypeRecord> chests = new ArrayList<ChestTypeRecord>();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + " WHERE storage_unique = '" + storage.getUnique() + "' AND type='" + type.name() + "';");

			rs = ps.executeQuery();
			while (rs.next())
				chests.add(new ChestTypeRecord(rs.getString("chest_unique"), rs.getString("storage_unique"), type));
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
		return chests;
	}

	public List<ChestTypeRecord> getChestTypes(ChestType type) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ChestTypeRecord> chests = new ArrayList<ChestTypeRecord>();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + " WHERE type='" + type.name() + "';");

			rs = ps.executeQuery();
			while (rs.next())
				chests.add(new ChestTypeRecord(rs.getString("chest_unique"), rs.getString("storage_unique"), type));
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
		return chests;
	}

	public List<String> getChests(Storage storage, ChestType type) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> chests = new ArrayList<String>();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName() + " WHERE storage_unique = '" + storage.getUnique() + "' AND type='" + type.name() + "';");

			rs = ps.executeQuery();
			while (rs.next())
				chests.add(rs.getString("chest_unique"));
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
		return chests;
	}

	public boolean hasChestType(Chest chest, Storage storage) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean found = false;
		try {
			String query = "SELECT (count(*) > 0) as found FROM " + this.getName() + " WHERE chest_unique = ? AND storage_unique = ?";
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, chest.getUnique());
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

	public void setChestType(Chest chest, Storage storage, ChestType type) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean hasRecord = hasChestType(chest, storage);
		if (hasRecord) try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("UPDATE " + this.getName() + " SET type = ? WHERE chest_unique = ? AND storage_unique = ?");

			ps.setString(1, type.name());

			ps.setString(2, chest.getUnique());
			ps.setString(3, storage.getUnique());

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
			ColdStorage.getPlugin().getLogger().log(Level.WARNING, "Missing possible record with chest type: " + type.name());
		return;
	}

	public void addChestType(Chest chest, Storage storage, ChestType type) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("INSERT INTO " + this.getName() + " (chest_unique, storage_unique, type)" + " VALUES (?, ?, ?)");

			ps.setString(1, chest.getUnique());
			ps.setString(2, storage.getUnique());
			ps.setString(3, type.name());

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

	public void deleteChestType(Chest chest, String storageUnique) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("DELETE FROM " + this.getName() + " WHERE chest_unique = ? AND storage_unique = ?");

			ps.setString(1, chest.getUnique());
			ps.setString(2, storageUnique);

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

	public void deleteChestType(Chest chest, Storage storage) {
		deleteChestType(chest, storage.getUnique());
	}

}

package org.ctp.coldstorage.database.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.ctp.coldstorage.database.Errors;
import org.ctp.coldstorage.database.SQLite;
import org.ctp.coldstorage.storage.Chest;
import org.ctp.coldstorage.utils.LocationUtils;

public class ChestTable extends Table{

	public ChestTable(SQLite db) {
		super(db, "chests", Arrays.asList("unique"));
		addColumn("unique", "varchar", "\"\"");
		addColumn("player_unique", "varchar", "\"\"");
		addColumn("location_one", "varchar", "\"\"");
		addColumn("location_two", "varchar", "\"\"");
	}
	
	public List<Chest> getChests(OfflinePlayer player) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Chest> chests = new ArrayList<Chest>();
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName()
					+ " WHERE player_unique = '" + player.getUniqueId().toString() + "';");

			rs = ps.executeQuery();
			while (rs.next()) {
				Location loc = LocationUtils.stringToLocation(rs.getString("location_one"));
				if(rs.getString("location_two") != null || rs.getString("location_two").equals("")) {
					Location locTwo = LocationUtils.stringToLocation(rs.getString("location_two"));
					chests.add(new Chest(rs.getString("unique"), player, loc, locTwo));
				} else {
					chests.add(new Chest(rs.getString("unique"), player, loc));
				}
			}
		} catch (SQLException ex) {
			getDb().getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
					ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				getDb().getPlugin().getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return chests;
	}
	
	public Chest getChest(String unique) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Chest chest = null;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName()
					+ " WHERE `unique` = '" + unique + "';");

			rs = ps.executeQuery();
			while (rs.next()) {
				Location loc = LocationUtils.stringToLocation(rs.getString("location_one"));
				if(rs.getString("location_two") != null || rs.getString("location_two").equals("")) {
					Location locTwo = LocationUtils.stringToLocation(rs.getString("location_two"));
					chest = new Chest(unique, Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("player_unique"))), loc, locTwo);
				} else {
					chest = new Chest(unique, Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("player_unique"))), loc);
				}
			}
		} catch (SQLException ex) {
			getDb().getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
					ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				getDb().getPlugin().getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return chest;
	}
	
	public Chest getChest(Location loc) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Chest chest = null;
		String location = LocationUtils.locationToString(loc);
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + getName()
					+ " WHERE location_one = '" + location + "' OR location_two = '" + location + "';");

			rs = ps.executeQuery();
			while (rs.next()) {
				String unique = rs.getString("unique");
				Location locOne = LocationUtils.stringToLocation(rs.getString("location_one"));
				if(rs.getString("location_two") != null || rs.getString("location_two").equals("")) {
					Location locTwo = LocationUtils.stringToLocation(rs.getString("location_two"));
					chest = new Chest(unique, Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("player_unique"))), locOne, locTwo);
				} else {
					chest = new Chest(unique, Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("player_unique"))), locOne);
				}
			}
		} catch (SQLException ex) {
			getDb().getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
					ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				getDb().getPlugin().getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return chest;
	}
	
	public boolean hasChest(String unique) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean found = false;
		try {
			String query = "SELECT (count(*) > 0) as found FROM " + this.getName() + " WHERE `unique` = ?";
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, unique);
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
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				getDb().getPlugin().getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return found;
	}
	
	public boolean hasChest(Location loc) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean found = false;
		String location = LocationUtils.locationToString(loc);
		try {
			String query = "SELECT (count(*) > 0) as found FROM " + this.getName() + " WHERE location_one = ? OR location_two = ?";
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, location);
			ps.setString(2, location);
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
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				getDb().getPlugin().getLogger().log(Level.SEVERE,
						Errors.sqlConnectionClose(), ex);
			}
		}
		return found;
	}
	
	public void setChest(Chest chest) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean hasRecord = hasChest(chest.getUnique());
		if(hasRecord) {
			try {
				conn = getDb().getSQLConnection();
				ps = conn.prepareStatement("UPDATE " + this.getName() + " SET location_one = ?, location_two = ? WHERE `unique` = ?");
	
				ps.setString(1, LocationUtils.locationToString(chest.getLoc())); 
				ps.setString(2, LocationUtils.locationToString(chest.getDoubleLoc()));
				
				ps.setString(3, chest.getUnique());
				
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
			getDb().getPlugin().getLogger().log(Level.WARNING, "Missing possible record with chest unique: " + chest.getUnique());
		}
		return;
	}
	
	public boolean addChest(Chest chest) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean error = false;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("INSERT INTO " + this.getName() + 
					" (`unique`, player_unique, location_one, location_two)"
					+ " VALUES (?, ?, ?, ?)");

			ps.setString(1, chest.getUnique());
			ps.setString(2, chest.getPlayer().getUniqueId().toString());
			ps.setString(3, LocationUtils.locationToString(chest.getLoc())); 
			ps.setString(4, LocationUtils.locationToString(chest.getDoubleLoc()));
			
			ps.execute();
		} catch (SQLException ex) {
			getDb().getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
					ex);
			error = true;
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
		return !error;
	}
	
	public boolean deleteChest(Chest chest) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean error = false;
		try {
			conn = getDb().getSQLConnection();
			ps = conn.prepareStatement("DELETE FROM " + this.getName() + 
					" WHERE `unique` = ?");
			
			ps.setString(1, chest.getUnique());
			
			ps.execute();
		} catch (SQLException ex) {
			getDb().getPlugin().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(),
					ex);
			error = true;
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
		return !error;
	}

}

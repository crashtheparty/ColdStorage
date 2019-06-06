package org.ctp.coldstorage.storage;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;

public class Chest {

	private String unique;
	private Location loc, doubleLoc;
	private OfflinePlayer player;
	
	public Chest(String unique, OfflinePlayer player, Location loc) {
		setUnique(unique);
		setPlayer(player);
		setLoc(loc);
	}
	
	public Chest(String unique, OfflinePlayer player, Location loc, Location doubleLoc) {
		setUnique(unique);
		setPlayer(player);
		setLoc(loc);
		setDoubleLoc(doubleLoc);
	}
	public Chest(Location loc) {
		setLoc(loc);
	}
	public Chest(Location loc, Location doubleLoc) {
		setLoc(loc);
		setDoubleLoc(doubleLoc);
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public Location getDoubleLoc() {
		return doubleLoc;
	}

	public void setDoubleLoc(Location doubleLoc) {
		this.doubleLoc = doubleLoc;
	}
	
	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}
	
	public void toggle(Storage storage, ChestType type, Player player) {
		if(DatabaseUtils.getChestType(storage, this) == null) {
			if(storage.getStorageType() != null) {
				if(type == ChestType.EXPORT && storage.getStorageType().getMaxExport() <= DatabaseUtils.getChestTypes(storage, ChestType.EXPORT).size()) {
					if(storage.getStorageType().getMaxExport() == 0) {
						ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.no_export"));
						return;
					}
					ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.too_many_export"));
					return;
				}
				if(type == ChestType.IMPORT && storage.getStorageType().getMaxImport() <= DatabaseUtils.getChestTypes(storage, ChestType.IMPORT).size()) {
					if(storage.getStorageType().getMaxImport() == 0) {
						ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.no_import"));
						return;
					}
					ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.too_many_import"));
					return;
				}
				DatabaseUtils.addChestType(storage, this, type);
			}
		} else {
			DatabaseUtils.deleteChestType(storage, this);
		}
	}

	public enum ChestType{
		EXPORT(), IMPORT();
	}
}

package org.ctp.coldstorage.inventory;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface ColdStorageInventory {

	public OfflinePlayer getPlayer();
	
	public void setPlayer(OfflinePlayer player);
	
	public OfflinePlayer getAdmin();
	
	public void setAdmin(OfflinePlayer player);
	
	public Player getShow();
	
	public void setShow(Player player);
	
	public Inventory getInventory();
	
	public void setInventory();
	
	public void close(boolean external);
	
	public HashMap<String, Object> getCodes();
	
	public HashMap<String, Object> getCodes(String string, Object object);
	
	public HashMap<String, Object> getCodes(HashMap<String, Object> objects);
	
	public boolean isOpening();
	
	public Inventory open(Inventory inv);
}
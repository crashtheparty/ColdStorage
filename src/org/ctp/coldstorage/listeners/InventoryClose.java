package org.ctp.coldstorage.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.ctp.coldstorage.handlers.ColdStorageInventory;
import org.ctp.coldstorage.utils.InventoryUtilities;

public class InventoryClose implements Listener{
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = null;
		if (event.getPlayer() instanceof Player) {
			player = (Player) event.getPlayer();
		} else {
			return;
		}
		ColdStorageInventory csInv = InventoryUtilities.getInventory(player);
		
		if(csInv != null) {
			if(csInv.isEditing())
				return;
			if(!csInv.isOpening()) {
				InventoryUtilities.removeInventory(player);
			}
		}
	}

}

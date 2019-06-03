package org.ctp.coldstorage.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class InventoryClose implements Listener{

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = null;
		if (event.getPlayer() instanceof Player) {
			player = (Player) event.getPlayer();
		} else {
			return;
		}
		ColdStorageInventory csInv = InventoryUtils.getInventory(player);
		
		if(csInv != null) {
			if(csInv instanceof Anvilable && ((Anvilable) csInv).isEditing()){
				return;
			}
			if(!csInv.isOpening()) {
				InventoryUtils.removeInventory(player);
			}
		}
	}
}

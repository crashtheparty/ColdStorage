package org.ctp.coldstorage.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.inventory.ColdStorageData;
import org.ctp.crashapi.inventory.InventoryData;

public class InventoryClose implements Listener {

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = null;
		if (event.getPlayer() instanceof Player) player = (Player) event.getPlayer();
		else
			return;
		InventoryData csInv = ColdStorage.getPlugin().getInventory(player);

		if (csInv != null) {
			if (csInv instanceof Anvilable && ((Anvilable) csInv).willEdit()) return;
			if (csInv instanceof ColdStorageData && !((ColdStorageData) csInv).isOpening()) ColdStorage.getPlugin().removeInventory(csInv);
		}
	}
}

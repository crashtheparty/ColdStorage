package org.ctp.coldstorage.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class ChatMessage implements Listener{

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		ColdStorageInventory inv = InventoryUtils.getInventory(player);
		if (inv != null) {
			event.setCancelled(true);
			Bukkit.getScheduler().runTask(ColdStorage.getPlugin(), new Runnable() {
				public void run() {
					if (inv instanceof Anvilable) {
						Anvilable anvil = (Anvilable) inv;
						if (anvil.isChoice()) {
							anvil.setChoice(event.getMessage());
						} else if (anvil.isEditing()) {
							anvil.setItemName(event.getMessage());
						}
					}
				}
			});
		}
	}
}

package org.ctp.coldstorage.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.crashapi.inventory.InventoryData;

public class ChatMessage implements Listener{

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		InventoryData inv = ColdStorage.getPlugin().getInventory(player);
		if (inv != null) {
			event.setCancelled(true);
			Bukkit.getScheduler().runTask(ColdStorage.getPlugin(), new Runnable() {
				@Override
				public void run() {
					if (inv instanceof Anvilable) {
						Anvilable anvil = (Anvilable) inv;
						if (anvil.isChoice()) anvil.setChoice(event.getMessage());
						else if (anvil.willEdit()) anvil.setItemName(event.getMessage());
					}
				}
			});
		}
	}
}

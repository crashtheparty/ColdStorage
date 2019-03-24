package org.ctp.coldstorage.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.ctp.coldstorage.handlers.ColdStorageInventory;
import org.ctp.coldstorage.handlers.ColdStorageInventory.Screen;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.InventoryUtilities;
import org.ctp.coldstorage.utils.Storage;

public class ChatMessage implements Listener{
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		ColdStorageInventory inv = InventoryUtilities.getInventory(player);
		if(inv != null) {
			if(inv.getScreen() == Screen.EDIT) {
				event.setCancelled(true);
				String chat = event.getMessage();
				int order = 0;
				try {
					order = Integer.parseInt(chat);
				} catch (Exception e) {
					ChatUtils.sendMessage(player, "Entered order not a number - set to 0.");
				}
				Storage storage = Storage.getStorage(inv.getPlayer(), inv.getUnique());
				if(storage != null) {
					storage.setOrderBy(order);
					storage.updateStorage(player);
					ChatUtils.sendMessage(player, "Updated the order.");
				} else {
					ChatUtils.sendMessage(player, "Issue with storages - none selected.");
				}
				inv.listEditColdStorage();
			} else {
				InventoryUtilities.removeInventory(player);
			}
		}
	}

}

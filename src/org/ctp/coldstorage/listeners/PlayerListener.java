package org.ctp.coldstorage.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.ctp.coldstorage.storage.Chest;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.StorageUtils;

public class PlayerListener implements Listener{

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(StorageUtils.gettingNewChest(event.getPlayer())) {
				if(event.getClickedBlock().getState() instanceof org.bukkit.block.Chest) {
					if(event.useInteractedBlock() == Event.Result.DENY && event.useItemInHand() == Event.Result.DENY) {
						ChatUtils.sendMessage(event.getPlayer(), ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.interact_event_cancelled"));
						return;
					}
					event.setCancelled(true);
					if(StorageUtils.setNewChest(event.getPlayer(), event.getClickedBlock())) {
						ChatUtils.sendMessage(event.getPlayer(), ChatUtils.getMessage(ChatUtils.getCodes(), "listeners.new_chest"));
					} else {
						ChatUtils.sendMessage(event.getPlayer(), ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.chest_exists"));
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryOpen(InventoryOpenEvent event) {
		if(event.isCancelled()) return;
		if(event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();
			Location loc = event.getInventory().getLocation();
			if(loc != null) {
				Chest chest = DatabaseUtils.getChest(loc);
				if(chest != null) {
					ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes("%owned_player%", chest.getPlayer().getName()), 
							"listeners.open_inventory"));
				}
			}
		}
	}

}

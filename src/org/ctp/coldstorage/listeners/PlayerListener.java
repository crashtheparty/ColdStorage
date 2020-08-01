package org.ctp.coldstorage.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.storage.Chest;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.StorageUtils;
import org.ctp.crashapi.utils.ChatUtils;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) if (StorageUtils.gettingNewChest(event.getPlayer())) if (event.getClickedBlock().getState() instanceof org.bukkit.block.Chest) {
			if (event.useInteractedBlock() == Event.Result.DENY && event.useItemInHand() == Event.Result.DENY) {
				Chatable.get().sendMessage(event.getPlayer(), Chatable.get().getMessage(ChatUtils.getCodes(), "exceptions.interact_event_cancelled"));
				return;
			}
			event.setCancelled(true);
			if (StorageUtils.setNewChest(event.getPlayer(), event.getClickedBlock())) Chatable.get().sendMessage(event.getPlayer(), Chatable.get().getMessage(ChatUtils.getCodes(), "listeners.new_chest"));
			else
				Chatable.get().sendMessage(event.getPlayer(), Chatable.get().getMessage(ChatUtils.getCodes(), "exceptions.chest_exists"));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.isCancelled()) return;
		if (event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();
			Location loc = event.getInventory().getLocation();
			if (loc != null) {
				Chest chest = DatabaseUtils.getChest(loc);
				if (chest != null) {
					HashMap<String, Object> codes = ChatUtils.getCodes();
					codes.put("%owned_player%", chest.getPlayer().getName());
					Chatable.get().sendMessage(player, Chatable.get().getMessage(codes, "listeners.open_inventory"));
				}
			}
		}
	}

}

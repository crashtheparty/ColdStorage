package org.ctp.coldstorage.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.StorageUtils;
import org.ctp.crashapi.utils.ChatUtils;

public class BlockListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		Bukkit.getScheduler().runTaskLater(ColdStorage.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if (event.isCancelled()) return;
				Block block = event.getBlock();
				InventoryHolder invHolder = StorageUtils.getChestInventory(block);
				if (invHolder != null && invHolder instanceof DoubleChest) {
					DoubleChest doubleChest = (DoubleChest) invHolder;
					Location loc = null;
					Location locTwo = block.getLocation();

					for(BlockFace direction: StorageUtils.getDirections()) {
						Block b = block.getRelative(direction);
						InventoryHolder i = StorageUtils.getChestInventory(b);
						if (i != null && i instanceof DoubleChest) {
							DoubleChest doubleChestTwo = (DoubleChest) i;
							if (doubleChestTwo.getLeftSide().equals(doubleChest.getLeftSide())) {
								loc = b.getLocation();
								break;
							}
						}
					}
					if (loc != null && DatabaseUtils.hasChest(loc.getBlock())) DatabaseUtils.addDoubleChest(loc, locTwo);
				}
			}
		}, 2l);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		if (event.getBlock().getType() == Material.CHEST && DatabaseUtils.hasChest(event.getBlock())) if (StorageUtils.gettingNewChest(event.getPlayer())) {
			if (DatabaseUtils.getChest(event.getBlock().getLocation()).getPlayer().getUniqueId().equals(event.getPlayer().getUniqueId()) || event.getPlayer().hasPermission("coldstorage.remove_chests")) {
				if (StorageUtils.deleteChest(event.getPlayer(), event.getBlock())) Chatable.get().sendMessage(event.getPlayer(), Chatable.get().getMessage(ChatUtils.getCodes(), "listeners.break_chest"));
				else
					event.setCancelled(true);
			} else {
				event.setCancelled(true);
				Chatable.get().sendMessage(event.getPlayer(), Chatable.get().getMessage(ChatUtils.getCodes(), "exceptions.not_your_chest"));
			}
		} else {
			event.setCancelled(true);
			Chatable.get().sendMessage(event.getPlayer(), Chatable.get().getMessage(ChatUtils.getCodes(), "exceptions.invalid_block_break"));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		for(int i = event.blockList().size() - 1; i >= 0; i--) {
			Block block = event.blockList().get(i);
			if (block.getType() == Material.CHEST && DatabaseUtils.hasChest(block)) event.blockList().remove(i);
		}
	}
}

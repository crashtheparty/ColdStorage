package org.ctp.coldstorage.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.InventoryHolder;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.StorageUtils;

public class BlockListener implements Listener{
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Bukkit.getScheduler().runTaskLater(ColdStorage.getPlugin(), new Runnable() {
			@Override
			public void run() {
				Block block = event.getBlock();
				InventoryHolder invHolder = StorageUtils.getChestInventory(block);
				if(invHolder != null && invHolder instanceof DoubleChest) {
					DoubleChest doubleChest = (DoubleChest) invHolder;
					Location loc = null;
					Location locTwo = block.getLocation();
					
					for(BlockFace direction : StorageUtils.getDirections()) {
						Block b = block.getRelative(direction);
						InventoryHolder i = StorageUtils.getChestInventory(b);
						if(i != null && i instanceof DoubleChest) {
							DoubleChest doubleChestTwo = (DoubleChest) i;
							if(doubleChestTwo.getLeftSide().equals(doubleChest.getLeftSide())) {
								loc = b.getLocation();
								break;
							}
						}
					}
					if(loc != null && DatabaseUtils.hasChest(loc.getBlock())) {
						DatabaseUtils.addDoubleChest(loc, locTwo);
					}
				}
			}
		}, 2l);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.getBlock().getType() == Material.CHEST && DatabaseUtils.hasChest(event.getBlock())) {
			if(StorageUtils.gettingNewChest(event.getPlayer())) {
				if(DatabaseUtils.getChest(event.getBlock().getLocation()).getPlayer().getUniqueId().equals(event.getPlayer().getUniqueId())
						|| event.getPlayer().hasPermission("coldstorage.remove_chests")){
					if(StorageUtils.deleteChest(event.getPlayer(), event.getBlock())) {
						ChatUtils.sendMessage(event.getPlayer(), ChatUtils.getMessage(ChatUtils.getCodes(), "listeners.break_chest"));
					} else {
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
					ChatUtils.sendMessage(event.getPlayer(), ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.not_your_chest"));
				}
			} else {
				event.setCancelled(true);
				ChatUtils.sendMessage(event.getPlayer(), ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.invalid_block_break"));
			}
		}
	}
	
	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		for(int i = event.blockList().size() - 1; i >= 0; i--) {
			Block block = event.blockList().get(i);
			if(block.getType() == Material.CHEST && DatabaseUtils.hasChest(event.getBlock())) {
				event.blockList().remove(i);
			}
		}
	}
}

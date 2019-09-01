package org.ctp.coldstorage.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.inventory.storage.ListStorage;
import org.ctp.coldstorage.inventory.storage.ViewStorage;
import org.ctp.coldstorage.storage.ChestTypeRecord;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.coldstorage.storage.Chest;
import org.ctp.coldstorage.storage.Chest.ChestType;
import org.ctp.coldstorage.utils.config.ItemSerialization;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class StorageUtils {

	private static List<BlockFace> DIRECTIONS = Arrays.asList(BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST);
	private static List<OfflinePlayer> CHEST_PLAYERS = new ArrayList<OfflinePlayer>();
	
	public static void setImportChests() {
		List<ChestTypeRecord> chests = DatabaseUtils.getChestTypes(ChestType.IMPORT);
		for(ChestTypeRecord record : chests) {
			Chest chest = DatabaseUtils.getChest(record.getChestUUID());
			OfflinePlayer player = chest.getPlayer();
			Storage storage = (Storage) DatabaseUtils.getCache(player, record.getStorageUUID(), false);
			if(storage == null) {
				DatabaseUtils.deleteChestType(storage, chest);
				continue;
			}
			if(storage.getStorageType() == null) continue;
			ItemStack item = ItemSerialization.dataToItem(storage.getMaterial(), 1, storage.getMeta());
			if(chest.getLoc().getWorld().isChunkLoaded(chest.getLoc().getBlockX() / 16, chest.getLoc().getBlockZ() / 16)) {
				if(chest.getLoc().getBlock().getState() instanceof org.bukkit.block.Chest) {
					Inventory inv = ((org.bukkit.block.Chest) chest.getLoc().getBlock().getState()).getInventory();
					if(inv.containsAtLeast(item, 1) && storage.getStoredAmount() < storage.getStorageType().getMaxAmountBase()) {
						if(inv.removeItem(item).isEmpty()) {
							storage.setStoredAmount(storage.getStoredAmount() + 1);
							DatabaseUtils.updateCache(player, storage);
							ColdStorageInventory openedInv = InventoryUtils.getInventory(player);
							if(openedInv instanceof ViewStorage) {
								ViewStorage viewStorage = (ViewStorage) openedInv;
								if(viewStorage.getStorage().getUnique().equals(storage.getUnique())) {
									viewStorage.setStorage(storage);
									openedInv.setInventory();
								}
							}
							if (openedInv instanceof ListStorage) {
								ListStorage listStorage = (ListStorage) openedInv;
								listStorage.updateStorage(storage);
							}
						}
					}
				}
			}
		}
	}
	
	public static void setExportChests() {
		List<ChestTypeRecord> chests = DatabaseUtils.getChestTypes(ChestType.EXPORT);
		for(ChestTypeRecord record : chests) {
			Chest chest = DatabaseUtils.getChest(record.getChestUUID());
			OfflinePlayer player = chest.getPlayer();
			Storage storage = (Storage) DatabaseUtils.getCache(player, record.getStorageUUID(), false);
			if(storage == null) {
				DatabaseUtils.deleteChestType(record.getStorageUUID(), chest);
				continue;
			}
			if(storage.getStorageType() == null) continue;
			if(chest.getLoc().getBlock().getState() instanceof org.bukkit.block.Chest) {
				if(chest.getLoc().getWorld().isChunkLoaded(chest.getLoc().getBlockX() / 16, chest.getLoc().getBlockZ() / 16)) {
					Inventory inv = ((org.bukkit.block.Chest) chest.getLoc().getBlock().getState()).getInventory();
					if(storage.getStoredAmount() > 0) {
						if(inv.addItem(ItemSerialization.dataToItem(storage.getMaterial(), 1, storage.getMeta())).isEmpty()) {
							storage.setStoredAmount(storage.getStoredAmount() - 1);
							DatabaseUtils.updateCache(player, storage);
							ColdStorageInventory openedInv = InventoryUtils.getInventory(player);
							if(openedInv instanceof ViewStorage) {
								ViewStorage viewStorage = (ViewStorage) openedInv;
								if(viewStorage.getStorage().getUnique().equals(storage.getUnique())) {
									viewStorage.setStorage(storage);
									openedInv.setInventory();
								}
							}
							if (openedInv instanceof ListStorage) {
								ListStorage listStorage = (ListStorage) openedInv;
								listStorage.updateStorage(storage);
							}
						}
					}
				}
			}
		}
	}
	
	public static void allowNewChest(Player player) {
		if(!CHEST_PLAYERS.contains(player)) {
			CHEST_PLAYERS.add(player);
		}
	}
	
	public static boolean gettingNewChest(Player player) {
		return CHEST_PLAYERS.contains(player);
	}
	
	public static InventoryHolder getChestInventory(Block b) {
		if(b.getState() instanceof org.bukkit.block.Chest) {
			return ((org.bukkit.block.Chest) b.getState()).getInventory().getHolder();
		}
		return null;
	}
	
	public static boolean deleteChest(Player player, Block block) {
		CHEST_PLAYERS.remove(player);
		if(!DatabaseUtils.hasChest(block)) {
			ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.missing_chest"));
			return false;
		}
		
		org.ctp.coldstorage.storage.Chest chest = DatabaseUtils.getChest(block.getLocation());
		if(chest != null) {
			List<ChestTypeRecord> chestTypes = DatabaseUtils.getChestTypes(chest);
			if(chestTypes.size() > 0) {
				ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.has_chest_types"));
				return false;
			}
			if(DatabaseUtils.deleteChest(chest)) {
				return true;
			}
		}
		ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.chest_delete"));
		return false;
	}
	
	public static boolean setNewChest(Player player, Block block) {
		CHEST_PLAYERS.remove(player);
		if(DatabaseUtils.hasChest(block)) {
			return false;
		}
		InventoryHolder invHolder = getChestInventory(block);
		Location loc = block.getLocation();
		Location locTwo = null;
		if(invHolder != null && invHolder instanceof DoubleChest) {
			DoubleChest doubleChest = (DoubleChest) invHolder;
			
			for(BlockFace direction : DIRECTIONS) {
				Block b = block.getRelative(direction);
				InventoryHolder i = getChestInventory(b);
				if(i != null && i instanceof DoubleChest) {
					DoubleChest doubleChestTwo = (DoubleChest) i;
					if(doubleChestTwo.getLeftSide().equals(doubleChest.getLeftSide())) {
						locTwo = b.getLocation();
						break;
					}
				}
			}
		}
		org.ctp.coldstorage.storage.Chest chest = null;
		if(locTwo == null) {
			chest = new org.ctp.coldstorage.storage.Chest(UUID.randomUUID().toString(), player, loc);
		} else {
			chest = new org.ctp.coldstorage.storage.Chest(UUID.randomUUID().toString(), player, loc, locTwo);
		}
		return DatabaseUtils.addChest(chest);
	}
	
	public static List<BlockFace> getDirections(){
		return DIRECTIONS;
	}
}

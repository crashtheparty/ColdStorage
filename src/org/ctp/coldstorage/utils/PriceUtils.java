package org.ctp.coldstorage.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.storage.Cache;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.config.ItemSerialization;

public class PriceUtils {

	public static String getStringCost(StorageType type) {
		if(ColdStorage.getPlugin().getConfiguration().getMainConfig().getBoolean("vault")) {
			if(type.getVaultCost() == 0) {
				return ChatUtils.getMessage(ChatUtils.getCodes(), "info.free");
			}
			return "" + type.getVaultCost();
		}
		if(type.getItemCost().getType() == Material.AIR) {
			return ChatUtils.getMessage(ChatUtils.getCodes(), "info.free");
		}
		return "" + ItemSerialization.itemToString(type.getItemCost());
	}
	
	public static boolean takeMoney(Player player, Cache cache, StorageType type) {
		if(type == null) return false;
		if(ColdStorage.getPlugin().getConfiguration().getMainConfig().getBoolean("vault")) {
			if(ColdStorage.getEconomy() != null) {
				if(ColdStorage.getEconomy().getBalance(player) >= type.getVaultCost()) {
					ColdStorage.getEconomy().withdrawPlayer(player, type.getVaultCost());
					Storage storage = new Storage(cache.getPlayer(), cache.getUnique(), cache.getMaterial(), cache.getMeta(), cache.getStorageTypeString(),
							cache.getName(), 0, 0, true);
					DatabaseUtils.updateCache(player, storage);
					return true;
				}
			}
		} else {
			if(hasItems(player, type.getItemCost())) {
				player.getInventory().removeItem(type.getItemCost());
				Storage storage = new Storage(cache.getPlayer(), cache.getUnique(), cache.getMaterial(), cache.getMeta(), cache.getStorageTypeString(),
						cache.getName(), 0, 0, true);
				DatabaseUtils.updateCache(player, storage);
				return true;
			}
		}
		return false;
	}
	
	private static boolean hasItems(Player player, ItemStack item) {
		Inventory inv = player.getInventory();
		Material material = item.getType();
		String metadata = ItemSerialization.itemToData(item);
		int toRemove = item.getAmount();
		
		for(int i = 0; i < 36; i++) {
			ItemStack invItem = inv.getItem(i);
			if(invItem == null) {
				
			}else if(invItem.getType().equals(material)) {
				if(metadata.equals(ItemSerialization.itemToData(invItem))) {
					toRemove -= invItem.getAmount();
				}
			}
			if(toRemove <= 0) break;
		}
		return toRemove <= 0;
	}
	
}

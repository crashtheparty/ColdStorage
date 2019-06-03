package org.ctp.coldstorage.utils.inventory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.config.ItemSerialization;

public class InventoryUtils {
	private static Map<String, ColdStorageInventory> INVENTORIES = new HashMap<String, ColdStorageInventory>();
	
	public static int maxAddToInventory(Player player, ItemStack item) {
		int add = 0;
		
		Material type = item.getType();
		
		Inventory inv = player.getInventory();
		for(int i = 0; i < 36; i++) {
			ItemStack invItem = inv.getItem(i);
			if(invItem == null) {
				add += type.getMaxStackSize();
			}else if(invItem.getType().equals(type)) {
				if(ItemSerialization.itemToData(item).equals(ItemSerialization.itemToData(invItem))) {
					add += (type.getMaxStackSize() - invItem.getAmount());
				}
			}else if(invItem.getType().equals(Material.AIR)) {
				add += type.getMaxStackSize();
			}
		}
		return add;
	}
	
	public static int maxRemoveFromInventory(Storage storage, Player player, ItemStack item) {
		if(storage.getStorageType() == null) {
			ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.missing_storage_type"));
			return 0;
		}
		if(storage.getStoredAmount() >= storage.getStorageType().getMaxAmountBase()) {
			return 0;
		}
		int remove = 0;
		
		Material type = item.getType();
		
		Inventory inv = player.getInventory();
		for(int i = 0; i < 36; i++) {
			ItemStack invItem = inv.getItem(i);
			if(invItem == null) {
				
			}else if(invItem.getType().equals(type)) {
				if(ItemSerialization.itemToData(item).equals(ItemSerialization.itemToData(invItem))) {
					remove += invItem.getAmount();
				}
			}
		}
		if(remove + storage.getStoredAmount() > storage.getStorageType().getMaxAmountBase()){
			remove = storage.getStorageType().getMaxAmountBase() - storage.getStoredAmount();
		}
		return remove;
	}

	public static int addItems(Player player, ItemStack itemAdd) {
		Inventory inv = player.getInventory();
		int amount = itemAdd.getAmount();
		Material material = itemAdd.getType();
		String metadata = ItemSerialization.itemToData(itemAdd);
		
		for(int i = 0; i < 36; i++) {
			int addItem = material.getMaxStackSize();
			ItemStack item = inv.getItem(i);
			if(item == null) {
				if(addItem > amount) addItem = amount;
				player.getInventory().setItem(i, ItemSerialization.dataToItem(material, addItem, metadata));
				amount -= addItem;
			}else if(item.getType().equals(material)) {
				if(ItemSerialization.itemToData(itemAdd).equals(ItemSerialization.itemToData(item))) {
					int setItem = item.getAmount();
					if(addItem > amount + setItem) addItem = amount + setItem;
					player.getInventory().setItem(i, ItemSerialization.dataToItem(material, addItem, metadata));
					amount = amount - addItem + setItem;
				}
			}else if(item.getType().equals(Material.AIR)) {
				if(addItem > amount) addItem = amount;
				player.getInventory().setItem(i, ItemSerialization.dataToItem(material, material.getMaxStackSize(), metadata));
				amount -= addItem;
			}
			if(amount <= 0) break;
		}
		return amount;
	}

	public static ColdStorageInventory getInventory(OfflinePlayer player) {
		String id = player.getUniqueId().toString();
		if(INVENTORIES.containsKey(id)) {
			return INVENTORIES.get(id);
		}
		return null;
	}
	
	public static void addInventory(OfflinePlayer player, ColdStorageInventory inv) {
		String id = player.getUniqueId().toString();
		INVENTORIES.put(id, inv);
		inv.setInventory();
	}
	
	public static void removeInventory(OfflinePlayer player) {
		INVENTORIES.remove(player.getUniqueId().toString());
	}
	
	public void resetInventories() {
		Iterator<Entry<String, ColdStorageInventory>> iterator = INVENTORIES.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, ColdStorageInventory> next = iterator.next();
			INVENTORIES.remove(next.getKey());
		}
	}
}

package org.ctp.coldstorage.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.handlers.ColdStorageInventory;
import org.ctp.coldstorage.utils.config.ItemSerialization;

public class InventoryUtilities {
	
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
	
	public static int maxRemoveFromInventory(Player player, ItemStack item) {
		int remove = 0;
		
		Material type = item.getType();
		
		Inventory inv = player.getInventory();
		for(int i = 0; i < 36; i++) {
			ItemStack invItem = inv.getItem(i);
			if(invItem == null) {
				
			}else if(invItem.getType().equals(type)) {
				if(ItemSerialization.itemToData(item).equals(ItemSerialization.itemToData(invItem))) {
					remove += item.getAmount();
				}
			}
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
	
	public static void addInventory(OfflinePlayer player) {
		ColdStorageInventory inv = new ColdStorageInventory(player);
		String id = player.getUniqueId().toString();
		INVENTORIES.put(id, inv);
		inv.listColdStorage();
	}
	
	public static void removeInventory(OfflinePlayer player) {
		INVENTORIES.remove(player.getUniqueId().toString());
	}
	
	public static void addInventory(OfflinePlayer admin, OfflinePlayer player) {
		ColdStorageInventory inv = new ColdStorageInventory(player, admin);
		String id = admin.getUniqueId().toString();
		INVENTORIES.put(id, inv);
		inv.listColdStorage();
	}

}

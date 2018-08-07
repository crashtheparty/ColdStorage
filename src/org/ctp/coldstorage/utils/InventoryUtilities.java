package org.ctp.coldstorage.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.database.tables.StorageTable;
import org.ctp.coldstorage.database.tables.Table;
import org.ctp.coldstorage.utils.config.ConfigUtilities;

public class InventoryUtilities {
	
	private final static int PAGING = 36;
	
	public static void listColdStorage(Player player) {
		listColdStorage(player, 1);
	}
	
	public static void listColdStorage(Player player, int page) {		
		StorageList storageList = StorageList.getList(player);
		if(storageList == null) {
			storageList = new StorageList(player);
		}
		if(page < 1) page = 1;
		storageList.setPage(page);
		
		List<Storage> storages = storageList.getStorages();
		if(storages == null) {
			storages = new ArrayList<Storage>();
		}
		Inventory inv;
		
		if(PAGING > storages.size() && page == 1) {
			inv = Bukkit.createInventory(null, 54, "Cold Storage List");
		} else {
			inv = Bukkit.createInventory(null, 54, "Cold Storage List Page " + page);
		}
		for(int i = 0; i < PAGING; i++) {
			int storageNum = i + (PAGING * (page - 1));
			if(storages.size() <= storageNum) break;
			Storage storage = storages.get(storageNum);
			
			ItemStack storageItem = new ItemStack(storage.getMaterial());
			ItemMeta storageItemMeta = storageItem.getItemMeta();
			storageItemMeta.setDisplayName(ChatColor.GOLD + "Type: " + ChatColor.DARK_AQUA + "" + storage.getMaterial().name());
			storageItemMeta.setLore(Arrays.asList(ChatUtilities.hideText(storage.getUnique()) + ChatColor.GOLD + "Amount: " + ChatColor.DARK_AQUA + "" + storage.getAmount(), ChatColor.GOLD + "Max Storage Size: " + ChatColor.DARK_AQUA + ConfigUtilities.MAX_STORAGE_SIZE));
			storageItem.setItemMeta(storageItemMeta);
			inv.setItem(i, storageItem);
		}
		
		if(page == 1 && storages.size() > PAGING) {
			ItemStack nextPage = new ItemStack(Material.ARROW);
			ItemMeta nextPageMeta = nextPage.getItemMeta();
			nextPageMeta.setDisplayName(ChatColor.BLUE + "Next Page");
			nextPage.setItemMeta(nextPageMeta);
			inv.setItem(53, nextPage);
		}
		if(page != 1) {
			ItemStack prevPage = new ItemStack(Material.ARROW);
			ItemMeta prevPageMeta = prevPage.getItemMeta();
			prevPageMeta.setDisplayName(ChatColor.BLUE + "Previous Page");
			prevPage.setItemMeta(prevPageMeta);
			inv.setItem(45, prevPage);
		}
		
		ItemStack newStorage = new ItemStack(Material.PAPER);
		ItemMeta newStorageMeta = newStorage.getItemMeta();
		newStorageMeta.setDisplayName(ChatColor.YELLOW + "Add New Cold Storage");
		newStorageMeta.setLore(Arrays.asList(ChatColor.GOLD + "Price: " + ChatColor.DARK_AQUA + EconUtils.stringifyPrice(), ChatColor.GOLD + "Max Storage Size: " + ChatColor.DARK_AQUA + ConfigUtilities.MAX_STORAGE_SIZE));
		newStorage.setItemMeta(newStorageMeta);
		inv.setItem(49, newStorage);

		player.openInventory(inv);
	}
	
	public static void selectColdStorageType(Player player) {
		Inventory inv = Bukkit.createInventory(null, 9, "Select Material for Cold Storage");
		
		player.openInventory(inv);
	}
	
	public static void createColdStorage(Player player, Material type) {
		if(type.getMaxStackSize() > 1) {
			Table table = ColdStorage.getDb().getTable(StorageTable.class);
			StorageTable storageTable = null;
			if(table instanceof StorageTable) {
				storageTable = (StorageTable) table;
			} else {
				return;
			}
			
			if(EconUtils.takeMoney(player)) {
				storageTable.addPlayerStorage(player, type);
				ChatUtilities.sendMessage(player, "Paid " + EconUtils.stringifyPrice() + " on Cold Storage for " + type.name() + ".");
				listColdStorage(player);
			} else {
				ChatUtilities.sendMessage(player, "Don't have the money for a new cold storage: " + EconUtils.stringifyPrice());
			}
		} else {
			ChatUtilities.sendMessage(player, "Not valid material; please select a different item.");
			selectColdStorageType(player);
		}
	}
	
	public static void openColdStorage(Player player, ItemStack item) {
		if(item == null) return;
		ItemMeta itemMeta = item.getItemMeta();
		List<String> lore = itemMeta.getLore();
		String id = "";
		if(lore.size() > 0) {
			id = lore.get(0).split(ChatColor.GOLD + "Amount")[0];
		}
		if(id != "") {
			id = ChatUtilities.revealText(id);
		} else {
			return;
		}
		
		Storage storage = Storage.getStorage(player, id);
		
		Inventory inv = Bukkit.createInventory(null, 27, "Cold Storage: " + item.getType().name());
		
		ItemStack itemStack = new ItemStack(item.getType());
		ItemMeta itemStackMeta = itemStack.getItemMeta();
		itemStackMeta.setDisplayName(ChatColor.GOLD + "Take a Stack");
		itemStackMeta.setLore(Arrays.asList(ChatUtilities.hideText(storage.getUnique()) + ChatColor.GOLD + "Amount: " + ChatColor.DARK_AQUA + "" + storage.getAmount(), ChatColor.GOLD + "Max Storage Size: " + ChatColor.DARK_AQUA + ConfigUtilities.MAX_STORAGE_SIZE));
		itemStack.setItemMeta(itemStackMeta);
		inv.setItem(4, itemStack);
		
		ItemStack fillInventory = new ItemStack(Material.DRAGON_BREATH);
		ItemMeta fillInventoryMeta = fillInventory.getItemMeta();
		fillInventoryMeta.setDisplayName(ChatColor.GOLD + "Fill Inventory");
		fillInventory.setItemMeta(fillInventoryMeta);
		inv.setItem(14, fillInventory);
		
		ItemStack emptyInventory = new ItemStack(Material.GLASS_BOTTLE);
		ItemMeta emptyInventoryMeta = emptyInventory.getItemMeta();
		emptyInventoryMeta.setDisplayName(ChatColor.GOLD + "Empty Inventory");
		emptyInventoryMeta.setLore(Arrays.asList("Click items in your inventory to insert manually."));
		emptyInventory.setItemMeta(emptyInventoryMeta);
		inv.setItem(12, emptyInventory);
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatColor.BLUE + "Go Back");
		back.setItemMeta(backMeta);
		inv.setItem(18, back);
		
		player.openInventory(inv);
	}
	
	public static int maxAddToInventory(Player player, Material type) {
		int add = 0;
		Inventory inv = player.getInventory();
		for(int i = 0; i < 36; i++) {
			ItemStack item = inv.getItem(i);
			if(item == null) {
				add += type.getMaxStackSize();
			}else if(item.getType().equals(type)) {
				add += (item.getMaxStackSize() - item.getAmount());
			}else if(item.getType().equals(Material.AIR)) {
				add += type.getMaxStackSize();
			}
		}
		return add;
	}
	
	public static int maxRemoveFromInventory(Player player, Material type) {
		int remove = 0;
		Inventory inv = player.getInventory();
		for(int i = 0; i < 36; i++) {
			ItemStack item = inv.getItem(i);
			if(item == null) {
				
			}else if(item.getType().equals(type)) {
				remove += item.getAmount();
			}
		}
		return remove;
	}
	
	public static int addItems(Player player, Material material, int amount) {
		Inventory inv = player.getInventory();
		
		for(int i = 0; i < 36; i++) {
			int addItem = material.getMaxStackSize();
			ItemStack item = inv.getItem(i);
			if(item == null) {
				if(addItem > amount) addItem = amount;
				player.getInventory().setItem(i, new ItemStack(material, addItem));
				amount -= addItem;
			}else if(item.getType().equals(material)) {
				int setItem = item.getAmount();
				if(addItem > amount + setItem) addItem = amount + setItem;
				player.getInventory().setItem(i, new ItemStack(material, addItem));
				amount = amount - addItem + setItem;
			}else if(item.getType().equals(Material.AIR)) {
				if(addItem > amount) addItem = amount;
				player.getInventory().setItem(i, new ItemStack(material, material.getMaxStackSize()));
				amount -= addItem;
			}
			if(amount <= 0) break;
		}
		return amount;
	}

}

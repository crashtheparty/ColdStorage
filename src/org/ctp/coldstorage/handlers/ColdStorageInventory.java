package org.ctp.coldstorage.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.database.tables.StorageTable;
import org.ctp.coldstorage.database.tables.Table;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.EconUtils;
import org.ctp.coldstorage.utils.InventoryUtilities;
import org.ctp.coldstorage.utils.Storage;
import org.ctp.coldstorage.utils.StorageList;
import org.ctp.coldstorage.utils.config.ConfigUtilities;
import org.ctp.coldstorage.utils.config.ItemSerialization;
import org.ctp.coldstorage.utils.exception.ColdStorageOverMaxException;

public class ColdStorageInventory {

	private final static int PAGING = 36;
	private OfflinePlayer player, admin;
	private Player show;
	private Screen screen;
	private boolean opening = false, editing = false;
	private String unique = null;
	
	public ColdStorageInventory(OfflinePlayer player) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
	}
	
	public ColdStorageInventory(OfflinePlayer player, OfflinePlayer admin) {
		this.player = player;
		this.admin = admin;
		if(this.admin instanceof Player) {
			setShow((Player) this.admin); 
		}
	}
	
	public void update(int page) {
		switch(screen) {
		case LIST:
			listColdStorage(page);
		default:
			break;
		}
	}
	
	public void listColdStorage() {
		listColdStorage(1);
	}
	
	public void listDeleteColdStorage() {
		listDeleteColdStorage(1);
	}
	
	public void listEditColdStorage() {
		listEditColdStorage(1);
	}
	
	public void listColdStorage(int page) {
		setUnique(null);
		setScreen(Screen.LIST);
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
			inv = Bukkit.createInventory(null, 54, player.getName() + "'s List");
		} else {
			inv = Bukkit.createInventory(null, 54, player.getName() + "'s List Page " + page);
		}
		for(int i = 0; i < PAGING; i++) {
			int storageNum = i + (PAGING * (page - 1));
			if(storages.size() <= storageNum) break;
			Storage storage = storages.get(storageNum);
			
			ItemStack storageItem = new ItemStack(storage.getMaterial());
			ItemMeta storageItemMeta = storageItem.getItemMeta();
			storageItemMeta.setDisplayName(ChatColor.GOLD + "Type: " + ChatColor.DARK_AQUA + "" + storage.getMaterial().name());
			List<String> lore = new ArrayList<String>();
			lore.addAll(Arrays.asList(ChatUtils.hideText(storage.getUnique()) + ChatColor.GOLD + "Amount: " + ChatColor.DARK_AQUA + "" + storage.getAmount(), 
					ChatColor.GOLD + "Max Storage Size: " + ChatColor.DARK_AQUA + ConfigUtilities.MAX_STORAGE_SIZE));
			if(!storage.getMeta().equals("")) {
				lore.add(ChatColor.GOLD + "Metadata: ");
				lore.addAll(Arrays.asList(storage.getMeta().split(" ")));
			}
			storageItemMeta.setLore(lore);
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
		
		ItemStack insertAll = new ItemStack(Material.COBBLESTONE);
		ItemMeta insertAllMeta = insertAll.getItemMeta();
		insertAllMeta.setDisplayName(ChatColor.YELLOW + "Insert to All Cold Storages");
		insertAll.setItemMeta(insertAllMeta);
		inv.setItem(40, insertAll);
		
		ItemStack editOrder = new ItemStack(Material.PAPER);
		ItemMeta editOrderMeta = editOrder.getItemMeta();
		editOrderMeta.setDisplayName(ChatColor.YELLOW + "Edit Display Order");
		editOrder.setItemMeta(editOrderMeta);
		inv.setItem(47, editOrder);
		
		if((admin != null && show.hasPermission("coldstorage.admindelete")) || (admin == null && show.hasPermission("coldstorage.delete"))) {
			ItemStack delete = new ItemStack(Material.BARRIER);
			ItemMeta deleteMeta = delete.getItemMeta();
			deleteMeta.setDisplayName(ChatColor.YELLOW + "Delete Cold Storage List");
			String refund = admin == null ? EconUtils.stringifyRefund(show) : "No Refund";
			deleteMeta.setLore(Arrays.asList(ChatColor.GOLD + "Refund: " + ChatColor.DARK_AQUA + refund));
			delete.setItemMeta(deleteMeta);
			inv.setItem(51, delete);
		}
		
		ItemStack newStorage = new ItemStack(Material.PAPER);
		ItemMeta newStorageMeta = newStorage.getItemMeta();
		newStorageMeta.setDisplayName(ChatColor.YELLOW + "Add New Cold Storage");
		String price = admin == null ? EconUtils.stringifyPrice(show) : "Free";
		newStorageMeta.setLore(Arrays.asList(ChatColor.GOLD + "Price: " + ChatColor.DARK_AQUA + price, ChatColor.GOLD + "Max Storage Size: " + ChatColor.DARK_AQUA + ConfigUtilities.MAX_STORAGE_SIZE));
		newStorage.setItemMeta(newStorageMeta);
		inv.setItem(49, newStorage);

		open(inv);
	}
	
	public void listEditColdStorage(int page) {
		setEditing(false);
		setUnique(null);
		setScreen(Screen.EDIT);
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
			inv = Bukkit.createInventory(null, 54, player.getName() + " Edits");
		} else {
			inv = Bukkit.createInventory(null, 54, player.getName() + " Edits Page " + page);
		}
		for(int i = 0; i < PAGING; i++) {
			int storageNum = i + (PAGING * (page - 1));
			if(storages.size() <= storageNum) break;
			Storage storage = storages.get(storageNum);
			
			ItemStack storageItem = new ItemStack(storage.getMaterial());
			ItemMeta storageItemMeta = storageItem.getItemMeta();
			storageItemMeta.setDisplayName(ChatColor.GOLD + "Type: " + ChatColor.DARK_AQUA + "" + storage.getMaterial().name());
			List<String> lore = new ArrayList<String>();
			lore.addAll(Arrays.asList(ChatUtils.hideText(storage.getUnique()) + ChatColor.GOLD + "Amount: " + ChatColor.DARK_AQUA + "" + storage.getAmount(), 
					ChatColor.GOLD + "Order: " + ChatColor.DARK_AQUA + storage.getOrderBy()));
			if(!storage.getMeta().equals("")) {
				lore.add(ChatColor.GOLD + "Metadata: ");
				lore.addAll(Arrays.asList(storage.getMeta().split(" ")));
			}
			storageItemMeta.setLore(lore);
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
		
		ItemStack listOrder = new ItemStack(Material.BOOK);
		ItemMeta listOrderMeta = listOrder.getItemMeta();
		listOrderMeta.setDisplayName(ChatColor.YELLOW + "List Display Order");
		listOrder.setItemMeta(listOrderMeta);
		inv.setItem(39, listOrder);
		
		if((admin != null && show.hasPermission("coldstorage.admindelete")) || (admin == null && show.hasPermission("coldstorage.delete"))) {
			ItemStack delete = new ItemStack(Material.BARRIER);
			ItemMeta deleteMeta = delete.getItemMeta();
			deleteMeta.setDisplayName(ChatColor.YELLOW + "Delete Cold Storage List");
			String refund = admin == null ? EconUtils.stringifyRefund(show) : "No Refund";
			deleteMeta.setLore(Arrays.asList(ChatColor.GOLD + "Refund: " + ChatColor.DARK_AQUA + refund));
			delete.setItemMeta(deleteMeta);
			inv.setItem(41, delete);
		}

		open(inv);
	}
	
	public void listDeleteColdStorage(int page) {
		setUnique(null);
		setScreen(Screen.DELETE);
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
			inv = Bukkit.createInventory(null, 54, player.getName() + "'s List");
		} else {
			inv = Bukkit.createInventory(null, 54, player.getName() + "'s List Page " + page);
		}
		for(int i = 0; i < PAGING; i++) {
			int storageNum = i + (PAGING * (page - 1));
			if(storages.size() <= storageNum) break;
			Storage storage = storages.get(storageNum);
			
			ItemStack storageItem = new ItemStack(storage.getMaterial());
			ItemMeta storageItemMeta = storageItem.getItemMeta();
			storageItemMeta.setDisplayName(ChatColor.GOLD + "Type: " + ChatColor.DARK_AQUA + "" + storage.getMaterial().name());
			List<String> lore = new ArrayList<String>();
			lore.addAll(Arrays.asList(ChatUtils.hideText(storage.getUnique()) + ChatColor.GOLD + "Amount: " + ChatColor.DARK_AQUA + "" + storage.getAmount(), 
					ChatColor.GOLD + "Can Delete: " + ChatColor.DARK_AQUA + (storage.getAmount() > 0 ? "Too Many Items In Storage" : "Yes"), 
					ChatColor.GOLD + "Refund: " + ChatColor.DARK_AQUA + EconUtils.stringifyRefund(show)));
			if(!storage.getMeta().equals("")) {
				lore.add(ChatColor.GOLD + "Metadata: ");
				lore.addAll(Arrays.asList(storage.getMeta().split(" ")));
			}
			storageItemMeta.setLore(lore);
			storageItem.setItemMeta(storageItemMeta);
			inv.setItem(i, storageItem);
		}
		
		if(storages.size() > PAGING * page) {
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
		
		ItemStack editOrder = new ItemStack(Material.PAPER);
		ItemMeta editOrderMeta = editOrder.getItemMeta();
		editOrderMeta.setDisplayName(ChatColor.YELLOW + "Edit Display Order");
		editOrder.setItemMeta(editOrderMeta);
		inv.setItem(39, editOrder);
		
		ItemStack listOrder = new ItemStack(Material.BOOK);
		ItemMeta listOrderMeta = listOrder.getItemMeta();
		listOrderMeta.setDisplayName(ChatColor.YELLOW + "List Display Order");
		listOrder.setItemMeta(listOrderMeta);
		inv.setItem(41, listOrder);

		open(inv);
	}
	
	public void selectColdStorageType() {
		setScreen(Screen.TYPE);
		Inventory inv = Bukkit.createInventory(null, 9, "Select Material for Cold Storage");
		
		open(inv);
	}
	
	public void createColdStorage(ItemStack item) {
		Material type = item.getType();
		if(type == null || type.equals(Material.AIR)) {
			ChatUtils.sendMessage(show, "Not valid material; please select a different item.");
		} else {
			Table table = ColdStorage.getDb().getTable(StorageTable.class);
			StorageTable storageTable = null;
			if(table instanceof StorageTable) {
				storageTable = (StorageTable) table;
			} else {
				return;
			}
			
			Storage storage = new Storage(player, null, item, 0, 0);
			
			if(show.equals(player)) {
				if(player instanceof Player) {
					Player p = (Player) player;
					if(EconUtils.takeMoney(p, admin != null)) {
						storageTable.addPlayerStorage(storage, show);
						ChatUtils.sendMessage(show, "Paid " + EconUtils.stringifyPrice(p) + " on Cold Storage for " + type.name() + ".");
						listColdStorage();
					} else {
						ChatUtils.sendMessage(show, "Don't have the money for a new cold storage: " + EconUtils.stringifyPrice(p));
					}
				} else {
					ChatUtils.sendMessage(show, "Player " + player.getName() + " must be online to take money from them.");
				}
			} else {
				storageTable.addPlayerStorage(storage, show);
				ChatUtils.sendMessage(show, "Admin created cold storage for " + player.getName() + " for " + type.name() + ".");
				listColdStorage();
			}
		}
	}
	
	public void openColdStorage(ItemStack item, String id) {
		if(item == null) return;
		if(id == null) {
			ItemMeta itemMeta = item.getItemMeta();
			List<String> lore = itemMeta.getLore();
			id = "";
			if(lore.size() > 0) {
				id = lore.get(0).split(ChatColor.GOLD + "Amount")[0];
			}
			if(id != "") {
				id = ChatUtils.revealText(id);
			} else {
				return;
			}
		}
		setUnique(id);
		setScreen(Screen.OPEN);
		
		Storage storage = Storage.getStorage(player, id);
		
		if(storage == null) {
			ChatUtils.sendMessage(show, "There was an issue opening this storage. Please ask an administrator for assistance.");
			ChatUtils.sendToConsole(Level.WARNING, "Storage was null: Player - " + player.getName() + " ID - " + id);
			return;
		}
		
		Inventory inv = Bukkit.createInventory(null, 27, "Cold Storage: " + item.getType().name());
		
		ItemStack itemStack = new ItemStack(item.getType());
		ItemMeta itemStackMeta = itemStack.getItemMeta();
		itemStackMeta.setDisplayName(ChatColor.GOLD + "Take a Stack");
		List<String> takeAllLore = new ArrayList<String>();
		takeAllLore.addAll(Arrays.asList(ChatColor.GOLD + "Amount: " + ChatColor.DARK_AQUA + "" + storage.getAmount(), 
				ChatColor.GOLD + "Max Storage Size: " + ChatColor.DARK_AQUA + ConfigUtilities.MAX_STORAGE_SIZE));
		if(!storage.getMeta().equals("")) {
			takeAllLore.add(ChatColor.GOLD + "Metadata: ");
			takeAllLore.addAll(Arrays.asList(storage.getMeta().split(" ")));
		}
		itemStackMeta.setLore(takeAllLore);
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
		
		open(inv);
	}
	
	public void editColdStorage(ItemStack item) {
		if(item == null) return;
		ItemMeta itemMeta = item.getItemMeta();
		List<String> lore = itemMeta.getLore();
		String id = "";
		if(lore.size() > 0) {
			id = lore.get(0).split(ChatColor.GOLD + "Amount")[0];
		}
		if(id != "") {
			id = ChatUtils.revealText(id);
		} else {
			return;
		}
		setUnique(id);
		
		setEditing(true);
		ChatUtils.sendMessage(show, "Type a number into chat to set the order.");
		show.closeInventory();
	}
	
	public void deleteColdStorage(ItemStack item) {
		if(item == null) return;
		ItemMeta itemMeta = item.getItemMeta();
		List<String> lore = itemMeta.getLore();
		String id = "";
		if(lore.size() > 0) {
			id = lore.get(0).split(ChatColor.GOLD + "Amount")[0];
		}
		if(id != "") {
			id = ChatUtils.revealText(id);
		} else {
			return;
		}
		setUnique(id);
		
		Storage storage = Storage.getStorage(player, id);
		storage.deleteStorage(show);
		
		listDeleteColdStorage();
	}
	
	public void insertAll() {
		StorageList list = StorageList.getList(player);
		for(Storage storage : list.getStorages()) {
			ItemStack storageItem = ItemSerialization.dataToItem(storage.getMaterial(), storage.getAmount(), storage.getMeta());
			if(storageItem != null) {
				int amount = 0;
				try {
					amount = InventoryUtilities.maxRemoveFromInventory(storage.getAmount(), show, storageItem);
				} catch (ColdStorageOverMaxException ex) {
					ChatUtils.sendMessage(show, ex.getMessage());
					return;
				}
				storageItem = ItemSerialization.dataToItem(storage.getMaterial(), amount, storage.getMeta());
				show.getInventory().removeItem(storageItem);
				storage.setAmount(storage.getAmount() + amount);
				
				storage.updateStorage(player);
			}
		}
		update(1);
	}

	public Screen getScreen() {
		return screen;
	}

	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	public Player getShow() {
		return show;
	}

	private void setShow(Player show) {
		this.show = show;
	}

	public boolean isOpening() {
		return opening;
	}
	
	public void open(Inventory inv) {
		opening = true;
		show.openInventory(inv);
		if(opening) {
			opening = false;
		}
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}
	
	public OfflinePlayer getPlayer() {
		return player;
	}

	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public enum Screen{
		LIST(), EDIT(), DELETE(), OPEN(), TYPE();
	}
}

package org.ctp.coldstorage.inventory.storage;

import org.ctp.coldstorage.storage.Chest.ChestType;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.LocationUtils;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.storage.Chest;
import org.ctp.coldstorage.storage.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EditChests implements ColdStorageInventory{
	
	private final static int PAGING = 36;
	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private boolean opening;
	private Storage storage;
	private ChestType chestType;
	private int page;

	public EditChests(OfflinePlayer player, Storage storage, ChestType chestType) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
		this.setStorage(storage);
		this.setChestType(chestType);
	}
	
	public EditChests(OfflinePlayer player, OfflinePlayer admin, Storage storage, ChestType chestType) {
		this.player = player;
		this.admin = admin;
		if(this.admin != null && this.admin instanceof Player) {
			setShow((Player) this.admin); 
		} else {
			setShow((Player) this.player); 
		}
		this.setStorage(storage);
		this.setChestType(chestType);
	}
	
	public void changePage(int page) {
		this.page = page;
		setInventory();
	}
	
	public int getPage() {
		return page;
	}

	@Override
	public OfflinePlayer getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}

	@Override
	public OfflinePlayer getAdmin() {
		return admin;
	}

	@Override
	public void setAdmin(OfflinePlayer player) {
		this.admin = player;
	}

	@Override
	public Player getShow() {
		return show;
	}

	@Override
	public void setShow(Player player) {
		this.show = player;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void setInventory() {
		if(page < 1) page = 1;
		Inventory inv;
		List<Chest> chests = DatabaseUtils.getChests(player);
		HashMap<String, Object> codes = getCodes("%name%", storage.getName());
		codes.put("%type%", chestType.name());
		if(PAGING > chests.size() && page == 1) {
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(codes, "inventory.editchests.title"));
		} else {
			codes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(codes, "inventory.editchests.title_paginated"));
		}
		inv = open(inv);
		
		for(int i = 0; i < PAGING; i++) {
			int chestNum = i + (PAGING * (page - 1));
			if(chests.size() <= chestNum) break;
			Chest chest = chests.get(chestNum);
			ChestType currentType = DatabaseUtils.getChestType(storage, chest);
			ItemStack chestItem = new ItemStack(Material.GOLDEN_APPLE);
			String selected = ChatUtils.getMessage(ChatUtils.getCodes(), "info.false");
			String otherInfo = "";
			if(currentType != null) {
				if(currentType == chestType) {
					chestItem.setType(Material.ENCHANTED_GOLDEN_APPLE);
					selected = ChatUtils.getMessage(ChatUtils.getCodes(), "info.true");
				} else {
					otherInfo = ChatUtils.getMessage(ChatUtils.getCodes(), "inventory.editchests.different_type");
					chestItem.setType(Material.APPLE);
				}
			}
			ItemMeta chestItemMeta = chestItem.getItemMeta();
			chestItemMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.editchests.chest_type"));
			List<String> lore = new ArrayList<String>();
			HashMap<String, Object> loreCodes = getCodes();
			if(!otherInfo.equals("")) {
				lore.add(otherInfo);
			}
			lore.add(ChatUtils.getMessage(getCodes("%selected%", selected), "inventory.editchests.selected"));
			loreCodes.put("%location_one%", LocationUtils.locationToString(chest.getLoc()));
			loreCodes.put("%location_two%", LocationUtils.locationToString(chest.getDoubleLoc()));
			lore.addAll(ChatUtils.getMessages(loreCodes, "inventory.editchests.chest_type_locations"));
			chestItemMeta.setLore(lore);
			chestItem.setItemMeta(chestItemMeta);
			inv.setItem(i, chestItem);
		}
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(49, back);

		if(chests.size() > PAGING * page) {
			ItemStack nextPage = new ItemStack(Material.ARROW);
			ItemMeta nextPageMeta = nextPage.getItemMeta();
			nextPageMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.next_page"));
			nextPage.setItemMeta(nextPageMeta);
			inv.setItem(53, nextPage);
		}
		if(page != 1) {
			ItemStack prevPage = new ItemStack(Material.ARROW);
			ItemMeta prevPageMeta = prevPage.getItemMeta();
			prevPageMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.previous_page"));
			prevPage.setItemMeta(prevPageMeta);
			inv.setItem(45, prevPage);
		}
	}
	
	public void toggle(int slot) {
		int num = slot + (PAGING * (page - 1));
		List<Chest> chests = DatabaseUtils.getChests(player);
		if(chests.size() > num) {
			Chest chest = chests.get(num);
			chest.toggle(storage, chestType, show);
		}
		setInventory();
	}
	
	public void viewStorage() {
		close(false);
		InventoryUtils.addInventory(show, new ViewStorage(player, admin, storage));
	}

	@Override
	public void close(boolean external) {
		if(InventoryUtils.getInventory(show) != null) {
			InventoryUtils.removeInventory(show);
			if(!external) {
				show.closeInventory();
			}
		}
	}

	@Override
	public Inventory open(Inventory inv) {
		opening = true;
		if(inventory == null) {
			inventory = inv;
			show.openInventory(inv);
		} else {
			if(inv.getSize() == inventory.getSize()) {
				inv = show.getOpenInventory().getTopInventory();
				inventory = inv;
			} else {
				inventory = inv;
				show.openInventory(inv);
			}
		}
		for(int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, new ItemStack(Material.AIR));
		}
		if(opening) {
			opening = false;
		}
		return inv;
	}

	@Override
	public HashMap<String, Object> getCodes() {
		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%player%", player.getName());
		if(admin != null) {
			codes.put("%admin%", admin.getName());
		}
		codes.put("%show%", show.getName());
		return codes;
	}
	
	public HashMap<String, Object> getCodes(String string, Object object) {
		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%player%", player.getName());
		if(admin != null) {
			codes.put("%admin%", admin.getName());
		}
		codes.put("%show%", show.getName());
		codes.put(string, object);
		return codes;
	}
	
	public HashMap<String, Object> getCodes(HashMap<String, Object> objects) {
		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%player%", player.getName());
		if(admin != null) {
			codes.put("%admin%", admin.getName());
		}
		codes.put("%show%", show.getName());
		codes.putAll(objects);
		return codes;
	}

	@Override
	public boolean isOpening() {
		return opening;
	}
	public ChestType getChestType() {
		return chestType;
	}

	public void setChestType(ChestType chestType) {
		this.chestType = chestType;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

}

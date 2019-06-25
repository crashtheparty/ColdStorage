package org.ctp.coldstorage.inventory.admin;

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
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.nms.AnvilGUI;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.PriceUtils;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class EditStorageTypeList implements ColdStorageInventory, Anvilable{
	
	private final static int PAGING = 36;
	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private int page = 1;
	private boolean opening = false, editing = false;
	
	public EditStorageTypeList(OfflinePlayer player) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
	}
	
	public EditStorageTypeList(OfflinePlayer player, OfflinePlayer admin) {
		this.player = player;
		this.admin = admin;
		if(this.admin != null && this.admin instanceof Player) {
			setShow((Player) this.admin); 
		} else {
			setShow((Player) this.player); 
		}
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
		editing = false;
		Inventory inv = null;
		if(PAGING >= StorageType.getAll().size() && page == 1) {
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(getCodes(), "inventory.editstoragelist.title"));
		} else {
			HashMap<String, Object> titleCodes = getCodes();
			titleCodes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(titleCodes, "inventory.editstoragelist.title_paginated"));
		}
		inv = open(inv);
		
		for(int i = 0; i < PAGING; i++) {
			int typeNum = i + (PAGING * (page - 1));
			if(StorageType.getAll().size() <= typeNum) break;
			StorageType type = StorageType.getAll().get(typeNum);
			
			ItemStack storageItem = new ItemStack(Material.PAPER);
			ItemMeta storageItemMeta = storageItem.getItemMeta();
			HashMap<String, Object> itemCodes = getCodes();
			itemCodes.put("%type%", type.getType());
			storageItemMeta.setDisplayName(ChatUtils.getMessage(itemCodes, "inventory.editstoragelist.storage_type"));
			List<String> lore = new ArrayList<String>();
			HashMap<String, Object> loreCodes = getCodes();
			loreCodes.put("%size%", type.getMaxAmountBase());
			loreCodes.put("%price%", PriceUtils.getStringCost(type));
			lore.addAll(ChatUtils.getMessages(loreCodes, "inventory.editstoragelist.storage_lore"));
			storageItemMeta.setLore(lore);
			storageItem.setItemMeta(storageItemMeta);
			inv.setItem(i, storageItem);
		}

		ItemStack create = new ItemStack(Material.BOOK);
		ItemMeta createMeta = create.getItemMeta();
		createMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.create_remove.create_storage_type"));
		create.setItemMeta(createMeta);
		inv.setItem(50, create);
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(48, back);

		if(StorageType.getAll().size() > PAGING * page) {
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
	
	public void createNew() {
		editing = true;
		inventory = null;
		AnvilGUI.createAnvil(show, this, false);
	}
	
	public void viewAdminList() {
		close(false);
		InventoryUtils.addInventory(show, new AdminList(player, admin));
	}

	@Override
	public void close(boolean external) {
		if(InventoryUtils.getInventory(show) != null) {
			if(!editing) {
				InventoryUtils.removeInventory(show);
			}
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
	public boolean isOpening() {
		return opening;
	}

	@Override
	public boolean isEditing() {
		return editing;
	}

	@Override
	public void setEditing(boolean editing) {
		this.editing = editing;
	}
	
	public void changePage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	@Override
	public void setItemName(String name) {
		if(StorageType.getStorageType(name) != null) {
			ChatUtils.sendMessage(show, ChatUtils.getMessage(getCodes(), "exceptions.missing_storage_type"));
			editing = false;
			setInventory();
			return;
		}
		editing = false;
		close(false);
		StorageType type = new StorageType(name, 0, 0, 100, new ItemStack(Material.DIAMOND, 4), 100000);
		StorageType.add(type);
		DatabaseUtils.addStorageType(show, type);
		InventoryUtils.addInventory(show, new EditStorageType(player, admin, type));
	}

	public void editStorageType(int slot) {
		int num = slot + (PAGING * (page - 1));
		if(StorageType.getAll().size() > num) {
			StorageType type = StorageType.getAll().get(num);
			close(false);
			InventoryUtils.addInventory(show, new EditStorageType(player, admin, type));
			return;
		}
		
		ChatUtils.sendMessage(show, ChatUtils.getMessage(getCodes(), "exceptions.invalid_storage_type"));
		setInventory();
	}

	@Override
	public void setChoice(String choice) {
		
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
	public boolean isChoice() {
		return false;
	}

}

package org.ctp.coldstorage.inventory.draft;

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
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.storage.Draft;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.PriceUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class StorageTypeList implements ColdStorageInventory{

	private final static int PAGING = 18;
	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private int page = 1;
	private boolean opening = false;
	private Draft draft;
	
	public StorageTypeList(OfflinePlayer player, Draft draft) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
		this.setDraft(draft);
	}
	
	public StorageTypeList(OfflinePlayer player, OfflinePlayer admin, Draft draft) {
		this.player = player;
		this.admin = admin;
		if(this.admin != null && this.admin instanceof Player) {
			setShow((Player) this.admin); 
		} else {
			setShow((Player) this.player); 
		}
		this.setDraft(draft);
	}
	
	public void changePage(int page) {
		this.page = page;
		setInventory();
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

	@SuppressWarnings("serial")
	@Override
	public void setInventory() {
		Inventory inv = null;
		if(PAGING > StorageType.getAll().size() && page == 1) {
			inv = Bukkit.createInventory(null, 36, ChatUtils.getMessage(getCodes(), "inventory.storagetypelist.title"));
		} else {
			inv = Bukkit.createInventory(null, 36, ChatUtils.getMessage(getCodes("%page%", page), "inventory.storagetypelist.title_paginated"));
		}
		inv = open(inv);
		
		for(int i = 0; i < PAGING; i++) {
			int typeNum = i + (PAGING * (page - 1));
			if(StorageType.getAll().size() <= typeNum) break;
			StorageType type = StorageType.getAll().get(typeNum);
			
			ItemStack storageItem = new ItemStack(Material.PAPER);
			ItemMeta storageItemMeta = storageItem.getItemMeta();
			storageItemMeta.setDisplayName(ChatUtils.getMessage(getCodes("%storage_type%", type.getType()), "inventory.storagetypelist.storage_type"));
			List<String> lore = new ArrayList<String>();
			lore.addAll(ChatUtils.getMessages(getCodes(new HashMap<String, Object>() {{
				put("%max_amount%", type.getMaxAmountBase()); put("%price%", PriceUtils.getStringCost(type));
			}}), "inventory.storagetypelist.storage_meta"));
			storageItemMeta.setLore(lore);
			storageItem.setItemMeta(storageItemMeta);
			inv.setItem(i, storageItem);
		}

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(31, back);

		if(StorageType.getAll().size() > PAGING * page) {
			ItemStack nextPage = new ItemStack(Material.ARROW);
			ItemMeta nextPageMeta = nextPage.getItemMeta();
			nextPageMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.next_page"));
			nextPage.setItemMeta(nextPageMeta);
			inv.setItem(35, nextPage);
		}
		if(page != 1) {
			ItemStack prevPage = new ItemStack(Material.ARROW);
			ItemMeta prevPageMeta = prevPage.getItemMeta();
			prevPageMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.previous_page"));
			prevPage.setItemMeta(prevPageMeta);
			inv.setItem(27, prevPage);
		}
	}
	
	public int getPage() {
		return page;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	public void selectStorageType(int slot) {
		int num = slot + (PAGING * (page - 1));
		
		if(StorageType.getAll().size() > num) {
			StorageType type = StorageType.getAll().get(num);
			draft.setStorageType(type);
			DatabaseUtils.updateCache(player, draft);
			close(false);
			InventoryUtils.addInventory(show, new ViewDraft(player, admin, draft));
			return;
		}
		ChatUtils.sendMessage(show, ChatUtils.getMessage(getCodes(), "exceptions.missing_storage_type"));
		setInventory();
	}
	
	public void viewDraft() {
		close(false);
		InventoryUtils.addInventory(show, new ViewDraft(player, admin, draft));
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

	public Draft getDraft() {
		return draft;
	}

	public void setDraft(Draft draft) {
		this.draft = draft;
	}

}

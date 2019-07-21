package org.ctp.coldstorage.inventory.draft;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.ctp.coldstorage.inventory.storage.ListStorage;
import org.ctp.coldstorage.storage.Cache;
import org.ctp.coldstorage.storage.Draft;
import org.ctp.coldstorage.storage.StorageList;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class DraftList implements ColdStorageInventory{

	private final static int PAGING = 36;
	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private int page = 1;
	private boolean opening = false;

	public DraftList(OfflinePlayer player) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
	}
	
	public DraftList(OfflinePlayer player, OfflinePlayer admin) {
		this.player = player;
		this.admin = admin;
		if(this.admin != null && this.admin instanceof Player) {
			setShow((Player) this.admin); 
		} else {
			setShow((Player) this.player); 
		}
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

	@Override
	public void setInventory() {
		StorageList storageList = StorageList.getList(player);
		if(page < 1) page = 1;
		storageList.setPage(page);
		storageList.update();
		List<Cache> storages = storageList.getDrafts();
		if(storages == null) {
			storages = new ArrayList<Cache>();
		}
		Inventory inv;
		if(PAGING >= storages.size() && page == 1) {
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(getCodes(), "inventory.draftlist.title"));
		} else {
			HashMap<String, Object> titleCodes = getCodes();
			titleCodes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(titleCodes, "inventory.draftlist.title_paginated"));
		}
		inv = open(inv);
		for(int i = 0; i < PAGING; i++) {
			int storageNum = i + (PAGING * (page - 1));
			if(storages.size() <= storageNum) break;
			if(storages.get(storageNum) instanceof Draft) {
				Draft draft = (Draft) storages.get(storageNum);
				
				ItemStack storageItem = new ItemStack(draft.getMaterial());
				if(storageItem.getType() == null || storageItem.getType() == Material.AIR) storageItem.setType(Material.BARRIER);
				ItemMeta storageItemMeta = storageItem.getItemMeta();
				HashMap<String, Object> nameCodes = getCodes();
				nameCodes.put("%name%", draft.getName());
				storageItemMeta.setDisplayName(ChatUtils.getMessage(nameCodes, "inventory.draftlist.name"));
				List<String> lore = new ArrayList<String>();
				String maxAmount = draft.getStorageType() == null ? "Unknown" : "" + draft.getStorageType().getMaxAmountBase();
				HashMap<String, Object> amountCodes = getCodes();
				amountCodes.put("%max_amount%", maxAmount);
				lore.add(ChatUtils.getMessage(amountCodes, "inventory.draftlist.max_amount"));
				if(!draft.getMeta().equals("")) {
					lore.add(ChatUtils.getMessage(getCodes(), "inventory.draftlist.metadata"));
					lore.addAll(Arrays.asList(draft.getMeta().split(" ")));
				}
				storageItemMeta.setLore(lore);
				storageItem.setItemMeta(storageItemMeta);
				inv.setItem(i, storageItem);
			}
		}

		if(storages.size() > PAGING * page) {
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
		
		ItemStack buy = new ItemStack(Material.PAPER);
		ItemMeta buyMeta = buy.getItemMeta();
		buyMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.create_remove.create_draft"));
		buy.setItemMeta(buyMeta);
		inv.setItem(50, buy);
		
		ItemStack viewList = new ItemStack(Material.BOOK);
		ItemMeta viewListMeta = viewList.getItemMeta();
		viewListMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.draftlist.viewstorages"));
		viewList.setItemMeta(viewListMeta);
		inv.setItem(48, viewList);
	}
	
	public void viewList() {
		close(false);
		InventoryUtils.addInventory(show, new ListStorage(player, admin));
	}
	
	public void editDraft(int slot) {
		int num = slot + (PAGING * (page - 1));
		StorageList storageList = StorageList.getList(player);
		
		List<Cache> storages = storageList.getDrafts();
		if(storages == null) {
			storages = new ArrayList<Cache>();
		}
		if(storages.size() > num) {
			if(storages.get(num) instanceof Draft) {
				Draft draft = (Draft) storages.get(num);
				close(false);
				InventoryUtils.addInventory(show, new ViewDraft(player, admin, draft));
				return;
			}
		}
		ChatUtils.sendMessage(show, ChatUtils.getMessage(getCodes(), "exceptions.missing_draft"));
		setInventory();
	}
	
	public void createNew() {
		Draft draft = new Draft(player);
		DatabaseUtils.addCache(player, draft);
		if(draft.getUnique() != null) {
			close(false);
			InventoryUtils.addInventory(show, new ViewDraft(player, admin, draft));
		} else {
			ChatUtils.sendMessage(show, ChatUtils.getMessage(getCodes(), "exceptions.missing_draft"));
			setInventory();
		}
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
	public boolean isOpening() {
		return opening;
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
}

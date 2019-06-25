package org.ctp.coldstorage.inventory.storage;

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
import org.ctp.coldstorage.inventory.draft.DraftList;
import org.ctp.coldstorage.storage.Cache;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.coldstorage.storage.StorageList;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.config.ItemSerialization;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class ListStorage implements ColdStorageInventory{

	private final static int PAGING = 36;
	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private int page = 1;
	private boolean opening = false;
	
	public ListStorage(OfflinePlayer player) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
	}
	
	public ListStorage(OfflinePlayer player, OfflinePlayer admin) {
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
		List<Cache> storages = storageList.getStorages();
		if(storages == null) {
			storages = new ArrayList<Cache>();
		}
		Inventory inv;
		if(PAGING >= storages.size() && page == 1) {
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(getCodes(), "inventory.storagelist.title"));
		} else {
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(getCodes("%page%", page), "inventory.storagelist.title_paginated"));
		}
		inv = open(inv);
		for(int i = 0; i < PAGING; i++) {
			int storageNum = i + (PAGING * (page - 1));
			if(storages.size() <= storageNum) break;
			if(storages.get(storageNum) instanceof Storage) {
				Storage storage = (Storage) storages.get(storageNum);
				
				ItemStack storageItem = new ItemStack(storage.getMaterial());
				ItemMeta storageItemMeta = storageItem.getItemMeta();
				storageItemMeta.setDisplayName(ChatUtils.getMessage(getCodes("%name%", storage.getName()), "inventory.storagelist.name"));
				List<String> lore = new ArrayList<String>();
				boolean first = true;
				lore.add(ChatUtils.getMessage(getCodes("%amount%", storage.getStoredAmount()), "inventory.storagelist.amount"));
				lore.add(ChatUtils.getMessage(getCodes("%insert%", storage.canInsertAll()), "inventory.storagelist.insert"));
				for(String meta : storage.getMeta().split(" ")) {
					if(first) {
						lore.add(ChatUtils.getMessage(getCodes("%meta%", meta), "inventory.storagelist.meta_first"));
					} else {
						lore.add(ChatUtils.getMessage(getCodes("%meta%", meta), "inventory.storagelist.meta"));
					}
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
		buyMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.create_remove.buy_cold_storage"));
		buy.setItemMeta(buyMeta);
		inv.setItem(51, buy);

		ItemStack insertAll = new ItemStack(Material.COBBLESTONE);
		ItemMeta insertAllMeta = insertAll.getItemMeta();
		insertAllMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.storagelist.insert_all"));
		insertAll.setItemMeta(insertAllMeta);
		inv.setItem(47, insertAll);
	}
	
	public void viewDraftList() {
		close(false);
		InventoryUtils.addInventory(show, new DraftList(player, admin));
	}
	
	@SuppressWarnings("serial")
	public void insertAll() {
		for(Cache cache : StorageList.getList(player).getStorages()) {
			if(cache instanceof Storage && ((Storage) cache).canInsertAll()) {
				Storage storage = (Storage) cache;
				ItemStack itemAdd = ItemSerialization.dataToItem(storage.getMaterial(), 1, storage.getMeta());
				int amount = InventoryUtils.maxRemoveFromInventory(storage, show, itemAdd);
				if(amount > 0) {
					itemAdd = ItemSerialization.dataToItem(storage.getMaterial(), amount, storage.getMeta());
					show.getInventory().removeItem(itemAdd);
					storage.setStoredAmount(storage.getStoredAmount() + amount);
					DatabaseUtils.updateCache(show, storage);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(getCodes(new HashMap<String, Object>() {{
						put("%amount%", amount); put("%material%", storage.getMaterial()); put("%storage%", storage.getName());
					}}), "inventory.storagelist.inserted"));
				}
			}
		}
		setInventory();
	}
	
	public void viewStorage(int slot) {
		int num = slot + (PAGING * (page - 1));
		
		StorageList storageList = StorageList.getList(player);
		
		List<Cache> storages = storageList.getStorages();
		if(storages == null) {
			storages = new ArrayList<Cache>();
		}
		if(storages.size() > num) {
			if(storages.get(num) instanceof Storage) {
				Storage storage = (Storage) storages.get(num);
				close(false);
				InventoryUtils.addInventory(show, new ViewStorage(player, admin, storage));
				return;
			}
		}
		ChatUtils.sendMessage(getShow(), ChatUtils.getMessage(getCodes(), "exceptions.missing_storage"));
		setInventory();
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

	public void updateStorage(Storage storage) {
		StorageList storageList = StorageList.getList(player);
		storageList.update();
		List<Cache> storages = storageList.getStorages();
		if(storages == null) {
			storages = new ArrayList<Cache>();
		}
		for(int i = 0; i < PAGING; i++) {
			int storageNum = i + (PAGING * (page - 1));
			if(storages.size() <= storageNum) break;
			if(storages.get(storageNum) instanceof Storage) {
				Storage s = (Storage) storages.get(storageNum);
				if(s.getUnique().equals(storage.getUnique())) {
					ItemStack storageItem = new ItemStack(storage.getMaterial());
					ItemMeta storageItemMeta = storageItem.getItemMeta();
					storageItemMeta.setDisplayName(ChatUtils.getMessage(getCodes("%name%", storage.getName()), "inventory.storagelist.name"));
					List<String> lore = new ArrayList<String>();
					boolean first = true;
					lore.add(ChatUtils.getMessage(getCodes("%amount%", storage.getStoredAmount()), "inventory.storagelist.amount"));
					lore.add(ChatUtils.getMessage(getCodes("%insert%", storage.canInsertAll()), "inventory.storagelist.insert"));
					for(String meta : storage.getMeta().split(" ")) {
						if(first) {
							lore.add(ChatUtils.getMessage(getCodes("%meta%", meta), "inventory.storagelist.meta_first"));
						} else {
							lore.add(ChatUtils.getMessage(getCodes("%meta%", meta), "inventory.storagelist.meta"));
						}
					}
					storageItemMeta.setLore(lore);
					storageItem.setItemMeta(storageItemMeta);
					inventory.setItem(i, storageItem);
				}
			}
		}
	}

}

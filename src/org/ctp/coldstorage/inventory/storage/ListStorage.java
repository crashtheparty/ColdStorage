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
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.ColdStorageData;
import org.ctp.coldstorage.inventory.draft.DraftList;
import org.ctp.coldstorage.storage.Cache;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.coldstorage.storage.StorageList;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;
import org.ctp.crashapi.inventory.Pageable;
import org.ctp.crashapi.item.MatData;
import org.ctp.crashapi.utils.ChatUtils;

public class ListStorage extends ColdStorageData implements Pageable {

	private final static int PAGING = 36;
	private int page = 1;

	public ListStorage(Player player) {
		super(player);
	}

	public ListStorage(Player player, OfflinePlayer editing) {
		super(player, editing);
	}

	public void changePage(int page) {
		this.page = page;
		setInventory();
	}

	@Override
	public void setInventory() {
		StorageList storageList = StorageList.getList(getEditing());
		if (page < 1) page = 1;
		storageList.setPage(page);
		storageList.update();
		List<Cache> storages = storageList.getStorages();
		if (storages == null) storages = new ArrayList<Cache>();
		Inventory inv;
		if (PAGING >= storages.size() && page == 1) inv = Bukkit.createInventory(null, 54, getChat().getMessage(getCodes(), "inventory.storagelist.title"));
		else
			inv = Bukkit.createInventory(null, 54, getChat().getMessage(getCodes("%page%", page), "inventory.storagelist.title_paginated"));
		inv = open(inv);
		for(int i = 0; i < PAGING; i++) {
			int storageNum = i + (PAGING * (page - 1));
			if (storages.size() <= storageNum) break;
			if (storages.get(storageNum) instanceof Storage) {
				Storage storage = (Storage) storages.get(storageNum);

				Material m = storage.getMaterial();
				if (m == null || m == Material.AIR) m = Material.BARRIER;
				ItemStack storageItem = new ItemStack(m);
				ItemMeta storageItemMeta = storageItem.getItemMeta();
				storageItemMeta.setDisplayName(getChat().getMessage(getCodes("%name%", storage.getName()), "inventory.storagelist.name"));
				List<String> lore = new ArrayList<String>();
				boolean first = true;
				lore.add(getChat().getMessage(getCodes("%amount%", storage.getStoredAmount()), "inventory.storagelist.amount"));
				lore.add(getChat().getMessage(getCodes("%insert%", storage.canInsertAll()), "inventory.storagelist.insert"));
				for(String meta: storage.getMeta().split(" "))
					if (first) lore.add(getChat().getMessage(getCodes("%meta%", meta), "inventory.storagelist.meta_first"));
					else
						lore.add(getChat().getMessage(getCodes("%meta%", meta), "inventory.storagelist.meta"));
				if (storage.getMaterial() == null || MatData.isAir(storage.getMaterial())) lore.add(getChat().getMessage(getCodes("%name%", storage.getMaterialName()), "inventory.storagelist.null_material"));
				storageItemMeta.setLore(lore);
				storageItem.setItemMeta(storageItemMeta);
				inv.setItem(i, storageItem);
			}
		}

		if (storages.size() > PAGING * page) {
			ItemStack nextPage = new ItemStack(Material.ARROW);
			ItemMeta nextPageMeta = nextPage.getItemMeta();
			nextPageMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.next_page"));
			nextPage.setItemMeta(nextPageMeta);
			inv.setItem(53, nextPage);
		}
		if (page != 1) {
			ItemStack prevPage = new ItemStack(Material.ARROW);
			ItemMeta prevPageMeta = prevPage.getItemMeta();
			prevPageMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.previous_page"));
			prevPage.setItemMeta(prevPageMeta);
			inv.setItem(45, prevPage);
		}

		ItemStack buy = new ItemStack(Material.PAPER);
		ItemMeta buyMeta = buy.getItemMeta();
		buyMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.create_remove.buy_cold_storage"));
		buy.setItemMeta(buyMeta);
		inv.setItem(51, buy);

		ItemStack insertAll = new ItemStack(Material.COBBLESTONE);
		ItemMeta insertAllMeta = insertAll.getItemMeta();
		insertAllMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.storagelist.insert_all"));
		insertAll.setItemMeta(insertAllMeta);
		inv.setItem(47, insertAll);
	}

	public void viewDraftList() {
		close(false);
		ColdStorage.getPlugin().addInventory(new DraftList(getPlayer(), getEditing()));
	}

	@SuppressWarnings("serial")
	public void insertAll() {
		for(Cache cache: StorageList.getList(getEditing()).getStorages())
			if (cache instanceof Storage && ((Storage) cache).canInsertAll()) {
				Storage storage = (Storage) cache;
				ItemStack itemAdd = ColdStorage.getPlugin().getItemSerial().dataToItem(storage.getMaterial(), 1, storage.getMeta());
				int amount = InventoryUtils.maxRemoveFromInventory(storage, getPlayer(), itemAdd);
				if (amount > 0) {
					itemAdd = ColdStorage.getPlugin().getItemSerial().dataToItem(storage.getMaterial(), amount, storage.getMeta());
					getPlayer().getInventory().removeItem(itemAdd);
					storage.setStoredAmount(storage.getStoredAmount() + amount);
					DatabaseUtils.updateCache(getPlayer(), storage);
					getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes(new HashMap<String, Object>() {
						{
							put("%amount%", amount);
							put("%material%", storage.getMaterial());
							put("%storage%", storage.getName());
						}
					}), "inventory.storagelist.inserted"));
				}
			}
		setInventory();
	}

	public void viewStorage(int slot) {
		int num = slot + (PAGING * (page - 1));

		StorageList storageList = StorageList.getList(getEditing());

		List<Cache> storages = storageList.getStorages();
		if (storages == null) storages = new ArrayList<Cache>();
		if (storages.size() > num) if (storages.get(num) instanceof Storage) {
			Storage storage = (Storage) storages.get(num);
			close(false);
			ColdStorage.getPlugin().addInventory(new ViewStorage(getPlayer(), getEditing(), storage));
			return;
		}
		getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes(), "exceptions.missing_storage"));
		setInventory();
	}

	public void updateStorage(Storage storage) {
		StorageList storageList = StorageList.getList(getEditing());
		storageList.update();
		List<Cache> storages = storageList.getStorages();
		if (storages == null) storages = new ArrayList<Cache>();
		for(int i = 0; i < PAGING; i++) {
			int storageNum = i + (PAGING * (page - 1));
			if (storages.size() <= storageNum) break;
			if (storages.get(storageNum) instanceof Storage) {
				Storage s = (Storage) storages.get(storageNum);
				if (s.getUnique().equals(storage.getUnique())) {
					ItemStack storageItem = new ItemStack(storage.getMaterial());
					ItemMeta storageItemMeta = storageItem.getItemMeta();
					storageItemMeta.setDisplayName(getChat().getMessage(getCodes("%name%", storage.getName()), "inventory.storagelist.name"));
					List<String> lore = new ArrayList<String>();
					boolean first = true;
					lore.add(getChat().getMessage(getCodes("%amount%", storage.getStoredAmount()), "inventory.storagelist.amount"));
					lore.add(getChat().getMessage(getCodes("%insert%", storage.canInsertAll()), "inventory.storagelist.insert"));
					for(String meta: storage.getMeta().split(" "))
						if (first) lore.add(getChat().getMessage(getCodes("%meta%", meta), "inventory.storagelist.meta_first"));
						else
							lore.add(getChat().getMessage(getCodes("%meta%", meta), "inventory.storagelist.meta"));
					storageItemMeta.setLore(lore);
					storageItem.setItemMeta(storageItemMeta);
					getInventory().setItem(i, storageItem);
				}
			}
		}
	}

	@Override
	public ChatUtils getChat() {
		return Chatable.get();
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public void setPage(int page) {
		this.page = page;
	}

}

package org.ctp.coldstorage.inventory.draft;

import java.util.*;

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
import org.ctp.coldstorage.inventory.storage.ListStorage;
import org.ctp.coldstorage.storage.Cache;
import org.ctp.coldstorage.storage.Draft;
import org.ctp.coldstorage.storage.StorageList;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.crashapi.inventory.Pageable;
import org.ctp.crashapi.utils.ChatUtils;

public class DraftList extends ColdStorageData implements Pageable {

	private final static int PAGING = 36;
	private int page = 1;

	public DraftList(Player player) {
		super(player);
	}

	public DraftList(Player player, OfflinePlayer editing) {
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
		List<Cache> storages = storageList.getDrafts();
		if (storages == null) storages = new ArrayList<Cache>();
		Inventory inv;
		if (PAGING >= storages.size() && page == 1) inv = Bukkit.createInventory(null, 54, getChat().getMessage(getCodes(), "inventory.draftlist.title"));
		else {
			HashMap<String, Object> titleCodes = getCodes();
			titleCodes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, getChat().getMessage(titleCodes, "inventory.draftlist.title_paginated"));
		}
		inv = open(inv);
		for(int i = 0; i < PAGING; i++) {
			int storageNum = i + (PAGING * (page - 1));
			if (storages.size() <= storageNum) break;
			if (storages.get(storageNum) instanceof Draft) {
				Draft draft = (Draft) storages.get(storageNum);

				ItemStack storageItem = new ItemStack(draft.getMaterial());
				if (storageItem.getType() == null || storageItem.getType() == Material.AIR) storageItem.setType(Material.BARRIER);
				ItemMeta storageItemMeta = storageItem.getItemMeta();
				HashMap<String, Object> nameCodes = getCodes();
				nameCodes.put("%name%", draft.getName());
				storageItemMeta.setDisplayName(getChat().getMessage(nameCodes, "inventory.draftlist.name"));
				List<String> lore = new ArrayList<String>();
				String maxAmount = draft.getStorageType() == null ? "Unknown" : "" + draft.getStorageType().getMaxAmountBase();
				HashMap<String, Object> amountCodes = getCodes();
				amountCodes.put("%max_amount%", maxAmount);
				lore.add(getChat().getMessage(amountCodes, "inventory.draftlist.max_amount"));
				if (!draft.getMeta().equals("")) {
					lore.add(getChat().getMessage(getCodes(), "inventory.draftlist.metadata"));
					lore.addAll(Arrays.asList(draft.getMeta().split(" ")));
				}
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
		buyMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.create_remove.create_draft"));
		buy.setItemMeta(buyMeta);
		inv.setItem(50, buy);

		ItemStack viewList = new ItemStack(Material.BOOK);
		ItemMeta viewListMeta = viewList.getItemMeta();
		viewListMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.draftlist.viewstorages"));
		viewList.setItemMeta(viewListMeta);
		inv.setItem(48, viewList);
	}

	public void viewList() {
		close(false);
		ColdStorage.getPlugin().addInventory(new ListStorage(getPlayer(), getEditing()));
	}

	public void editDraft(int slot) {
		int num = slot + (PAGING * (page - 1));
		StorageList storageList = StorageList.getList(getEditing());

		List<Cache> storages = storageList.getDrafts();
		if (storages == null) storages = new ArrayList<Cache>();
		if (storages.size() > num) if (storages.get(num) instanceof Draft) {
			Draft draft = (Draft) storages.get(num);
			close(false);
			ColdStorage.getPlugin().addInventory(new ViewDraft(getPlayer(), getEditing(), draft));
			return;
		}
		getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes(), "exceptions.missing_draft"));
		setInventory();
	}

	public void createNew() {
		Draft draft = new Draft(getEditing());
		DatabaseUtils.addCache(getEditing(), draft);
		if (draft.getUnique() != null) {
			close(false);
			ColdStorage.getPlugin().addInventory(new ViewDraft(getPlayer(), getEditing(), draft));
		} else {
			getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes(), "exceptions.missing_draft"));
			setInventory();
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

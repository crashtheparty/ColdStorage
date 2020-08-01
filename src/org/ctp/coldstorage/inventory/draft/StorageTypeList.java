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
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.ColdStorageData;
import org.ctp.coldstorage.storage.Draft;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.PriceUtils;
import org.ctp.crashapi.inventory.Pageable;
import org.ctp.crashapi.utils.ChatUtils;

public class StorageTypeList extends ColdStorageData implements Pageable {

	private final static int PAGING = 18;
	private int page = 1;
	private Draft draft;

	public StorageTypeList(Player player, Draft draft) {
		super(player);
		this.draft = draft;
	}

	public StorageTypeList(Player player, OfflinePlayer editing, Draft draft) {
		super(player, editing);
		this.draft = draft;
	}

	@Override
	public void setPage(int page) {
		this.page = page;
		setInventory();
	}

	@SuppressWarnings("serial")
	@Override
	public void setInventory() {
		Inventory inv = null;
		if (PAGING >= StorageType.getAll().size() && page == 1) inv = Bukkit.createInventory(null, 36, getChat().getMessage(getCodes(), "inventory.storagetypelist.title"));
		else
			inv = Bukkit.createInventory(null, 36, getChat().getMessage(getCodes("%page%", page), "inventory.storagetypelist.title_paginated"));
		inv = open(inv);

		for(int i = 0; i < PAGING; i++) {
			int typeNum = i + (PAGING * (page - 1));
			if (StorageType.getAll().size() <= typeNum) break;
			StorageType type = StorageType.getAll().get(typeNum);

			ItemStack storageItem = new ItemStack(Material.PAPER);
			ItemMeta storageItemMeta = storageItem.getItemMeta();
			storageItemMeta.setDisplayName(getChat().getMessage(getCodes("%storage_type%", type.getType()), "inventory.storagetypelist.storage_type"));
			List<String> lore = new ArrayList<String>();
			lore.addAll(getChat().getMessages(getCodes(new HashMap<String, Object>() {
				{
					put("%max_amount%", type.getMaxAmountBase());
					put("%price%", PriceUtils.getStringCost(type));
				}
			}), "inventory.storagetypelist.storage_meta"));
			storageItemMeta.setLore(lore);
			storageItem.setItemMeta(storageItemMeta);
			inv.setItem(i, storageItem);
		}

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(31, back);

		if (StorageType.getAll().size() > PAGING * page) {
			ItemStack nextPage = new ItemStack(Material.ARROW);
			ItemMeta nextPageMeta = nextPage.getItemMeta();
			nextPageMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.next_page"));
			nextPage.setItemMeta(nextPageMeta);
			inv.setItem(35, nextPage);
		}
		if (page != 1) {
			ItemStack prevPage = new ItemStack(Material.ARROW);
			ItemMeta prevPageMeta = prevPage.getItemMeta();
			prevPageMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.previous_page"));
			prevPage.setItemMeta(prevPageMeta);
			inv.setItem(27, prevPage);
		}
	}

	@Override
	public int getPage() {
		return page;
	}

	public void selectStorageType(int slot) {
		int num = slot + (PAGING * (page - 1));

		if (StorageType.getAll().size() > num) {
			StorageType type = StorageType.getAll().get(num);
			draft.setStorageType(type);
			DatabaseUtils.updateCache(getPlayer(), draft);
			close(false);
			ColdStorage.getPlugin().addInventory(new ViewDraft(getPlayer(), getEditing(), draft));
			return;
		}
		getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes(), "exceptions.missing_storage_type"));
		setInventory();
	}

	public void viewDraft() {
		close(false);
		ColdStorage.getPlugin().addInventory(new ViewDraft(getPlayer(), getEditing(), draft));
	}

	public Draft getDraft() {
		return draft;
	}

	public void setDraft(Draft draft) {
		this.draft = draft;
	}

	@Override
	public ChatUtils getChat() {
		return Chatable.get();
	}

}

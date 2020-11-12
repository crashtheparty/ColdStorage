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
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.inventory.ColdStorageData;
import org.ctp.coldstorage.nms.AnvilGUINMS;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.PriceUtils;
import org.ctp.crashapi.inventory.Pageable;
import org.ctp.crashapi.utils.ChatUtils;

public class EditStorageTypeList extends ColdStorageData implements Pageable, Anvilable {

	private final static int PAGING = 36;
	private int page = 1;

	public EditStorageTypeList(Player player) {
		super(player);
	}

	public EditStorageTypeList(Player player, OfflinePlayer editing) {
		super(player, editing);
	}

	@Override
	public void setInventory() {
		setEdit(false);
		Inventory inv = null;
		if (PAGING >= StorageType.getAll().size() && page == 1) inv = Bukkit.createInventory(null, 54, getChat().getMessage(getCodes(), "inventory.editstoragelist.title"));
		else {
			HashMap<String, Object> titleCodes = getCodes();
			titleCodes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, getChat().getMessage(titleCodes, "inventory.editstoragelist.title_paginated"));
		}
		inv = open(inv);

		for(int i = 0; i < PAGING; i++) {
			int typeNum = i + (PAGING * (page - 1));
			if (StorageType.getAll().size() <= typeNum) break;
			StorageType type = StorageType.getAll().get(typeNum);

			ItemStack storageItem = new ItemStack(Material.PAPER);
			ItemMeta storageItemMeta = storageItem.getItemMeta();
			HashMap<String, Object> itemCodes = getCodes();
			itemCodes.put("%type%", type.getType());
			storageItemMeta.setDisplayName(getChat().getMessage(itemCodes, "inventory.editstoragelist.storage_type"));
			List<String> lore = new ArrayList<String>();
			HashMap<String, Object> loreCodes = getCodes();
			loreCodes.put("%size%", type.getMaxAmountBase());
			loreCodes.put("%price%", PriceUtils.getStringCost(type));
			lore.addAll(getChat().getMessages(loreCodes, "inventory.editstoragelist.storage_lore"));
			storageItemMeta.setLore(lore);
			storageItem.setItemMeta(storageItemMeta);
			inv.setItem(i, storageItem);
		}

		ItemStack create = new ItemStack(Material.BOOK);
		ItemMeta createMeta = create.getItemMeta();
		createMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.create_remove.create_storage_type"));
		create.setItemMeta(createMeta);
		inv.setItem(50, create);

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(48, back);

		if (StorageType.getAll().size() > PAGING * page) {
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
	}

	public void createNew() {
		setEdit(true);
		setInventoryNull();
		AnvilGUINMS.createAnvil(getPlayer(), this, false);
	}

	public void viewAdminList() {
		close(false);
		ColdStorage.getPlugin().addInventory(new AdminList(getPlayer(), getEditing()));
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public void setItemName(String name) {
		if (StorageType.getStorageType(name) != null) {
			getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes(), "exceptions.missing_storage_type"));
			setEdit(false);
			setInventory();
			return;
		}
		setEdit(false);
		close(false);
		StorageType type = new StorageType(name, 0, 0, 100, new ItemStack(Material.DIAMOND, 4), 100000);
		StorageType.add(type);
		DatabaseUtils.addStorageType(getPlayer(), type);
		ColdStorage.getPlugin().addInventory(new EditStorageType(getPlayer(), getEditing(), type));
	}

	public void editStorageType(int slot) {
		int num = slot + (PAGING * (page - 1));
		if (StorageType.getAll().size() > num) {
			StorageType type = StorageType.getAll().get(num);
			close(false);
			ColdStorage.getPlugin().addInventory(new EditStorageType(getPlayer(), getEditing(), type));
			return;
		}

		getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes(), "exceptions.invalid_storage_type"));
		setInventory();
	}

	@Override
	public ChatUtils getChat() {
		return ColdStorage.getPlugin().getChat();
	}

	@Override
	public void setPage(int page) {
		this.page = page;
		setInventory();
	}

	@Override
	public void setChoice(String choice) {}

	@Override
	public boolean isChoice() {
		return false;
	}

}

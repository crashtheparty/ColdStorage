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
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.ColdStorageData;
import org.ctp.coldstorage.storage.Chest;
import org.ctp.coldstorage.storage.Chest.ChestType;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.crashapi.inventory.Pageable;
import org.ctp.crashapi.utils.ChatUtils;
import org.ctp.crashapi.utils.LocationUtils;

public class EditChests extends ColdStorageData implements Pageable {

	private final static int PAGING = 36;
	private Storage storage;
	private ChestType chestType;
	private int page;

	public EditChests(Player player, Storage storage, ChestType chestType) {
		super(player);
		this.setStorage(storage);
		this.setChestType(chestType);
	}

	public EditChests(Player player, OfflinePlayer editing, Storage storage, ChestType chestType) {
		super(player, editing);
		this.setStorage(storage);
		this.setChestType(chestType);
	}

	@Override
	public void setPage(int page) {
		this.page = page;
		setInventory();
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public void setInventory() {
		if (page < 1) page = 1;
		Inventory inv;
		List<Chest> chests = DatabaseUtils.getChests(getEditing());
		HashMap<String, Object> codes = getCodes("%name%", storage.getName());
		codes.put("%type%", chestType.name());
		if (PAGING >= chests.size() && page == 1) inv = Bukkit.createInventory(null, 54, getChat().getMessage(codes, "inventory.editchests.title"));
		else {
			codes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, getChat().getMessage(codes, "inventory.editchests.title_paginated"));
		}
		inv = open(inv);

		for(int i = 0; i < PAGING; i++) {
			int chestNum = i + (PAGING * (page - 1));
			if (chests.size() <= chestNum) break;
			Chest chest = chests.get(chestNum);
			ChestType currentType = DatabaseUtils.getChestType(storage, chest);
			ItemStack chestItem = new ItemStack(Material.GOLDEN_APPLE);
			String selected = getChat().getMessage(getCodes(), "info.false");
			String otherInfo = "";
			if (currentType != null) if (currentType == chestType) {
				chestItem.setType(Material.ENCHANTED_GOLDEN_APPLE);
				selected = getChat().getMessage(getCodes(), "info.true");
			} else {
				otherInfo = getChat().getMessage(getCodes(), "inventory.editchests.different_type");
				chestItem.setType(Material.APPLE);
			}
			ItemMeta chestItemMeta = chestItem.getItemMeta();
			chestItemMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.editchests.chest_type"));
			List<String> lore = new ArrayList<String>();
			HashMap<String, Object> loreCodes = getCodes();
			if (!otherInfo.equals("")) lore.add(otherInfo);
			lore.add(getChat().getMessage(getCodes("%selected%", selected), "inventory.editchests.selected"));
			loreCodes.put("%location_one%", LocationUtils.locationToString(chest.getLoc()));
			loreCodes.put("%location_two%", LocationUtils.locationToString(chest.getDoubleLoc()));
			lore.addAll(getChat().getMessages(loreCodes, "inventory.editchests.chest_type_locations"));
			chestItemMeta.setLore(lore);
			chestItem.setItemMeta(chestItemMeta);
			inv.setItem(i, chestItem);
		}

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(49, back);

		if (chests.size() > PAGING * page) {
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

	public void toggle(int slot) {
		int num = slot + (PAGING * (page - 1));
		List<Chest> chests = DatabaseUtils.getChests(getEditing());
		if (chests.size() > num) {
			Chest chest = chests.get(num);
			chest.toggle(storage, chestType, getPlayer());
		}
		setInventory();
	}

	public void viewStorage() {
		close(false);
		ColdStorage.getPlugin().addInventory(new ViewStorage(getPlayer(), getEditing(), storage));
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

	@Override
	public ChatUtils getChat() {
		return super.getChat();
	}

}

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
import org.bukkit.inventory.meta.SkullMeta;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.ColdStorageData;
import org.ctp.coldstorage.inventory.storage.ListStorage;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.crashapi.inventory.Pageable;
import org.ctp.crashapi.utils.ChatUtils;

public class PlayerList extends ColdStorageData implements Pageable {
	
	private final static int PAGING = 36;
	private int page = 1;
	
	public PlayerList(Player player) {
		super(player);
	}

	public PlayerList(Player player, OfflinePlayer editing) {
		super(player, editing);
	}

	@Override
	public void setInventory() {
		Inventory inv = null;
		List<OfflinePlayer> players = DatabaseUtils.getOfflinePlayers();
		if(players == null) players = new ArrayList<OfflinePlayer>();
		if(PAGING >= players.size() && page == 1) inv = Bukkit.createInventory(null, 54, getChat().getMessage(getCodes(), "inventory.playerlist.title"));
		else {
			HashMap<String, Object> titleCodes = getCodes();
			titleCodes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, getChat().getMessage(titleCodes, "inventory.playerlist.title_paginated"));
		}
		inv = open(inv);
		
		for(int i = 0; i < PAGING; i++) {
			int playerNum = i + (PAGING * (page - 1));
			if(players.size() <= playerNum) break;
			OfflinePlayer offlinePlayer = players.get(playerNum);
			
			ItemStack playerItem = new ItemStack(Material.PLAYER_HEAD);
			ItemMeta playerItemMeta = playerItem.getItemMeta();
			if(playerItemMeta instanceof SkullMeta) ((SkullMeta) playerItemMeta).setOwningPlayer(offlinePlayer);
			HashMap<String, Object> itemCodes = getCodes();
			itemCodes.put("%owning_player%", offlinePlayer.getName());
			playerItemMeta.setDisplayName(getChat().getMessage(itemCodes, "inventory.playerlist.owning_player"));
			List<String> lore = new ArrayList<String>();
			lore.addAll(getChat().getMessages(getCodes(), "inventory.playerlist.meta"));
			playerItemMeta.setLore(lore);
			playerItem.setItemMeta(playerItemMeta);
			inv.setItem(i, playerItem);
		}
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(49, back);

		if(StorageType.getAll().size() > PAGING * page) {
			ItemStack nextPage = new ItemStack(Material.ARROW);
			ItemMeta nextPageMeta = nextPage.getItemMeta();
			nextPageMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.next_page"));
			nextPage.setItemMeta(nextPageMeta);
			inv.setItem(53, nextPage);
		}
		if(page != 1) {
			ItemStack prevPage = new ItemStack(Material.ARROW);
			ItemMeta prevPageMeta = prevPage.getItemMeta();
			prevPageMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.previous_page"));
			prevPage.setItemMeta(prevPageMeta);
			inv.setItem(45, prevPage);
		}
	}
	
	public void openStorageList(int slot) {
		int num = slot + (PAGING * (page - 1));
		List<OfflinePlayer> players = DatabaseUtils.getOfflinePlayers();
		if(players == null) players = new ArrayList<OfflinePlayer>();
		if(players.size() > num) {
			OfflinePlayer player = players.get(num);
			if(player.getUniqueId().equals(getPlayer().getUniqueId())) {
				getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes(), "exceptions.cannot_modify_yourself"));
				setInventory();
				return;
			}
			close(false);
			ColdStorage.getPlugin().addInventory(new ListStorage(getPlayer(), player));
			return;
		}
		getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes(), "exceptions.missing_player"));
		setInventory();
	}
	
	public void viewAdminList() {
		close(false);
		ColdStorage.getPlugin().addInventory(new AdminList(getPlayer(), getEditing()));
	}
	
	@Override
	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public ChatUtils getChat() {
		return Chatable.get();
	}

}

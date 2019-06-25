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
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.inventory.storage.ListStorage;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class PlayerList implements ColdStorageInventory{
	
	private final static int PAGING = 36;
	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private int page = 1;
	private boolean opening = false;
	
	public PlayerList(OfflinePlayer player) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
	}
	
	public PlayerList(OfflinePlayer player, OfflinePlayer admin) {
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
		Inventory inv = null;
		List<OfflinePlayer> players = DatabaseUtils.getOfflinePlayers();
		if(players == null) {
			players = new ArrayList<OfflinePlayer>();
		}
		if(PAGING >= players.size() && page == 1) {
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(getCodes(), "inventory.playerlist.title"));
		} else {
			HashMap<String, Object> titleCodes = getCodes();
			titleCodes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(titleCodes, "inventory.playerlist.title_paginated"));
		}
		inv = open(inv);
		
		for(int i = 0; i < PAGING; i++) {
			int playerNum = i + (PAGING * (page - 1));
			if(players.size() <= playerNum) break;
			OfflinePlayer offlinePlayer = players.get(playerNum);
			
			ItemStack playerItem = new ItemStack(Material.PLAYER_HEAD);
			ItemMeta playerItemMeta = playerItem.getItemMeta();
			if(playerItemMeta instanceof SkullMeta) {
				((SkullMeta) playerItemMeta).setOwningPlayer(offlinePlayer);
			}
			HashMap<String, Object> itemCodes = getCodes();
			itemCodes.put("%owning_player%", offlinePlayer.getName());
			playerItemMeta.setDisplayName(ChatUtils.getMessage(itemCodes, "inventory.playerlist.owning_player"));
			List<String> lore = new ArrayList<String>();
			lore.addAll(ChatUtils.getMessages(getCodes(), "inventory.playerlist.meta"));
			playerItemMeta.setLore(lore);
			playerItem.setItemMeta(playerItemMeta);
			inv.setItem(i, playerItem);
		}
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(49, back);

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
	
	public void openStorageList(int slot) {
		int num = slot + (PAGING * (page - 1));
		List<OfflinePlayer> players = DatabaseUtils.getOfflinePlayers();
		if(players == null) {
			players = new ArrayList<OfflinePlayer>();
		}
		if(players.size() > num) {
			OfflinePlayer player = players.get(num);
			if(player.getUniqueId().equals(this.player.getUniqueId())) {
				ChatUtils.sendMessage(show, ChatUtils.getMessage(getCodes(), "exceptions.cannot_modify_yourself"));
				setInventory();
				return;
			}
			close(false);
			InventoryUtils.addInventory(show, new ListStorage(player, this.player));
			return;
		}
		ChatUtils.sendMessage(show, ChatUtils.getMessage(getCodes(), "exceptions.missing_player"));
		setInventory();
	}
	
	public void viewAdminList() {
		close(false);
		InventoryUtils.addInventory(show, new AdminList(player, admin));
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
	
	public void changePage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
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

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
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.permissions.Permission;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class EditTypePermissions implements ColdStorageInventory{

	private final static int PAGING = 36;
	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private boolean opening = false;
	private StorageType type;
	private int page = 1;
	
	public EditTypePermissions(OfflinePlayer player, StorageType type) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
		this.type = type;
	}
	
	public EditTypePermissions(OfflinePlayer player, OfflinePlayer admin, StorageType type) {
		this.player = player;
		this.admin = admin;
		if(this.admin != null && this.admin instanceof Player) {
			setShow((Player) this.admin); 
		} else {
			setShow((Player) this.player); 
		}
		this.type = type;
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
		if(PAGING > StorageType.getAll().size() && page == 1) {
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(getCodes(), "inventory.edittypepermissions.title"));
		} else {
			HashMap<String, Object> codes = getCodes();
			codes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(codes, "inventory.edittypepermissions.title_paginated"));
		}
		inv = open(inv);
		List<Permission> permissions = DatabaseUtils.getPermissions();
		
		for(int i = 0; i < PAGING; i++) {
			int permissionNum = i + (PAGING * (page - 1));
			if(permissions.size() <= permissionNum) break;
			Permission permission = permissions.get(permissionNum);
			
			ItemStack permissionItem = new ItemStack(Material.GOLDEN_APPLE);
			String selected = ChatUtils.getMessage(ChatUtils.getCodes(), "info.false");
			if(type.getPermissions().contains(permission.getPermission())) {
				permissionItem.setType(Material.ENCHANTED_GOLDEN_APPLE);
				selected = ChatUtils.getMessage(ChatUtils.getCodes(), "info.true");
			}
			ItemMeta permissionItemMeta = permissionItem.getItemMeta();
			HashMap<String, Object> permissionCodes = getCodes();
			permissionCodes.put("%permission%", permission.getPermission());
			permissionItemMeta.setDisplayName(ChatUtils.getMessage(permissionCodes, "inventory.edittypepermissions.permission"));
			List<String> lore = new ArrayList<String>();
			HashMap<String, Object> loreCodes = getCodes();
			loreCodes.put("%check_order%", permission.getCheckOrder());
			loreCodes.put("%num_storages%", permission.getNumStorages());
			loreCodes.put("%selected%", selected);
			lore.addAll(ChatUtils.getMessages(loreCodes, "inventory.edittypepermissions.permission_lore"));
			permissionItemMeta.setLore(lore);
			permissionItem.setItemMeta(permissionItemMeta);
			inv.setItem(i, permissionItem);
		}
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(49, back);

		if(permissions.size() > PAGING * page) {
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
	
	public void togglePermission(int slot) {
		int num = slot + (PAGING * (page - 1));
		List<Permission> permissions = DatabaseUtils.getPermissions();
		if(permissions.size() > num) {
			Permission permission = permissions.get(num);
			type.togglePermission(permission, show);
		}
		setInventory();
	}
	
	public void editStorageType() {
		close(false);
		InventoryUtils.addInventory(show, new EditStorageType(player, admin, type));
	}
	
	public int getPage() {
		return page;
	}
	
	public void changePage(int page) {
		this.page = page;
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

	public StorageType getType() {
		return type;
	}

	public void setType(StorageType type) {
		this.type = type;
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

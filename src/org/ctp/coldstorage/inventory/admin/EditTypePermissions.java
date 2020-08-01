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
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.ColdStorageData;
import org.ctp.coldstorage.permissions.Permission;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.crashapi.inventory.Pageable;
import org.ctp.crashapi.utils.ChatUtils;

public class EditTypePermissions extends ColdStorageData implements Pageable {

	private final static int PAGING = 36;
	private StorageType type;
	private int page = 1;
	
	public EditTypePermissions(Player player, StorageType type) {
		super(player);
		this.type = type;
	}

	public EditTypePermissions(Player player, OfflinePlayer editing, StorageType type) {
		super(player, editing);
		this.type = type;
	}

	@Override
	public void setInventory() {
		Inventory inv = null;
		if(PAGING >= StorageType.getAll().size() && page == 1) inv = Bukkit.createInventory(null, 54, getChat().getMessage(getCodes(), "inventory.edittypepermissions.title"));
		else {
			HashMap<String, Object> codes = getCodes();
			codes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, getChat().getMessage(codes, "inventory.edittypepermissions.title_paginated"));
		}
		inv = open(inv);
		List<Permission> permissions = DatabaseUtils.getPermissions();
		
		for(int i = 0; i < PAGING; i++) {
			int permissionNum = i + (PAGING * (page - 1));
			if(permissions.size() <= permissionNum) break;
			Permission permission = permissions.get(permissionNum);
			
			ItemStack permissionItem = new ItemStack(Material.GOLDEN_APPLE);
			String selected = getChat().getMessage(getCodes(), "info.false");
			if(type.getPermissions().contains(permission.getPermission())) {
				permissionItem.setType(Material.ENCHANTED_GOLDEN_APPLE);
				selected = getChat().getMessage(getCodes(), "info.true");
			}
			ItemMeta permissionItemMeta = permissionItem.getItemMeta();
			HashMap<String, Object> permissionCodes = getCodes();
			permissionCodes.put("%permission%", permission.getPermission());
			permissionItemMeta.setDisplayName(getChat().getMessage(permissionCodes, "inventory.edittypepermissions.permission"));
			List<String> lore = new ArrayList<String>();
			HashMap<String, Object> loreCodes = getCodes();
			loreCodes.put("%check_order%", permission.getCheckOrder());
			loreCodes.put("%num_storages%", permission.getNumStorages());
			loreCodes.put("%selected%", selected);
			lore.addAll(getChat().getMessages(loreCodes, "inventory.edittypepermissions.permission_lore"));
			permissionItemMeta.setLore(lore);
			permissionItem.setItemMeta(permissionItemMeta);
			inv.setItem(i, permissionItem);
		}
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(49, back);

		if(permissions.size() > PAGING * page) {
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
	
	public void togglePermission(int slot) {
		int num = slot + (PAGING * (page - 1));
		List<Permission> permissions = DatabaseUtils.getPermissions();
		if(permissions.size() > num) {
			Permission permission = permissions.get(num);
			type.togglePermission(permission, getPlayer());
		}
		setInventory();
	}
	
	public void editStorageType() {
		close(false);
		ColdStorage.getPlugin().addInventory(new EditStorageType(getPlayer(), getEditing(), type));
	}
	
	@Override
	public int getPage() {
		return page;
	}
	
	@Override
	public void setPage(int page) {
		this.page = page;
	}

	public StorageType getType() {
		return type;
	}

	public void setType(StorageType type) {
		this.type = type;
	}

	@Override
	public ChatUtils getChat() {
		return Chatable.get();
	}

}

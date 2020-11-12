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
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.inventory.ColdStorageData;
import org.ctp.coldstorage.nms.AnvilGUINMS;
import org.ctp.coldstorage.permissions.Permission;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.crashapi.inventory.Pageable;
import org.ctp.crashapi.utils.ChatUtils;

public class ListGlobalPermissions extends ColdStorageData implements Pageable, Anvilable {

	private final static int PAGING = 36;
	private int page = 1;

	public ListGlobalPermissions(Player player) {
		super(player);
	}

	public ListGlobalPermissions(Player player, OfflinePlayer editing) {
		super(player, editing);
	}

	@Override
	public void setInventory() {
		List<Permission> permissions = DatabaseUtils.getGlobalPermissions();
		Inventory inv = null;
		if (PAGING >= permissions.size() && page == 1) inv = Bukkit.createInventory(null, 54, getChat().getMessage(getCodes(), "inventory.listglobalpermissions.title"));
		else {
			HashMap<String, Object> codes = getCodes();
			codes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, getChat().getMessage(codes, "inventory.listglobalpermissions.title_paginated"));
		}
		inv = open(inv);

		for(int i = 0; i < PAGING; i++) {
			int permissionNum = i + (PAGING * (page - 1));
			if (permissions.size() <= permissionNum) break;
			Permission permission = permissions.get(permissionNum);

			ItemStack permissionItem = new ItemStack(Material.NAME_TAG);
			ItemMeta permissionItemMeta = permissionItem.getItemMeta();
			HashMap<String, Object> permissionCodes = getCodes();
			permissionCodes.put("%permission%", permission.getPermission());
			permissionItemMeta.setDisplayName(getChat().getMessage(permissionCodes, "inventory.listglobalpermissions.permission"));
			List<String> lore = new ArrayList<String>();
			HashMap<String, Object> loreCodes = getCodes();
			loreCodes.put("%check_order%", permission.getCheckOrder());
			loreCodes.put("%num_storages%", permission.getNumStorages());
			lore.addAll(getChat().getMessages(loreCodes, "inventory.listglobalpermissions.permission_lore"));
			permissionItemMeta.setLore(lore);
			permissionItem.setItemMeta(permissionItemMeta);
			inv.setItem(i, permissionItem);
		}

		ItemStack create = new ItemStack(Material.BOOK);
		ItemMeta createMeta = create.getItemMeta();
		createMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.create_remove.create_global_permission"));
		create.setItemMeta(createMeta);
		inv.setItem(50, create);

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(48, back);

		if (permissions.size() > PAGING * page) {
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

	public void viewPermission(int slot) {
		int num = slot + (PAGING * (page - 1));
		List<Permission> permissions = DatabaseUtils.getGlobalPermissions();
		if (permissions.size() > num) {
			Permission permission = permissions.get(num);
			close(false);
			ColdStorage.getPlugin().addInventory(new ViewGlobalPermission(getPlayer(), getEditing(), permission));
			return;
		}
		getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes(), "exceptions.permission_does_not_exist"));
		setInventory();
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
	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public void setItemName(String name) {
		setEdit(false);
		name = name.replace(' ', '_').toLowerCase();
		String check = name.toString();
		check = check.replaceAll("[^a-zA-Z0-9\\-\\_]", "");
		if (!check.equals(name)) {
			HashMap<String, Object> codes = getCodes();
			codes.put("%name%", name);
			getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.invalid_permission_string.1"));
			getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes(), "exceptions.invalid_permission_string.2"));
			setInventory();
			return;
		}
		if (DatabaseUtils.getGlobalPermission(check) != null) {
			HashMap<String, Object> codes = getCodes();
			codes.put("%permission%", name);
			getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.permission_exists"));
			setInventory();
			return;
		}
		close(false);
		Permission permission = new Permission(check, 0, 0);
		DatabaseUtils.addGlobalPermission(permission);
		ColdStorage.getPlugin().addInventory(new ViewGlobalPermission(getPlayer(), getEditing(), permission));
	}

	@Override
	public ChatUtils getChat() {
		return Chatable.get();
	}

	@Override
	public void setChoice(String choice) {}

	@Override
	public boolean isChoice() {
		return false;
	}

}

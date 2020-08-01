package org.ctp.coldstorage.inventory.admin;

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

public class AdminList extends ColdStorageData {
	
	public AdminList(Player player) {
		super(player);
	}

	public AdminList(Player player, OfflinePlayer editing) {
		super(player, editing);
	}

	@Override
	public void setInventory(List<ItemStack> arg0) {
		setInventory();
	}

	@Override
	public void setInventory() {
		Inventory inv = Bukkit.createInventory(null, 27, getChat().getMessage(getCodes(), "inventory.adminlist.title"));
		inv = open(inv);

		ItemStack permissions = new ItemStack(Material.NAME_TAG);
		ItemMeta permissionsMeta = permissions.getItemMeta();
		permissionsMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.adminlist.modify_permissions"));
		permissions.setItemMeta(permissionsMeta);
		inv.setItem(10, permissions);
		
		ItemStack globalPermissions = new ItemStack(Material.NAME_TAG);
		ItemMeta globalPermissionsMeta = globalPermissions.getItemMeta();
		globalPermissionsMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.adminlist.modify_global_permissions"));
		globalPermissions.setItemMeta(globalPermissionsMeta);
		inv.setItem(12, globalPermissions);
		
		ItemStack players = new ItemStack(Material.ENDER_CHEST);
		ItemMeta playersMeta = players.getItemMeta();
		playersMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.adminlist.modify_players"));
		players.setItemMeta(playersMeta);
		inv.setItem(14, players);
		
		ItemStack storageType = new ItemStack(Material.CHEST);
		ItemMeta storageTypeMeta = storageType.getItemMeta();
		storageTypeMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.adminlist.modify_storage_types"));
		storageType.setItemMeta(storageTypeMeta);
		inv.setItem(16, storageType);
	}

	public void viewPermissionsList() {
		close(false);
		ColdStorage.getPlugin().addInventory(new ListPermissions(getPlayer(), getEditing()));
	}
	
	public void viewStorageTypeList() {
		close(false);
		ColdStorage.getPlugin().addInventory(new EditStorageTypeList(getPlayer(), getEditing()));
	}

	public void viewGlobalPermissionsList() {
		close(false);
		ColdStorage.getPlugin().addInventory(new ListGlobalPermissions(getPlayer(), getEditing()));
	}

	public void viewPlayerList() {
		close(false);
		ColdStorage.getPlugin().addInventory(new PlayerList(getPlayer(), getEditing()));
	}

}

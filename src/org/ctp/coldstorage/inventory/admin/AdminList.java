package org.ctp.coldstorage.inventory.admin;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class AdminList implements ColdStorageInventory{
	
	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private boolean opening = false;
	
	public AdminList(OfflinePlayer player) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
	}
	
	public AdminList(OfflinePlayer player, OfflinePlayer admin) {
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
		Inventory inv = Bukkit.createInventory(null, 27, ChatUtils.getMessage(getCodes(), "inventory.adminlist.title"));
		inv = open(inv);

		ItemStack permissions = new ItemStack(Material.NAME_TAG);
		ItemMeta permissionsMeta = permissions.getItemMeta();
		permissionsMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.adminlist.modify_permissions"));
		permissions.setItemMeta(permissionsMeta);
		inv.setItem(10, permissions);
		
		ItemStack globalPermissions = new ItemStack(Material.NAME_TAG);
		ItemMeta globalPermissionsMeta = globalPermissions.getItemMeta();
		globalPermissionsMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.adminlist.modify_global_permissions"));
		globalPermissions.setItemMeta(globalPermissionsMeta);
		inv.setItem(12, globalPermissions);
		
		ItemStack players = new ItemStack(Material.ENDER_CHEST);
		ItemMeta playersMeta = players.getItemMeta();
		playersMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.adminlist.modify_players"));
		players.setItemMeta(playersMeta);
		inv.setItem(14, players);
		
		ItemStack storageType = new ItemStack(Material.CHEST);
		ItemMeta storageTypeMeta = storageType.getItemMeta();
		storageTypeMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.adminlist.modify_storage_types"));
		storageType.setItemMeta(storageTypeMeta);
		inv.setItem(16, storageType);
	}

	public void viewPermissionsList() {
		close(false);
		InventoryUtils.addInventory(show, new ListPermissions(player, admin));
	}
	
	public void viewStorageTypeList() {
		close(false);
		InventoryUtils.addInventory(show, new EditStorageTypeList(player, admin));
	}

	public void viewGlobalPermissionsList() {
		close(false);
		InventoryUtils.addInventory(show, new ListGlobalPermissions(player, admin));
	}

	public void viewPlayerList() {
		close(false);
		InventoryUtils.addInventory(show, new PlayerList(player, admin));
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

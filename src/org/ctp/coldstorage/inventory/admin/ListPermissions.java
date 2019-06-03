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
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.nms.AnvilGUI;
import org.ctp.coldstorage.permissions.Permission;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class ListPermissions implements ColdStorageInventory, Anvilable{

	private final static int PAGING = 36;
	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private boolean opening = false, editing = false;
	private int page = 1;
	
	public ListPermissions(OfflinePlayer player) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
	}
	
	public ListPermissions(OfflinePlayer player, OfflinePlayer admin) {
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
		List<Permission> permissions = DatabaseUtils.getPermissions();
		Inventory inv = null;
		if(PAGING > permissions.size() && page == 1) {
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(getCodes(), "inventory.listpermissions.title"));
		} else {
			HashMap<String, Object> codes = getCodes();
			codes.put("%page%", page);
			inv = Bukkit.createInventory(null, 54, ChatUtils.getMessage(codes, "inventory.listpermissions.title_paginated"));
		}
		inv = open(inv);
		
		for(int i = 0; i < PAGING; i++) {
			int permissionNum = i + (PAGING * (page - 1));
			if(permissions.size() <= permissionNum) break;
			Permission permission = permissions.get(permissionNum);
			
			ItemStack permissionItem = new ItemStack(Material.NAME_TAG);
			ItemMeta permissionItemMeta = permissionItem.getItemMeta();
			HashMap<String, Object> permissionCodes = getCodes();
			permissionCodes.put("%permission%", permission.getPermission());
			permissionItemMeta.setDisplayName(ChatUtils.getMessage(permissionCodes, "inventory.listpermissions.permission"));
			List<String> lore = new ArrayList<String>();
			HashMap<String, Object> loreCodes = getCodes();
			loreCodes.put("%check_order%", permission.getCheckOrder());
			loreCodes.put("%num_storages%", permission.getNumStorages());
			lore.addAll(ChatUtils.getMessages(loreCodes, "inventory.listpermissions.permission_lore"));
			permissionItemMeta.setLore(lore);
			permissionItem.setItemMeta(permissionItemMeta);
			inv.setItem(i, permissionItem);
		}

		ItemStack create = new ItemStack(Material.BOOK);
		ItemMeta createMeta = create.getItemMeta();
		createMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.create_remove.create_permission"));
		create.setItemMeta(createMeta);
		inv.setItem(50, create);
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(48, back);
		
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
	
	public void createNew() {
		editing = true;
		inventory = null;
		AnvilGUI.createAnvil(show, this, false);
	}
	
	public void viewPermission(int slot) {
		int num = slot + (PAGING * (page - 1));
		List<Permission> permissions = DatabaseUtils.getPermissions();
		if(permissions.size() > num) {
			Permission permission = permissions.get(num);
			close(false);
			InventoryUtils.addInventory(show, new ViewPermission(player, admin, permission));
			return;
		}
		ChatUtils.sendMessage(show, ChatUtils.getMessage(getCodes(), "exceptions.permission_does_not_exist"));
		setInventory();
	}
	
	public void viewAdminList() {
		close(false);
		InventoryUtils.addInventory(show, new AdminList(player, admin));
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

	@Override
	public boolean isEditing() {
		return editing;
	}

	@Override
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	@Override
	public void setItemName(String name) {
		editing = false;
		name = name.replace(' ', '_').toLowerCase();
		String check = name.toString();
		check = check.replaceAll("[^a-zA-Z0-9\\-\\_]", "");
		if(!check.equals(name)) {
			HashMap<String, Object> codes = getCodes();
			codes.put("%name%", name);
			ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.invalid_permission_string.1"));
			ChatUtils.sendMessage(show, ChatUtils.getMessage(getCodes(), "exceptions.invalid_permission_string.2"));
			setInventory();
			return;
		}
		if(DatabaseUtils.getPermission(check) != null) {
			HashMap<String, Object> codes = getCodes();
			codes.put("%permission%", name);
			ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.permission_exists"));
			setInventory();
			return;
		}
		close(false);
		Permission permission = new Permission(check, 0, 0);
		DatabaseUtils.addPermission(permission);
		InventoryUtils.addInventory(show, new ViewPermission(player, admin, permission));
	}

	@Override
	public void setChoice(String choice) {
		
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

	@Override
	public boolean isChoice() {
		return false;
	}

}

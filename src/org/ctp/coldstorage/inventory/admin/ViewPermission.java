package org.ctp.coldstorage.inventory.admin;

import java.util.HashMap;

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

public class ViewPermission implements ColdStorageInventory, Anvilable{

	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private boolean opening = false, editing = false, choice;
	private Permission permission;
	private String editType;
	
	public ViewPermission(OfflinePlayer player, Permission permission) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
		this.permission = permission;
	}
	
	public ViewPermission(OfflinePlayer player, OfflinePlayer admin, Permission permission) {
		this.player = player;
		this.admin = admin;
		if(this.admin != null && this.admin instanceof Player) {
			setShow((Player) this.admin); 
		} else {
			setShow((Player) this.player); 
		}
		this.permission = permission;
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
		choice = false;
		HashMap<String, Object> codes = getCodes();
		codes.put("%permission%", permission.getPermission());
		Inventory inv = Bukkit.createInventory(null, 27, ChatUtils.getMessage(codes, "inventory.viewpermission.title"));
		inv = open(inv);

		ItemStack permissionItem = new ItemStack(Material.NAME_TAG);
		ItemMeta permissionItemMeta = permissionItem.getItemMeta();
		HashMap<String, Object> permissionCodes = getCodes();
		permissionCodes.put("%permission%", "coldstorage.permissions." + permission.getPermission());
		permissionItemMeta.setDisplayName(ChatUtils.getMessage(permissionCodes, "inventory.viewpermission.permission_string"));
		permissionItem.setItemMeta(permissionItemMeta);
		inv.setItem(4, permissionItem);

		ItemStack checkOrder = new ItemStack(Material.COMPARATOR);
		ItemMeta checkOrderMeta = checkOrder.getItemMeta();
		HashMap<String, Object> checkCodes = getCodes();
		checkCodes.put("%check_order%", permission.getCheckOrder());
		checkOrderMeta.setDisplayName(ChatUtils.getMessage(checkCodes, "inventory.viewpermission.check_order"));
		checkOrder.setItemMeta(checkOrderMeta);
		inv.setItem(12, checkOrder);
		
		ItemStack numAmount = new ItemStack(Material.CHEST);
		ItemMeta numAmountMeta = numAmount.getItemMeta();
		HashMap<String, Object> storageCodes = getCodes();
		storageCodes.put("%num_storages%", permission.getNumStorages());
		numAmountMeta.setDisplayName(ChatUtils.getMessage(storageCodes, "inventory.viewpermission.num_storages"));
		numAmount.setItemMeta(numAmountMeta);
		inv.setItem(14, numAmount);
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(18, back);
		
		ItemStack delete = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta deleteMeta = delete.getItemMeta();
		deleteMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.create_remove.delete"));
		delete.setItemMeta(deleteMeta);
		inv.setItem(26, delete);
	}
	
	public void openAnvil(String type) {
		editing = true;
		editType = type;
		inventory = null;
		AnvilGUI.createAnvil(show, this, false);
	}
	
	public void viewPermissionList() {
		close(false);
		InventoryUtils.addInventory(show, new ListPermissions(player, admin));
	}
	
	public void confirmDelete() {
		editing = true;
		inventory = null;
		AnvilGUI.createAnvil(show, this, true);
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
		if(editType != null) {
			if(editType.equals("check_order")) {
				int checkOrder = 0;
				try {
					checkOrder = Integer.parseInt(name);
				} catch(NumberFormatException ex) {
					HashMap<String, Object> codes = getCodes();
					codes.put("%num%", name);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_format"));
					setInventory();
					return;
				}
				permission.setCheckOrder(checkOrder);
				DatabaseUtils.updatePermission(permission);
				editType = null;
				setInventory();
				return;
			} else if (editType.equals("num_storage")) {
				int numStorage = -1;
				editType = null;
				try {
					numStorage = Integer.parseInt(name);
				} catch(NumberFormatException ex) {
					HashMap<String, Object> codes = getCodes();
					codes.put("%num%", name);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_format"));
					setInventory();
					return;
				}
				if(numStorage < -1) {
					HashMap<String, Object> codes = getCodes();
					codes.put("%num%", numStorage);
					codes.put("%lowest%", -1);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_too_low"));
					numStorage = -1;
				}
				permission.setNumStorages(numStorage);
				DatabaseUtils.updatePermission(permission);
				setInventory();
				return;
			}
		}
		HashMap<String, Object> codes = getCodes();
		codes.put("%option%", editType);
		codes.put("%area%", "Edit Storage Type");
		ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.option_nonexistent"));
		setInventory();
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	@Override
	public void setChoice(String choice) {
		this.choice = false;
		if(choice.equals("confirm")) {
			DatabaseUtils.removePermission(permission.getPermission(), show);
			viewPermissionList();
			return;
		} else if (choice.equals("deny")) {
			setInventory();
			return;
		}
		HashMap<String, Object> codes = getCodes();
		codes.put("%option%", choice);
		codes.put("%area%", "Choice");
		ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.option_nonexistent"));
		setInventory();
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
		return choice;
	}

}

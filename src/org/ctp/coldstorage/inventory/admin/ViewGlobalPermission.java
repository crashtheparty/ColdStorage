package org.ctp.coldstorage.inventory.admin;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.inventory.ColdStorageData;
import org.ctp.coldstorage.nms.AnvilGUINMS;
import org.ctp.coldstorage.permissions.Permission;
import org.ctp.coldstorage.utils.DatabaseUtils;

public class ViewGlobalPermission extends ColdStorageData implements Anvilable {

	private boolean choice;
	private Permission permission;
	private String editType;

	public ViewGlobalPermission(Player player, Permission permission) {
		super(player);
		this.permission = permission;
	}

	public ViewGlobalPermission(Player player, OfflinePlayer editing, Permission permission) {
		super(player, editing);
		this.permission = permission;
	}

	@Override
	public void setInventory() {
		choice = false;
		HashMap<String, Object> codes = getCodes();
		codes.put("%permission%", permission.getPermission());
		Inventory inv = Bukkit.createInventory(null, 27, getChat().getMessage(codes, "inventory.viewpermission.title"));
		inv = open(inv);

		ItemStack permissionItem = new ItemStack(Material.NAME_TAG);
		ItemMeta permissionItemMeta = permissionItem.getItemMeta();
		HashMap<String, Object> permissionCodes = getCodes();
		permissionCodes.put("%permission%", "coldstorage.global-permissions." + permission.getPermission());
		permissionItemMeta.setDisplayName(getChat().getMessage(permissionCodes, "inventory.viewpermission.permission_string"));
		permissionItem.setItemMeta(permissionItemMeta);
		inv.setItem(4, permissionItem);

		ItemStack checkOrder = new ItemStack(Material.COMPARATOR);
		ItemMeta checkOrderMeta = checkOrder.getItemMeta();
		HashMap<String, Object> checkCodes = getCodes();
		checkCodes.put("%check_order%", permission.getCheckOrder());
		checkOrderMeta.setDisplayName(getChat().getMessage(checkCodes, "inventory.viewpermission.check_order"));
		checkOrder.setItemMeta(checkOrderMeta);
		inv.setItem(12, checkOrder);

		ItemStack numAmount = new ItemStack(Material.CHEST);
		ItemMeta numAmountMeta = numAmount.getItemMeta();
		HashMap<String, Object> storageCodes = getCodes();
		storageCodes.put("%num_storages%", permission.getNumStorages());
		numAmountMeta.setDisplayName(getChat().getMessage(storageCodes, "inventory.viewpermission.num_storages"));
		numAmount.setItemMeta(numAmountMeta);
		inv.setItem(14, numAmount);

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(18, back);

		ItemStack delete = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta deleteMeta = delete.getItemMeta();
		deleteMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.create_remove.delete"));
		delete.setItemMeta(deleteMeta);
		inv.setItem(26, delete);
	}

	public void openAnvil(String type) {
		setEdit(true);
		editType = type;
		setInventoryNull();
		AnvilGUINMS.createAnvil(getPlayer(), this, false);
	}

	public void viewPermissionList() {
		close(false);
		ColdStorage.getPlugin().addInventory(new ListPermissions(getPlayer(), getEditing()));
	}

	public void confirmDelete() {
		setEdit(true);
		setInventoryNull();
		AnvilGUINMS.createAnvil(getPlayer(), this, true);
	}

	@Override
	public void setItemName(String name) {
		setEdit(false);
		if (editType != null) if (editType.equals("check_order")) {
			int checkOrder = 0;
			try {
				checkOrder = Integer.parseInt(name);
			} catch (NumberFormatException ex) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", name);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_format"));
				setInventory();
				return;
			}
			permission.setCheckOrder(checkOrder);
			DatabaseUtils.updateGlobalPermission(permission);
			editType = null;
			setInventory();
			return;
		} else if (editType.equals("num_storage")) {
			int numStorage = -1;
			editType = null;
			try {
				numStorage = Integer.parseInt(name);
			} catch (NumberFormatException ex) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", name);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_format"));
				setInventory();
				return;
			}
			if (numStorage < -1) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", numStorage);
				codes.put("%lowest%", -1);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_too_low"));
				numStorage = -1;
			}
			permission.setNumStorages(numStorage);
			DatabaseUtils.updateGlobalPermission(permission);
			setInventory();
			return;
		}
		HashMap<String, Object> codes = getCodes();
		codes.put("%option%", editType);
		codes.put("%area%", "Edit Storage Type");
		getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.option_nonexistent"));
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
		if (choice.equals("confirm")) {
			DatabaseUtils.removeGlobalPermission(permission.getPermission());
			viewPermissionList();
			return;
		} else if (choice.equals("deny")) {
			setInventory();
			return;
		}
		HashMap<String, Object> codes = getCodes();
		codes.put("%option%", choice);
		codes.put("%area%", "Choice");
		getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.option_nonexistent"));
		setInventory();
	}

	@Override
	public boolean isChoice() {
		return choice;
	}

}

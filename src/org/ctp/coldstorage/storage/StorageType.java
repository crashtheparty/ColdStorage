package org.ctp.coldstorage.storage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.permissions.Permission;
import org.ctp.coldstorage.utils.DatabaseUtils;

public class StorageType {

	private static List<StorageType> TYPES = new ArrayList<StorageType>();
	private String type;
	private double vaultCost;
	private int maxAmountBase;
	private ItemStack itemCost;
	private int maxImport, maxExport;

	public StorageType(String type, int maxExport, int maxImport, double vaultCost, ItemStack itemCost, int maxAmountBase) {
		setType(type);
		setMaxExport(maxExport);
		setMaxImport(maxImport);
		setVaultCost(vaultCost);
		setItemCost(itemCost);
		setMaxAmountBase(maxAmountBase);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getVaultCost() {
		return vaultCost;
	}

	public void setVaultCost(double vaultCost) {
		this.vaultCost = vaultCost;
	}

	public ItemStack getItemCost() {
		return itemCost;
	}

	public void setItemCost(ItemStack itemCost) {
		this.itemCost = itemCost;
	}

	public static StorageType getStorageType(String type) {
		for(StorageType s: TYPES) {
			if (s.getType().equals(type)) { return s; }
		}
		return null;
	}

	public static List<StorageType> getAll() {
		return TYPES;
	}

	public static void add(StorageType type) {
		if (!TYPES.contains(type)) {
			TYPES.add(type);
		}
	}

	public static boolean remove(String type) {
		StorageType storage = null;
		for(StorageType s: TYPES) {
			if (s.getType().equals(type)) {
				storage = s;
			}
		}
		if (storage != null) { return TYPES.remove(storage); }
		return false;
	}

	public int getMaxAmountBase() {
		return maxAmountBase;
	}

	public void setMaxAmountBase(int maxAmountBase) {
		this.maxAmountBase = maxAmountBase;
	}

	public List<String> getPermissions() {
		return DatabaseUtils.getStringPermissions(this);
	}

	public void togglePermission(Permission permission, OfflinePlayer player) {
		if (getPermissions().contains(permission.getPermission())) {
			DatabaseUtils.removePermissionFromType(this, permission.getPermission(), player);
		} else {
			DatabaseUtils.addPermissionToType(this, permission.getPermission(), player);
		}
	}

	public int getMaxImport() {
		return maxImport;
	}

	public void setMaxImport(int maxImport) {
		this.maxImport = maxImport;
	}

	public int getMaxExport() {
		return maxExport;
	}

	public void setMaxExport(int maxExport) {
		this.maxExport = maxExport;
	}
}

package org.ctp.coldstorage.storage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.permissions.Permission;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;

public class Draft extends Cache{
	
	public Draft(OfflinePlayer player) {
		super(player, null, null, "", "");
	}
	
	public Draft(OfflinePlayer player, String unique, ItemStack item, StorageType storageType, String name) {
		super(player, unique, item, storageType, name);
	}
	
	public Draft(OfflinePlayer player, String unique, String materialName, String meta, String storageType, String name) {
		super(player, unique, materialName, meta, storageType, name);
	}
	
	public List<String> getReasons(){
		List<String> reasons = new ArrayList<String>();
		
		if(getPlayer() == null) {
			reasons.add(ChatUtils.getMessage(ChatUtils.getCodes(), "reasons.player_null"));
		}
		if(getUnique() == null) {
			reasons.add(ChatUtils.getMessage(ChatUtils.getCodes(), "reasons.unique_null"));
		}
		if(getMaterial() == null || getMaterial() == Material.AIR) {
			reasons.add(ChatUtils.getMessage(ChatUtils.getCodes(), "reasons.material_null"));
		}
		if(getStorageType() == null) {
			if(getStorageTypeString() != null && !getStorageTypeString().equals("")) {
				reasons.add(ChatUtils.getMessage(ChatUtils.getCodes("%storage_type%", getStorageTypeString()), "reasons.player_null"));
			} else {
				reasons.add(ChatUtils.getMessage(ChatUtils.getCodes(), "reasons.storage_type_null"));
			}
		}
		if(getName() == null || getName().equals("")) {
			reasons.add(ChatUtils.getMessage(ChatUtils.getCodes(), "reasons.name_null"));
		}
		if(tooManyTotal()) {
			reasons.add(ChatUtils.getMessage(ChatUtils.getCodes(), "reasons.too_many_total"));
		}
		if(tooManyType()) {
			reasons.add(ChatUtils.getMessage(ChatUtils.getCodes(), "reasons.too_many_type"));
		}
		if(zeroType()) {
			reasons.add(ChatUtils.getMessage(ChatUtils.getCodes(), "reasons.no_permission"));
		}
		
		return reasons;
	}
	
	public String getMaxStorages() {
		boolean hasPermission = false;
		List<String> permissions = DatabaseUtils.getStringPermissions(getStorageType());
		int permissionNum = -1;
		for(String permission : permissions) {
			if(getPlayer().getPlayer().hasPermission(permission)){
				Permission perm = DatabaseUtils.getPermission(permission);
				if(perm != null) {
					hasPermission = true;
					permissionNum = perm.getNumStorages();
				}
			}
		}
		if(!hasPermission) {
			permissionNum = ColdStorage.getPlugin().getConfiguration().getMaxStoragesType();
		}
		if(permissionNum == -1) return "Infinite";
		return "" + permissionNum;
	}
	
	private boolean tooManyTotal() {
		if(getPlayer().getPlayer() == null) {
			return true;
		}
		boolean hasPermission = false;
		int totalStorages = StorageList.getList(getPlayer()).getStorages().size();
		List<Permission> permissions = DatabaseUtils.getGlobalPermissions();
		int permissionNum = -1;
		for(Permission permission : permissions) {
			if(getPlayer().getPlayer().hasPermission(permission.getPermission())){
				hasPermission = true;
				permissionNum = permission.getNumStorages();
			}
		}
		if(!hasPermission) {
			permissionNum = ColdStorage.getPlugin().getConfiguration().getMaxStorages();
		}
		return permissionNum != -1 && permissionNum <= totalStorages;
	}
	
	private boolean tooManyType() {
		if(getPlayer().getPlayer() == null) {
			return true;
		}
		if(this.getStorageType() == null) {
			return true;
		}
		boolean hasPermission = false;
		int totalType = 0;
		for(Cache storage : StorageList.getList(getPlayer()).getStorages()) {
			if(this.getStorageType().equals(storage.getStorageType())){
				totalType ++;
			}
		}
		List<String> permissions = DatabaseUtils.getStringPermissions(getStorageType());
		int permissionNum = -1;
		for(String permission : permissions) {
			if(getPlayer().getPlayer().hasPermission(permission)){
				Permission perm = DatabaseUtils.getPermission(permission);
				if(perm != null) {
					hasPermission = true;
					permissionNum = perm.getNumStorages();
				}
			}
		}
		if(!hasPermission) {
			permissionNum = ColdStorage.getPlugin().getConfiguration().getMaxStoragesType();
		}
		return permissionNum != -1 && permissionNum <= totalType;
	}
	
	private boolean zeroType() {
		if(getPlayer().getPlayer() == null) {
			return true;
		}
		if(this.getStorageType() == null) {
			return true;
		}
		boolean hasPermission = false;
		List<String> permissions = DatabaseUtils.getStringPermissions(getStorageType());
		int permissionNum = -1;
		for(String permission : permissions) {
			if(getPlayer().getPlayer().hasPermission(permission)){
				Permission perm = DatabaseUtils.getPermission(permission);
				if(perm != null) {
					hasPermission = true;
					permissionNum = perm.getNumStorages();
				}
			}
		}
		if(!hasPermission) {
			permissionNum = ColdStorage.getPlugin().getConfiguration().getMaxStoragesType();
		}
		return permissionNum == 0;
	}
	
	public boolean canBuy() {
		return !tooManyTotal() && !tooManyType() && !zeroType() && getPlayer() != null && getUnique() != null && getMaterial() != null
				&& getMaterial() != Material.AIR && getStorageType() != null && getName() != null && !getName().equals("");
	}

}

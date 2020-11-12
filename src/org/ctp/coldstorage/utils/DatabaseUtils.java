package org.ctp.coldstorage.utils;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.database.tables.*;
import org.ctp.coldstorage.inventory.storage.ViewStorage;
import org.ctp.coldstorage.permissions.Permission;
import org.ctp.coldstorage.storage.*;
import org.ctp.coldstorage.storage.Chest.ChestType;
import org.ctp.crashapi.db.tables.Table;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.utils.ChatUtils;

public class DatabaseUtils {

	public static List<OfflinePlayer> getOfflinePlayers() {
		Table table = ColdStorage.getPlugin().getDb().getTable(CacheTable.class);
		CacheTable storageTable = null;
		if (table instanceof CacheTable) storageTable = (CacheTable) table;
		else
			return null;
		return storageTable.getOfflinePlayers();
	}

	public static void updateCache(OfflinePlayer player, Cache cache) {
		Table table = ColdStorage.getPlugin().getDb().getTable(CacheTable.class);
		CacheTable storageTable = null;
		if (table instanceof CacheTable) storageTable = (CacheTable) table;
		storageTable.setPlayerStorage(cache, player);
		if (cache.getPlayer() != null && cache.getPlayer().isOnline()) {
			InventoryData inv = ColdStorage.getPlugin().getInventory(cache.getPlayer().getPlayer());
			if (inv instanceof ViewStorage) {
				ViewStorage viewStorage = (ViewStorage) inv;
				if (viewStorage.getStorage().getUnique().equals(cache.getUnique())) {
					viewStorage.setStorage((Storage) cache);
					inv.setInventory();
				}
			}
		}
	}

	public static Cache getCache(OfflinePlayer player, String uuid, boolean draft) {
		Table table = ColdStorage.getPlugin().getDb().getTable(CacheTable.class);
		CacheTable storageTable = null;
		if (table instanceof CacheTable) storageTable = (CacheTable) table;
		else
			return null;
		return storageTable.getStorage(player, uuid, draft);
	}

	public static void addCache(OfflinePlayer player, Cache cache) {
		Table table = ColdStorage.getPlugin().getDb().getTable(CacheTable.class);
		CacheTable storageTable = null;
		if (table instanceof CacheTable) storageTable = (CacheTable) table;
		storageTable.addPlayerStorage(cache, player);
	}

	public static void deleteCache(Cache cache) {
		Table table = ColdStorage.getPlugin().getDb().getTable(CacheTable.class);
		CacheTable storageTable = null;
		if (table instanceof CacheTable) storageTable = (CacheTable) table;
		storageTable.deletePlayerStorage(cache);
	}

	public static void addStorageType(OfflinePlayer player, StorageType type) {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		storageTable.addStorageType(type, player);
	}

	public static void deleteStorageType(StorageType type) {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		storageTable.deleteStorageType(type);
	}

	public static void updateStorageType(OfflinePlayer player, StorageType type) {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		storageTable.setStorageType(type, player);
	}

	public static StorageType getStorageType(String type) {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		else
			return null;
		return storageTable.getType(type);
	}

	public static List<StorageType> getStorageTypes() {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		else
			return null;
		return storageTable.getAllTypes();
	}

	public static void removeStorageType(OfflinePlayer player, StorageType type) {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		storageTable.deleteStorageType(type);
	}

	public static void loadValues() {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		for(StorageType type: storageTable.getAllTypes())
			StorageType.add(type);
	}

	public static List<String> getStringPermissions(StorageType type) {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		else
			return null;
		return storageTable.getPermissions(type);
	}

	public static Permission getPermission(String permission) {
		Table table = ColdStorage.getPlugin().getDb().getTable(PermissionsTable.class);
		PermissionsTable permissionsTable = null;
		if (table instanceof PermissionsTable) permissionsTable = (PermissionsTable) table;
		else
			return null;
		return permissionsTable.getPermission(permission);
	}

	public static void addPermission(Permission permission) {
		Table table = ColdStorage.getPlugin().getDb().getTable(PermissionsTable.class);
		PermissionsTable permissionsTable = null;
		if (table instanceof PermissionsTable) permissionsTable = (PermissionsTable) table;
		permissionsTable.addPermission(permission);
	}

	public static void removePermission(String permission, OfflinePlayer player) {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		if (storageTable.removePermissionFromTypes(permission, player)) {
			table = ColdStorage.getPlugin().getDb().getTable(PermissionsTable.class);
			PermissionsTable permissionsTable = null;
			if (table instanceof PermissionsTable) permissionsTable = (PermissionsTable) table;
			permissionsTable.deletePermission(permission);
		} else if (player.isOnline()) Chatable.get().sendMessage(player.getPlayer(), Chatable.get().getMessage(ChatUtils.getCodes(), "exceptions.permissions_not_updated"));
		else
			Chatable.get().sendWarning(Chatable.get().getMessage(ChatUtils.getCodes(), "exceptions.permissions_not_updated"));
	}

	public static void updatePermission(Permission permission) {
		Table table = ColdStorage.getPlugin().getDb().getTable(PermissionsTable.class);
		PermissionsTable permissionsTable = null;
		if (table instanceof PermissionsTable) permissionsTable = (PermissionsTable) table;
		permissionsTable.setPermission(permission);
	}

	public static void addPermissionToType(StorageType type, String permission, OfflinePlayer player) {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		storageTable.addPermission(type, permission, player);
	}

	public static void removePermissionFromType(StorageType type, String permission, OfflinePlayer player) {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		storageTable.removePermission(type, permission, player);
	}

	public static void setPermissionsForType(StorageType type, List<String> permissions, OfflinePlayer player) {
		Table table = ColdStorage.getPlugin().getDb().getTable(StorageTypeTable.class);
		StorageTypeTable storageTable = null;
		if (table instanceof StorageTypeTable) storageTable = (StorageTypeTable) table;
		storageTable.setPermissions(type, permissions, player);
	}

	public static List<Permission> getPermissions() {
		Table table = ColdStorage.getPlugin().getDb().getTable(PermissionsTable.class);
		PermissionsTable permissionsTable = null;
		if (table instanceof PermissionsTable) permissionsTable = (PermissionsTable) table;
		else
			return null;
		return permissionsTable.getPermissions();
	}

	public static List<Permission> getGlobalPermissions() {
		Table table = ColdStorage.getPlugin().getDb().getTable(GlobalPermissionsTable.class);
		GlobalPermissionsTable permissionsTable = null;
		if (table instanceof GlobalPermissionsTable) permissionsTable = (GlobalPermissionsTable) table;
		else
			return null;
		return permissionsTable.getPermissions();
	}

	public static Permission getGlobalPermission(String permission) {
		Table table = ColdStorage.getPlugin().getDb().getTable(GlobalPermissionsTable.class);
		GlobalPermissionsTable permissionsTable = null;
		if (table instanceof GlobalPermissionsTable) permissionsTable = (GlobalPermissionsTable) table;
		else
			return null;
		return permissionsTable.getPermission(permission);
	}

	public static void addGlobalPermission(Permission permission) {
		Table table = ColdStorage.getPlugin().getDb().getTable(GlobalPermissionsTable.class);
		GlobalPermissionsTable permissionsTable = null;
		if (table instanceof GlobalPermissionsTable) permissionsTable = (GlobalPermissionsTable) table;
		permissionsTable.addPermission(permission);
	}

	public static void removeGlobalPermission(String permission) {
		Table table = ColdStorage.getPlugin().getDb().getTable(GlobalPermissionsTable.class);
		GlobalPermissionsTable permissionsTable = null;
		if (table instanceof GlobalPermissionsTable) permissionsTable = (GlobalPermissionsTable) table;
		permissionsTable.deletePermission(permission);
	}

	public static void updateGlobalPermission(Permission permission) {
		Table table = ColdStorage.getPlugin().getDb().getTable(GlobalPermissionsTable.class);
		GlobalPermissionsTable permissionsTable = null;
		if (table instanceof GlobalPermissionsTable) permissionsTable = (GlobalPermissionsTable) table;
		permissionsTable.setPermission(permission);
	}

	public static boolean hasChest(Block block) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTable.class);
		ChestTable chestTable = null;
		if (table instanceof ChestTable) chestTable = (ChestTable) table;
		else
			return true;
		return chestTable.hasChest(block.getLocation());
	}

	public static boolean deleteChest(Chest chest) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTable.class);
		ChestTable chestTable = null;
		if (table instanceof ChestTable) chestTable = (ChestTable) table;
		else
			return false;
		return chestTable.deleteChest(chest);
	}

	public static Chest getChest(Location loc) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTable.class);
		ChestTable chestTable = null;
		if (table instanceof ChestTable) chestTable = (ChestTable) table;
		else
			return null;
		return chestTable.getChest(loc);
	}

	public static Chest getChest(String unique) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTable.class);
		ChestTable chestTable = null;
		if (table instanceof ChestTable) chestTable = (ChestTable) table;
		else
			return null;
		return chestTable.getChest(unique);
	}

	public static void addDoubleChest(Location loc, Location newLoc) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTable.class);
		ChestTable chestTable = null;
		if (table instanceof ChestTable) chestTable = (ChestTable) table;
		Chest chest = chestTable.getChest(loc);
		if (chest != null) {
			chest.setDoubleLoc(newLoc);
			chestTable.setChest(chest);
		}
	}

	public static boolean addChest(Chest chest) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTable.class);
		ChestTable chestTable = null;
		if (table instanceof ChestTable) chestTable = (ChestTable) table;
		else
			return false;
		return chestTable.addChest(chest);
	}

	public static List<Chest> getChests(OfflinePlayer player) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTable.class);
		ChestTable chestTable = null;
		if (table instanceof ChestTable) chestTable = (ChestTable) table;
		else
			return null;
		return chestTable.getChests(player);
	}

	public static List<ChestTypeRecord> getChestTypes(Chest chest) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTypeTable.class);
		ChestTypeTable chestTable = null;
		if (table instanceof ChestTypeTable) chestTable = (ChestTypeTable) table;
		else
			return null;
		return chestTable.getChestTypes(chest);
	}

	public static ChestType getChestType(Storage storage, Chest chest) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTypeTable.class);
		ChestTypeTable chestTable = null;
		if (table instanceof ChestTypeTable) chestTable = (ChestTypeTable) table;
		else
			return null;
		return chestTable.getChestType(chest, storage);
	}

	public static void addChestType(Storage storage, Chest chest, ChestType type) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTypeTable.class);
		ChestTypeTable chestTable = null;
		if (table instanceof ChestTypeTable) chestTable = (ChestTypeTable) table;
		chestTable.addChestType(chest, storage, type);
	}

	public static void deleteChestType(Storage storage, Chest chest) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTypeTable.class);
		ChestTypeTable chestTable = null;
		if (table instanceof ChestTypeTable) chestTable = (ChestTypeTable) table;
		chestTable.deleteChestType(chest, storage);
	}

	public static void deleteChestType(String storageUnique, Chest chest) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTypeTable.class);
		ChestTypeTable chestTable = null;
		if (table instanceof ChestTypeTable) chestTable = (ChestTypeTable) table;
		chestTable.deleteChestType(chest, storageUnique);
	}

	public static List<ChestTypeRecord> getChestTypes(ChestType type) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTypeTable.class);
		ChestTypeTable chestTable = null;
		if (table instanceof ChestTypeTable) chestTable = (ChestTypeTable) table;
		else
			return null;
		return chestTable.getChestTypes(type);
	}

	public static List<ChestTypeRecord> getChestTypes(Storage storage, ChestType type) {
		Table table = ColdStorage.getPlugin().getDb().getTable(ChestTypeTable.class);
		ChestTypeTable chestTable = null;
		if (table instanceof ChestTypeTable) chestTable = (ChestTypeTable) table;
		else
			return null;
		return chestTable.getChestTypes(storage, type);
	}
}

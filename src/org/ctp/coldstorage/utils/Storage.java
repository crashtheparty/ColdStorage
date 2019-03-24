package org.ctp.coldstorage.utils;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.database.tables.StorageTable;
import org.ctp.coldstorage.database.tables.Table;
import org.ctp.coldstorage.utils.config.ItemSerialization;

public class Storage {
	
	private int amount, orderBy;
	private OfflinePlayer player;
	private Material material;
	private String unique, meta;
	
	public Storage(OfflinePlayer player, String unique, ItemStack item, int amount, int order) {
		setPlayer(player);
		setUnique(unique);
		setMaterial(item.getType());
		setAmount(amount);
		setMeta(ItemSerialization.itemToData(item));
		setOrderBy(order);
	}
	
	public Storage(OfflinePlayer player, String unique, Material material, int amount, String meta, int order) {
		setPlayer(player);
		setUnique(unique);
		setMaterial(material);
		setAmount(amount);
		setMeta(meta);
		setOrderBy(order);
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}
	
	public static Storage getStorage(OfflinePlayer player, String id) {
		Table table = ColdStorage.getDb().getTable(StorageTable.class);
		StorageTable storageTable = null;
		if(table instanceof StorageTable) {
			storageTable = (StorageTable) table;
		} else {
			return null;
		}
		return storageTable.getStorage(player, id);
	}
	
	public void updateStorage(OfflinePlayer player) {
		Table table = ColdStorage.getDb().getTable(StorageTable.class);
		StorageTable storageTable = null;
		if(table instanceof StorageTable) {
			storageTable = (StorageTable) table;
		} else {
			return;
		}
		storageTable.setPlayerStorage(this, player);
	}
	
	public void deleteStorage(Player deleted) {
		if(deleted.hasPermission("coldstorage.delete")) {
			Table table = ColdStorage.getDb().getTable(StorageTable.class);
			StorageTable storageTable = null;
			if(table instanceof StorageTable) {
				storageTable = (StorageTable) table;
			} else {
				return;
			}
			storageTable.deletePlayerStorage(this);
		}
	}

	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

}

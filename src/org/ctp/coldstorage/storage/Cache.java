package org.ctp.coldstorage.storage;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.utils.config.ItemSerialization;

public abstract class Cache {

	private OfflinePlayer player;
	private StorageType storageType;
	private String unique, meta, name, storageTypeString;
	private Material material;
	
	public Cache(OfflinePlayer player, String unique, ItemStack item, StorageType storageType, String name) {
		setPlayer(player);
		setUnique(unique);
		if(item != null) {
			setMaterial(item.getType());
			setMeta(ItemSerialization.itemToData(item));
		} else {
			setMaterial(Material.AIR);
			setMeta("");
		}
		setStorageType(storageType);
		setName(name);
	}
	
	public Cache(OfflinePlayer player, String unique, ItemStack item, String storageType, String name) {
		setPlayer(player);
		setUnique(unique);
		if(item != null) {
			setMaterial(item.getType());
			setMeta(ItemSerialization.itemToData(item));
		} else {
			setMaterial(Material.AIR);
			setMeta("");
		}
		setStorageType(storageType);
		setName(name);
	}
	
	public Cache(OfflinePlayer player, String unique, Material material, String meta, String storageType, String name) {
		setPlayer(player);
		setUnique(unique);
		setMaterial(material);
		setMeta(meta);
		setStorageType(storageType);
		setName(name);
	}
	
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}
	
	public String getUnique() {
		return unique;
	}
	
	public void setUnique(String unique) {
		this.unique = unique;
	}
	
	public String getMeta() {
		return meta;
	}
	
	public void setMeta(String meta) {
		this.meta = meta;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
		if(storageType != null) {
			this.storageTypeString = storageType.getType();
		}
	}
	
	public void setStorageType(String storageType) {
		this.storageTypeString = storageType;
		this.storageType = StorageType.getStorageType(storageType);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStorageTypeString() {
		return storageTypeString;
	}
}

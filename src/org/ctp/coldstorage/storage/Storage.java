package org.ctp.coldstorage.storage;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class Storage extends Cache {

	private int storedAmount, orderBy;
	private boolean canInsertAll;

	public Storage(OfflinePlayer player, String unique, ItemStack item, StorageType storageType, String name, int storedAmount, int orderBy) {
		super(player, unique, item, storageType, name);
		setOrderBy(orderBy);
		setStoredAmount(storedAmount);
		setCanInsertAll(true);
	}

	public Storage(OfflinePlayer player, String unique, String materialName, String meta, String storageType, String name, int storedAmount, int orderBy,
	boolean canInsertAll) {
		super(player, unique, materialName, meta, storageType, name);
		setOrderBy(orderBy);
		setStoredAmount(storedAmount);
		setCanInsertAll(canInsertAll);
	}

	public int getStoredAmount() {
		return storedAmount;
	}

	public void setStoredAmount(int storedAmount) {
		this.storedAmount = storedAmount;
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

	public boolean canInsertAll() {
		return canInsertAll;
	}

	public void setCanInsertAll(boolean canInsertAll) {
		this.canInsertAll = canInsertAll;
	}
}

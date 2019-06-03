package org.ctp.coldstorage.storage;

import org.ctp.coldstorage.storage.Chest.ChestType;

public class ChestTypeRecord {

	private String chestUUID, storageUUID;
	private ChestType type;
	
	public ChestTypeRecord(String chestUUID, String storageUUID, ChestType type) {
		this.setChestUUID(chestUUID);
		this.setStorageUUID(storageUUID);
		this.setType(type);
	}

	public String getChestUUID() {
		return chestUUID;
	}

	private void setChestUUID(String chestUUID) {
		this.chestUUID = chestUUID;
	}

	public String getStorageUUID() {
		return storageUUID;
	}

	private void setStorageUUID(String storageUUID) {
		this.storageUUID = storageUUID;
	}

	public ChestType getType() {
		return type;
	}

	private void setType(ChestType type) {
		this.type = type;
	}
}

package org.ctp.coldstorage.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.database.tables.StorageTable;
import org.ctp.coldstorage.database.tables.Table;

public class StorageList {

	private int page;
	private OfflinePlayer player;
	
	private static List<StorageList> STORAGE_LISTS = new ArrayList<StorageList>();
	
	public StorageList(OfflinePlayer player) {
		this.player = player;
		STORAGE_LISTS.add(this);
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	public static StorageList getList(OfflinePlayer player) {
		for(StorageList list : STORAGE_LISTS) {
			if(list.getPlayer().getUniqueId().toString().equals(player.getUniqueId().toString())) {
				return list;
			}
		}
		return null;
	}
	
	public List<Storage> getStorages(){
		Table table = ColdStorage.getDb().getTable(StorageTable.class);
		StorageTable storageTable = null;
		if(table instanceof StorageTable) {
			storageTable = (StorageTable) table;
		} else {
			return null;
		}
		return storageTable.getPlayerStorage(player);
	}
}

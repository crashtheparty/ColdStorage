package org.ctp.coldstorage.storage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.database.tables.CacheTable;
import org.ctp.coldstorage.database.tables.Table;

public class StorageList {

	private int page;
	private OfflinePlayer player;
	private List<Cache> storages, drafts;
	private static List<StorageList> STORAGE_LISTS = new ArrayList<StorageList>();
	
	public StorageList(OfflinePlayer player) {
		this.player = player;
		Table table = ColdStorage.getPlugin().getDb().getTable(CacheTable.class);
		CacheTable storageTable = null;
		if(table instanceof CacheTable) {
			storageTable = (CacheTable) table;
			storages = storageTable.getPlayerStorage(player, false);
			drafts = storageTable.getPlayerStorage(player, true);
		}
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
	
	public List<Cache> getStorages(){
		return storages;
	}

	public List<Cache> getDrafts() {
		return drafts;
	}
	
	public void update() {
		Table table = ColdStorage.getPlugin().getDb().getTable(CacheTable.class);
		CacheTable storageTable = null;
		if(table instanceof CacheTable) {
			storageTable = (CacheTable) table;
			storages = storageTable.getPlayerStorage(player, false);
			drafts = storageTable.getPlayerStorage(player, true);
		}
	}
	
	public static StorageList getList(OfflinePlayer player) {
		for(StorageList list : STORAGE_LISTS) {
			if(list.getPlayer().equals(player)) {
				return list;
			}
		}
		return new StorageList(player);
	}
}

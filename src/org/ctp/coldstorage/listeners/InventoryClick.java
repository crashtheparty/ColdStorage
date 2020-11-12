package org.ctp.coldstorage.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.inventory.admin.*;
import org.ctp.coldstorage.inventory.draft.DraftList;
import org.ctp.coldstorage.inventory.draft.StorageTypeList;
import org.ctp.coldstorage.inventory.draft.ViewDraft;
import org.ctp.coldstorage.inventory.storage.EditChests;
import org.ctp.coldstorage.inventory.storage.ListStorage;
import org.ctp.coldstorage.inventory.storage.ViewStorage;
import org.ctp.coldstorage.utils.inventory.InventoryClickUtils;
import org.ctp.crashapi.inventory.InventoryData;

public class InventoryClick implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = null;
		if (event.getWhoClicked() instanceof Player) player = (Player) event.getWhoClicked();
		else
			return;
		InventoryData csInv = ColdStorage.getPlugin().getInventory(player);

		if (csInv != null) {
			if (csInv instanceof Anvilable) if (((Anvilable) csInv).willEdit()) return;
			Inventory inv = event.getClickedInventory();
			if (inv == null) return;
			event.setCancelled(true);

			if (csInv instanceof ListStorage) InventoryClickUtils.viewStorageList(event, player, (ListStorage) csInv);
			else if (csInv instanceof ViewStorage) InventoryClickUtils.viewStorage(event, player, (ViewStorage) csInv);
			else if (csInv instanceof DraftList) InventoryClickUtils.viewDraftList(event, player, (DraftList) csInv);
			else if (csInv instanceof ViewDraft) InventoryClickUtils.viewDraft(event, player, (ViewDraft) csInv);
			else if (csInv instanceof StorageTypeList) InventoryClickUtils.storageTypeList(event, player, (StorageTypeList) csInv);
			else if (csInv instanceof EditStorageTypeList) InventoryClickUtils.editStorageTypeList(event, player, (EditStorageTypeList) csInv);
			else if (csInv instanceof EditStorageType) InventoryClickUtils.editStorageType(event, player, (EditStorageType) csInv);
			else if (csInv instanceof EditTypePermissions) InventoryClickUtils.editTypePermissions(event, player, (EditTypePermissions) csInv);
			else if (csInv instanceof ListPermissions) InventoryClickUtils.listPermissions(event, player, (ListPermissions) csInv);
			else if (csInv instanceof ViewPermission) InventoryClickUtils.viewPermission(event, player, (ViewPermission) csInv);
			else if (csInv instanceof AdminList) InventoryClickUtils.adminList(event, player, (AdminList) csInv);
			else if (csInv instanceof ListGlobalPermissions) InventoryClickUtils.listGlobalPermissions(event, player, (ListGlobalPermissions) csInv);
			else if (csInv instanceof ViewGlobalPermission) InventoryClickUtils.viewGlobalPermission(event, player, (ViewGlobalPermission) csInv);
			else if (csInv instanceof EditChests) InventoryClickUtils.editChests(event, player, (EditChests) csInv);
			else if (csInv instanceof PlayerList) InventoryClickUtils.playerList(event, player, (PlayerList) csInv);
		}
	}
}

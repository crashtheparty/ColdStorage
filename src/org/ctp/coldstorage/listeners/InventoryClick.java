package org.ctp.coldstorage.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.handlers.ColdStorageInventory;
import org.ctp.coldstorage.handlers.ColdStorageInventory.Screen;
import org.ctp.coldstorage.utils.ChatUtilities;
import org.ctp.coldstorage.utils.InventoryUtilities;
import org.ctp.coldstorage.utils.Storage;
import org.ctp.coldstorage.utils.StorageList;
import org.ctp.coldstorage.utils.config.ConfigUtilities;
import org.ctp.coldstorage.utils.config.ItemSerialization;

public class InventoryClick implements Listener{
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		Player player = null;
		if (event.getWhoClicked() instanceof Player) {
			player = (Player) event.getWhoClicked();
		} else {
			return;
		}
		ColdStorageInventory csInv = InventoryUtilities.getInventory(player);
		
		if(csInv != null) {
			if(csInv.isEditing())
				return;
			Inventory inv = event.getClickedInventory();
			Inventory openedInv = event.getInventory();
			if (inv == null)
				return;
			event.setCancelled(true);
						
			if(csInv.getScreen() == Screen.LIST) {
				StorageList storageList = StorageList.getList(player);
				if(storageList == null) {
					return;
				}
				if(inv.equals(openedInv)){
					switch(event.getSlot()){
						case 45:
							if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
								csInv.listColdStorage(storageList.getPage() - 1);
							}
							break;
						case 53:
							if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
								csInv.listColdStorage(storageList.getPage() + 1);
							}
							break;
						case 49:
							if(openedInv.getItem(49) != null && openedInv.getItem(49).getType() == Material.PAPER) {
								csInv.selectColdStorageType();
							}
							break;
						case 39:
							if(openedInv.getItem(39) != null && openedInv.getItem(39).getType() == Material.PAPER) {
								csInv.listEditColdStorage(storageList.getPage());
							}
							break;
						case 41:
							if(openedInv.getItem(41) != null && openedInv.getItem(41).getType() == Material.BARRIER) {
								csInv.listDeleteColdStorage(storageList.getPage());
							}
							break;
						
					}
					if(event.getSlot() < 36) {
						ItemStack item = openedInv.getItem(event.getSlot());
						if(item != null && item.getType() != Material.AIR) {
							csInv.openColdStorage(openedInv.getItem(event.getSlot()));
						}
					}
				}
			} else if(csInv.getScreen() == Screen.EDIT) {
				StorageList storageList = StorageList.getList(player);
				if(storageList == null) {
					return;
				}
				if(inv.equals(openedInv)){
					switch(event.getSlot()){
						case 45:
							if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
								csInv.listEditColdStorage(storageList.getPage() - 1);
							}
							break;
						case 53:
							if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
								csInv.listEditColdStorage(storageList.getPage() + 1);
							}
							break;
						case 39:
							if(openedInv.getItem(39) != null && openedInv.getItem(39).getType() == Material.BOOK) {
								csInv.listColdStorage(storageList.getPage());
							}
							break;
						case 41:
							if(openedInv.getItem(41) != null && openedInv.getItem(41).getType() == Material.BARRIER) {
								csInv.listDeleteColdStorage(storageList.getPage());
							}
						
					}
					if(event.getSlot() < 36) {
						ItemStack item = openedInv.getItem(event.getSlot());
						if(item != null && item.getType() != Material.AIR) {
							csInv.editColdStorage(openedInv.getItem(event.getSlot()));
						}
					}
				}
			} else if(csInv.getScreen() == Screen.DELETE) {
				StorageList storageList = StorageList.getList(player);
				if(storageList == null) {
					return;
				}
				if(inv.equals(openedInv)){
					switch(event.getSlot()){
						case 45:
							if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
								csInv.listDeleteColdStorage(storageList.getPage() - 1);
							}
							break;
						case 53:
							if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
								csInv.listDeleteColdStorage(storageList.getPage() + 1);
							}
							break;
						case 39:
							if(openedInv.getItem(39) != null && openedInv.getItem(39).getType() == Material.PAPER) {
								csInv.listEditColdStorage(storageList.getPage());
							}
							break;
						case 41:
							if(openedInv.getItem(41) != null && openedInv.getItem(41).getType() == Material.BOOK) {
								csInv.listColdStorage(storageList.getPage());
							}
						
					}
					if(event.getSlot() < 36) {
						ItemStack item = openedInv.getItem(event.getSlot());
						if(item != null && item.getType() != Material.AIR) {
							csInv.deleteColdStorage(openedInv.getItem(event.getSlot()));
						}
					}
				}
			} else if (csInv.getScreen() == Screen.TYPE) {
				event.setCancelled(true);
				ItemStack item = event.getCurrentItem();
				if(item == null) {
					ChatUtilities.sendMessage(player, "Please select a valid item!");
				}
				csInv.createColdStorage(item);
			} else if (csInv.getScreen() == Screen.OPEN) {
				event.setCancelled(true);
				if(!inv.equals(openedInv)){
					ItemStack item = event.getCurrentItem();
					ItemStack storageItem = openedInv.getItem(4);
					String id = csInv.getUnique();
					if(storageItem != null && item != null) {
						if(storageItem.getType().equals(item.getType())) {
							String meta = ItemSerialization.itemToData(item);
							Storage storage = Storage.getStorage(player, id);
							String storageMeta = storage.getMeta();
							if(meta.equals(storageMeta)) {
								ItemStack replace = new ItemStack(Material.AIR);
								int newAmount = storage.getAmount() + item.getAmount();
								if(newAmount > ConfigUtilities.MAX_STORAGE_SIZE) {
									replace = new ItemStack(item.getType(), newAmount - ConfigUtilities.MAX_STORAGE_SIZE);
									newAmount = ConfigUtilities.MAX_STORAGE_SIZE;
									ChatUtilities.sendMessage(player, "Item limit reached!");
								}
								storage.setAmount(newAmount);
								storage.updateStorage(player);
								inv.setItem(event.getSlot(), replace);
								csInv.openColdStorage(storageItem);
							} else {
								ChatUtilities.sendMessage(player, "Item must have matching metadata!");
							}
						} else {
							ChatUtilities.sendMessage(player, "Not a valid item!");
						}
					}
				} else {
					if(event.getSlot() == 18) {
						ItemStack item = event.getCurrentItem();
						if(item.getType().equals(Material.ARROW)) {
							StorageList storageList = StorageList.getList(player);
							if(storageList == null) {
								player.closeInventory();
							} else {
								csInv.listColdStorage(storageList.getPage());
							}
						}
					} else {
						ItemStack item = event.getCurrentItem();
						ItemStack storageItem = openedInv.getItem(4);
						if(item != null) {
							String id = csInv.getUnique();
							Storage storage = Storage.getStorage(player, id);
							int amount = storageItem.getMaxStackSize();
							ItemStack itemAdd = ItemSerialization.dataToItem(storage.getMaterial(), amount, storage.getMeta());
							HashMap<Integer, ItemStack> leftOver;
							switch (event.getSlot()){
							case 4:
								if(storage.getAmount() < amount) {
									amount = storage.getAmount();
									itemAdd = ItemSerialization.dataToItem(storage.getMaterial(), amount, storage.getMeta());
								}
								leftOver = player.getInventory().addItem(itemAdd);
								if (!leftOver.isEmpty()) {
						            amount -= leftOver.get(0).getAmount();
						        }
								storage.setAmount(storage.getAmount() - amount);
								break;
							case 12:
								amount = InventoryUtilities.maxRemoveFromInventory(player, itemAdd);
								itemAdd = ItemSerialization.dataToItem(storage.getMaterial(), amount, storage.getMeta());
								player.getInventory().removeItem(itemAdd);
								storage.setAmount(storage.getAmount() + amount);
								break;
							case 14:
								amount = InventoryUtilities.maxAddToInventory(player, itemAdd);
								if(storage.getAmount() < amount) {
									amount = storage.getAmount();
								}
								itemAdd = ItemSerialization.dataToItem(storage.getMaterial(), amount, storage.getMeta());
								int left = InventoryUtilities.addItems(player, itemAdd);
								if (left > 0) {
						            amount -= left;
						        }
								storage.setAmount(storage.getAmount() - amount);
								break;
							}
							
							storage.updateStorage(player);
							csInv.openColdStorage(storageItem);
						}
					}
				}
			} else {
				event.setCancelled(true);
				Bukkit.getConsoleSender().sendMessage("Bad screen: " + csInv.getScreen());
			}
		}
	}

}
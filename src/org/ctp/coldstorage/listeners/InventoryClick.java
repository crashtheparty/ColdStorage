package org.ctp.coldstorage.listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ctp.coldstorage.utils.ChatUtilities;
import org.ctp.coldstorage.utils.InventoryUtilities;
import org.ctp.coldstorage.utils.Storage;
import org.ctp.coldstorage.utils.StorageList;
import org.ctp.coldstorage.utils.config.ConfigUtilities;
import org.ctp.coldstorage.utils.config.ItemSerialization;

public class InventoryClick implements Listener{
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		Player player = null;
		if (event.getWhoClicked() instanceof Player) {
			player = (Player) event.getWhoClicked();
		} else {
			return;
		}
		if (inv == null)
			return;
		if (openedInv.getName().contains("Cold Storage List")) {
			event.setCancelled(true);
			StorageList storageList = StorageList.getList(player);
			if(storageList == null) {
				return;
			}
			if(inv.equals(openedInv)){
				switch(event.getSlot()){
					case 45:
						if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
							InventoryUtilities.listColdStorage(player, storageList.getPage() - 1);
						}
						break;
					case 53:
						if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
							InventoryUtilities.listColdStorage(player, storageList.getPage() + 1);
						}
						break;
					case 49:
						if(openedInv.getItem(49) != null && openedInv.getItem(49).getType() == Material.PAPER) {
							InventoryUtilities.selectColdStorageType(player);
						}
						break;
					
				}
				if(event.getSlot() < 36) {
					InventoryUtilities.openColdStorage(player, openedInv.getItem(event.getSlot()));
				}
			}
		} else if (openedInv.getName().equals("Select Material for Cold Storage")) {
			event.setCancelled(true);
			ItemStack item = event.getCurrentItem();
			if(item == null) {
				ChatUtilities.sendMessage(player, "Please select a valid item!");
			}
			String meta = ItemSerialization.itemToData(item);
			InventoryUtilities.createColdStorage(player, item.getType(), meta);
		} else if (openedInv.getName().contains("Cold Storage:")) {
			event.setCancelled(true);
			if(!inv.equals(openedInv)){
				ItemStack item = event.getCurrentItem();
				ItemStack storageItem = openedInv.getItem(4);
				ItemMeta itemMeta = storageItem.getItemMeta();
				List<String> lore = itemMeta.getLore();
				String id = "";
				if(lore.size() > 0) {
					id = lore.get(0).split(ChatColor.GOLD + "Amount")[0];
				}
				if(id != "") {
					id = ChatUtilities.revealText(id);
				} else {
					return;
				}
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
							storage.updateStorage();
							inv.setItem(event.getSlot(), replace);
							InventoryUtilities.openColdStorage(player, storageItem);
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
							InventoryUtilities.listColdStorage(player, storageList.getPage());
						}
					}
				} else {
					ItemStack item = event.getCurrentItem();
					ItemStack storageItem = openedInv.getItem(4);
					if(item != null) {
						ItemMeta itemMeta = storageItem.getItemMeta();
						if(itemMeta == null) return;
						List<String> lore = itemMeta.getLore();
						String id = "";
						if(lore.size() > 0) {
							id = lore.get(0).split(ChatColor.GOLD + "Amount")[0];
						}
						if(id != "") {
							id = ChatUtilities.revealText(id);
						} else {
							return;
						}
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
						
						storage.updateStorage();
						InventoryUtilities.openColdStorage(player, storageItem);
					}
				}
			}
		}
	}

}
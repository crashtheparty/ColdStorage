package org.ctp.coldstorage.utils.inventory;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.inventory.admin.AdminList;
import org.ctp.coldstorage.inventory.admin.EditStorageType;
import org.ctp.coldstorage.inventory.admin.EditStorageTypeList;
import org.ctp.coldstorage.inventory.admin.EditTypePermissions;
import org.ctp.coldstorage.inventory.admin.ListGlobalPermissions;
import org.ctp.coldstorage.inventory.admin.ListPermissions;
import org.ctp.coldstorage.inventory.admin.PlayerList;
import org.ctp.coldstorage.inventory.admin.ViewGlobalPermission;
import org.ctp.coldstorage.inventory.admin.ViewPermission;
import org.ctp.coldstorage.inventory.draft.DraftList;
import org.ctp.coldstorage.inventory.draft.StorageTypeList;
import org.ctp.coldstorage.inventory.draft.ViewDraft;
import org.ctp.coldstorage.inventory.storage.EditChests;
import org.ctp.coldstorage.inventory.storage.ListStorage;
import org.ctp.coldstorage.inventory.storage.ViewStorage;
import org.ctp.coldstorage.storage.Chest.ChestType;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.coldstorage.storage.StorageList;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.config.ItemSerialization;

public class InventoryClickUtils {

	public static void viewStorageList(InventoryClickEvent event, Player player, ListStorage csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			StorageList storageList = StorageList.getList(player);
			switch(event.getSlot()){
				case 45:
					if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
						csInv.changePage(storageList.getPage() - 1);
					}
					break;
				case 53:
					if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
						csInv.changePage(storageList.getPage() + 1);
					}
					break;
				case 51:
					if(openedInv.getItem(51) != null && openedInv.getItem(51).getType() == Material.PAPER) {
						csInv.viewDraftList();
					}
					break;
				case 47:
					if(openedInv.getItem(47) != null && openedInv.getItem(47).getType() == Material.COBBLESTONE) {
						csInv.insertAll();
					}
					break;
				
			}
			if(event.getSlot() < 36) {
				ItemStack item = openedInv.getItem(event.getSlot());
				if(item != null && item.getType() != Material.AIR) {
					csInv.viewStorage(event.getSlot());
				}
			}
		}
	}
	
	public static void viewStorage(InventoryClickEvent event, Player player, ViewStorage csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		
		if(!inv.equals(openedInv)){
			Storage storage = csInv.getStorage();
			if(storage != null) {
				ItemStack item = event.getCurrentItem();
				if(item.getType() != storage.getMaterial()) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("%material%", storage.getMaterial());
					map.put("%clicked_material%", item.getType());
					ChatUtils.sendMessage(player, ChatUtils.getMessage(map, "exceptions.incompatible_material"));
					return;
				}
				if(!storage.getMeta().equals(ItemSerialization.itemToData(item))) {
					ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.incompatible_item"));
					return;
				}
				if(storage.getStorageType() == null) {
					ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes("%storage_type%", storage.getStorageTypeString()), "exceptions.bad_storage_type"));
					return;
				}
				storage = (Storage) DatabaseUtils.getCache(storage.getPlayer(), storage.getUnique(), false);
				ItemStack replace = new ItemStack(Material.AIR);
				int newAmount = storage.getStoredAmount() + item.getAmount();
				if(newAmount > storage.getStorageType().getMaxAmountBase()) {
					if(newAmount - storage.getStorageType().getMaxAmountBase() > item.getAmount()) {
						replace = item;
						newAmount = storage.getStoredAmount();
					} else {
						replace = item.clone();
						replace.setAmount(newAmount - storage.getStorageType().getMaxAmountBase());
						newAmount = storage.getStorageType().getMaxAmountBase();
					}
					ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.item_limit_reached"));
				}
				storage.setStoredAmount(newAmount);
				inv.setItem(event.getSlot(), replace);
				DatabaseUtils.updateCache(player, storage);
				csInv.setStorage((Storage) DatabaseUtils.getCache(storage.getPlayer(), storage.getUnique(), false));
				csInv.setInventory();
			} else {
				ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "exceptions.missing_storage"));
			}
		} else {
			if(event.getSlot() >= 18) {
				switch (event.getSlot()){
					case 22:
						if(openedInv.getItem(22) != null && openedInv.getItem(22).getType() == Material.DIAMOND) {
							csInv.openAnvil("set_amount");
						}
						break;
					case 27:
						if(openedInv.getItem(27) != null && openedInv.getItem(27).getType() == Material.ARROW) {
							csInv.viewList();
						}
						break;
					case 29:
						if(openedInv.getItem(29) != null && openedInv.getItem(29).getType() == Material.CHEST) {
							csInv.editChests(ChestType.IMPORT);
						}
						break;
					case 30:
						if(openedInv.getItem(30) != null && openedInv.getItem(30).getType() == Material.PAPER) {
							csInv.openAnvil("order");
						}
						break;
					case 31:
						if(openedInv.getItem(31) != null && openedInv.getItem(31).getType() == Material.NAME_TAG) {
							csInv.openAnvil("name");
						}
						break;
					case 32:
						if(openedInv.getItem(32) != null && openedInv.getItem(32).getType() == Material.COBBLESTONE) {
							csInv.toggleInsertAll();
						}
						break;
					case 33:
						if(openedInv.getItem(33) != null && openedInv.getItem(33).getType() == Material.HOPPER) {
							csInv.editChests(ChestType.EXPORT);
						}
						break;
					case 35:
						if(openedInv.getItem(35) != null && openedInv.getItem(35).getType() == Material.REDSTONE_BLOCK) {
							csInv.confirmDelete();
						}
						break;
				}
			} else {
				ItemStack item = event.getCurrentItem();
				ItemStack storageItem = openedInv.getItem(4);
				if(item != null) {
					Storage storage = csInv.getStorage();
					storage = (Storage) DatabaseUtils.getCache(storage.getPlayer(), storage.getUnique(), false);
					int amount = storageItem.getMaxStackSize();
					ItemStack itemAdd = ItemSerialization.dataToItem(storage.getMaterial(), amount, storage.getMeta());
					HashMap<Integer, ItemStack> leftOver;
					switch (event.getSlot()){
					case 4:
						if(storage.getStoredAmount() < amount) {
							amount = storage.getStoredAmount();
							itemAdd = ItemSerialization.dataToItem(storage.getMaterial(), amount, storage.getMeta());
						}
						leftOver = player.getInventory().addItem(itemAdd);
						if (!leftOver.isEmpty()) {
				            amount -= leftOver.get(0).getAmount();
				        }
						storage.setStoredAmount(storage.getStoredAmount() - amount);
						break;
					case 12:
						amount = InventoryUtils.maxRemoveFromInventory(storage, player, itemAdd);
						if(amount > 0) {
							itemAdd = ItemSerialization.dataToItem(storage.getMaterial(), amount, storage.getMeta());
							player.getInventory().removeItem(itemAdd);
							storage.setStoredAmount(storage.getStoredAmount() + amount);
						}
						break;
					case 14:
						amount = InventoryUtils.maxAddToInventory(player, itemAdd);
						if(storage.getStoredAmount() < amount) {
							amount = storage.getStoredAmount();
						}
						itemAdd = ItemSerialization.dataToItem(storage.getMaterial(), amount, storage.getMeta());
						int left = InventoryUtils.addItems(player, itemAdd);
						if (left > 0) {
				            amount -= left;
				        }
						storage.setStoredAmount(storage.getStoredAmount() - amount);
						break;
					}
					DatabaseUtils.updateCache(player, storage);
					csInv.setStorage((Storage) DatabaseUtils.getCache(storage.getPlayer(), storage.getUnique(), false));
					csInv.setInventory();
				}
			}
		}
	}
	
	public static void viewDraftList(InventoryClickEvent event, Player player, DraftList csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			StorageList storageList = StorageList.getList(player);
			switch(event.getSlot()){
				case 45:
					if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
						csInv.changePage(storageList.getPage() - 1);
					}
					break;
				case 53:
					if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
						csInv.changePage(storageList.getPage() + 1);
					}
					break;
				case 50:
					if(openedInv.getItem(50) != null && openedInv.getItem(50).getType() == Material.PAPER) {
						csInv.createNew();
					}
					break;
				case 48:
					if(openedInv.getItem(48) != null && openedInv.getItem(48).getType() == Material.BOOK) {
						csInv.viewList();
					}
					break;
				
			}
			if(event.getSlot() < 36) {
				ItemStack item = openedInv.getItem(event.getSlot());
				if(item != null && item.getType() != Material.AIR) {
					csInv.editDraft(event.getSlot());
				}
			}
		}
	}
	
	public static void viewDraft(InventoryClickEvent event, Player player, ViewDraft csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(csInv.isModifyItem()) {
			if(!inv.equals(openedInv)) {
				if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
					ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes("%item%", ItemSerialization.itemToString(event.getCurrentItem())), "inventory.click.item_set"));
					csInv.modifyItem(event.getCurrentItem());
					return;
				}
			} else {
				if(event.getSlot() == 23) {
					csInv.setModifyItem(false);
					ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "inventory.click.canceled"));
					return;
				}
			}
			ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "inventory.click.storage_item"));
			csInv.setInventory();
		} else if(inv.equals(openedInv)){
			switch(event.getSlot()){
			case 12:
				if(openedInv.getItem(12) != null && openedInv.getItem(12).getType() == Material.NAME_TAG) {
					csInv.openAnvil();
				}
				break;
			case 13:
				if(openedInv.getItem(13) != null) {
					csInv.setModifyItem(true);
					ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "inventory.click.storage_item"));
					csInv.setInventory();
				}
				break;
			case 14:
				if(openedInv.getItem(14) != null && openedInv.getItem(14).getType() == Material.IRON_BLOCK) {
					csInv.editStorageType();
				}
				break;
			case 18:
				if(openedInv.getItem(18) != null && openedInv.getItem(18).getType() == Material.ARROW) {
					csInv.viewDraftList();
				}
				break;
			case 26:
				if(openedInv.getItem(26) != null) {
					csInv.attemptBuy();
				}
				break;
			}
		}
	}
	
	public static void storageTypeList(InventoryClickEvent event, Player player, StorageTypeList csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			switch(event.getSlot()){
				case 27:
					if(openedInv.getItem(27) != null && openedInv.getItem(27).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() - 1);
					}
					break;
				case 35:
					if(openedInv.getItem(35) != null && openedInv.getItem(35).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() + 1);
					}
					break;
				case 31:
					if(openedInv.getItem(31) != null && openedInv.getItem(31).getType() == Material.ARROW) {
						csInv.viewDraft();
					}
					break;
				
			}
			if(event.getSlot() < 18) {
				ItemStack item = openedInv.getItem(event.getSlot());
				if(item != null && item.getType() != Material.AIR) {
					csInv.selectStorageType(event.getSlot());
				}
			}
		}
	}
	
	public static void editStorageTypeList(InventoryClickEvent event, Player player, EditStorageTypeList csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			switch(event.getSlot()){
				case 45:
					if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() - 1);
					}
					break;
				case 53:
					if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() + 1);
					}
					break;
				case 50:
					if(openedInv.getItem(50) != null && openedInv.getItem(50).getType() == Material.BOOK) {
						csInv.createNew();
					}
					break;
				case 48:
					if(openedInv.getItem(48) != null && openedInv.getItem(48).getType() == Material.ARROW) {
						csInv.viewAdminList();
					}
					break;
				
			}
			if(event.getSlot() < 36) {
				ItemStack item = openedInv.getItem(event.getSlot());
				if(item != null && item.getType() != Material.AIR) {
					csInv.editStorageType(event.getSlot());
				}
			}
		}
	}
	
	public static void editStorageType(InventoryClickEvent event, Player player, EditStorageType csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(csInv.isModifyItem()) {
			if(!inv.equals(openedInv)) {
				if(event.getCurrentItem() != null) {
					ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes("%item%", ItemSerialization.itemToString(event.getCurrentItem())), "inventory.click.set_item"));
					csInv.modifyItem(event.getCurrentItem());
					return;
				}
			} else {
				if(event.getSlot() == 23) {
					csInv.setModifyItem(false);
					ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "inventory.click.canceled"));
					return;
				}
			}
			ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "inventory.click.item_cost"));
		} else if(inv.equals(openedInv)){
			switch(event.getSlot()){
				case 20:
					if(openedInv.getItem(20) != null && openedInv.getItem(20).getType() == Material.CHEST) {
						csInv.openAnvil("max_import");
					}
					break;
				case 21:
					if(openedInv.getItem(21) != null && openedInv.getItem(21).getType() == Material.HOPPER) {
						csInv.openAnvil("max_export");
					}
					break;
				case 22:
					if(openedInv.getItem(22) != null && openedInv.getItem(22).getType() == Material.SUNFLOWER) {
						csInv.openAnvil("vault_cost");
					}
					break;
				case 23:
					if(openedInv.getItem(23) != null && openedInv.getItem(23).getType() == Material.EMERALD) {
						csInv.setModifyItem(true);
						ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "inventory.click.item_cost"));
					}
					break;
				case 24:
					if(openedInv.getItem(24) != null && openedInv.getItem(24).getType() == Material.GOLD_INGOT) {
						csInv.openAnvil("max_amount");
					}
					break;
				case 31:
					if(openedInv.getItem(31) != null && openedInv.getItem(31).getType() == Material.PAPER) {
						csInv.openPermissions();
					}
					break;
				case 36:
					if(openedInv.getItem(36) != null && openedInv.getItem(36).getType() == Material.ARROW) {
						csInv.listStorageType();
					}
					break;
				case 44:
					if(openedInv.getItem(44) != null && openedInv.getItem(44).getType() == Material.REDSTONE_BLOCK) {
						csInv.confirmDelete();
					}
					break;
				
			}
		}
	}

	public static void editTypePermissions(InventoryClickEvent event, Player player, EditTypePermissions csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			switch(event.getSlot()){
				case 45:
					if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() - 1);
					}
					break;
				case 53:
					if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() + 1);
					}
					break;
				case 49:
					if(openedInv.getItem(49) != null && openedInv.getItem(49).getType() == Material.ARROW) {
						csInv.editStorageType();
					}
					break;
				
			}
			if(event.getSlot() < 36) {
				ItemStack item = openedInv.getItem(event.getSlot());
				if(item != null && item.getType() != Material.AIR) {
					csInv.togglePermission(event.getSlot());
				}
			}
		}
	}

	public static void listPermissions(InventoryClickEvent event, Player player, ListPermissions csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			switch(event.getSlot()){
				case 45:
					if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() - 1);
					}
					break;
				case 53:
					if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() + 1);
					}
					break;
				case 50:
					if(openedInv.getItem(50) != null && openedInv.getItem(50).getType() == Material.BOOK) {
						csInv.createNew();
					}
					break;
				case 48:
					if(openedInv.getItem(48) != null && openedInv.getItem(48).getType() == Material.ARROW) {
						csInv.viewAdminList();
					}
					break;
				
			}
			if(event.getSlot() < 36) {
				ItemStack item = openedInv.getItem(event.getSlot());
				if(item != null && item.getType() != Material.AIR) {
					csInv.viewPermission(event.getSlot());
				}
			}
		}
	}

	public static void viewPermission(InventoryClickEvent event, Player player, ViewPermission csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			switch(event.getSlot()){
			case 12:
				if(openedInv.getItem(12) != null && openedInv.getItem(12).getType() == Material.COMPARATOR) {
					csInv.openAnvil("check_order");
				}
				break;
			case 14:
				if(openedInv.getItem(14) != null && openedInv.getItem(14).getType() == Material.CHEST) {
					csInv.openAnvil("num_storage");
				}
				break;
			case 18:
				if(openedInv.getItem(18) != null && openedInv.getItem(18).getType() == Material.ARROW) {
					csInv.viewPermissionList();
				}
				break;
			case 26:
				if(openedInv.getItem(26) != null && openedInv.getItem(26).getType() == Material.REDSTONE_BLOCK) {
					csInv.confirmDelete();
				}
				break;
			}
		}
	}
	
	public static void listGlobalPermissions(InventoryClickEvent event, Player player, ListGlobalPermissions csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			switch(event.getSlot()){
				case 45:
					if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() - 1);
					}
					break;
				case 53:
					if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() + 1);
					}
					break;
				case 50:
					if(openedInv.getItem(50) != null && openedInv.getItem(50).getType() == Material.BOOK) {
						csInv.createNew();
					}
					break;
				case 48:
					if(openedInv.getItem(48) != null && openedInv.getItem(48).getType() == Material.ARROW) {
						csInv.viewAdminList();
					}
					break;
				
			}
			if(event.getSlot() < 36) {
				ItemStack item = openedInv.getItem(event.getSlot());
				if(item != null && item.getType() != Material.AIR) {
					csInv.viewPermission(event.getSlot());
				}
			}
		}
	}

	public static void viewGlobalPermission(InventoryClickEvent event, Player player, ViewGlobalPermission csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			switch(event.getSlot()){
			case 12:
				if(openedInv.getItem(12) != null && openedInv.getItem(12).getType() == Material.COMPARATOR) {
					csInv.openAnvil("check_order");
				}
				break;
			case 14:
				if(openedInv.getItem(14) != null && openedInv.getItem(14).getType() == Material.CHEST) {
					csInv.openAnvil("num_storage");
				}
				break;
			case 18:
				if(openedInv.getItem(18) != null && openedInv.getItem(18).getType() == Material.ARROW) {
					csInv.viewPermissionList();
				}
				break;
			case 26:
				if(openedInv.getItem(26) != null && openedInv.getItem(26).getType() == Material.REDSTONE_BLOCK) {
					csInv.confirmDelete();
				}
				break;
			}
		}
	}
	
	public static void adminList(InventoryClickEvent event, Player player, AdminList csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			switch(event.getSlot()){
			case 10:
				if(openedInv.getItem(10) != null && openedInv.getItem(10).getType() == Material.NAME_TAG) {
					csInv.viewPermissionsList();
				}
				break;
			case 12:
				if(openedInv.getItem(12) != null && openedInv.getItem(12).getType() == Material.NAME_TAG) {
					csInv.viewGlobalPermissionsList();
				}
				break;
			case 14:
				if(openedInv.getItem(14) != null && openedInv.getItem(14).getType() == Material.ENDER_CHEST) {
					csInv.viewPlayerList();
				}
				break;
			case 16:
				if(openedInv.getItem(16) != null && openedInv.getItem(16).getType() == Material.CHEST) {
					csInv.viewStorageTypeList();
				}
				break;
			}
		}
	}

	public static void editChests(InventoryClickEvent event, Player player, EditChests csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			switch(event.getSlot()){
				case 45:
					if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() - 1);
					}
					break;
				case 53:
					if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() + 1);
					}
					break;
				case 49:
					if(openedInv.getItem(49) != null && openedInv.getItem(49).getType() == Material.ARROW) {
						csInv.viewStorage();
					}
					break;
				
			}
			if(event.getSlot() < 36) {
				ItemStack item = openedInv.getItem(event.getSlot());
				if(item != null && item.getType() != Material.AIR) {
					if(item.getType() == Material.APPLE) {
						ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "inventory.editchests.different_types"));
					} else {
						csInv.toggle(event.getSlot());
					}
				}
			}
		}
	}

	public static void playerList(InventoryClickEvent event, Player player, PlayerList csInv) {
		Inventory inv = event.getClickedInventory();
		Inventory openedInv = event.getInventory();
		if(inv.equals(openedInv)){
			switch(event.getSlot()){
				case 45:
					if(openedInv.getItem(45) != null && openedInv.getItem(45).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() - 1);
					}
					break;
				case 53:
					if(openedInv.getItem(53) != null && openedInv.getItem(53).getType() == Material.ARROW) {
						csInv.changePage(csInv.getPage() + 1);
					}
					break;
				case 49:
					if(openedInv.getItem(49) != null && openedInv.getItem(49).getType() == Material.ARROW) {
						csInv.viewAdminList();
					}
					break;
				
			}
			if(event.getSlot() < 36) {
				ItemStack item = openedInv.getItem(event.getSlot());
				if(item != null && item.getType() != Material.AIR) {
					csInv.openStorageList(event.getSlot());
				}
			}
		}
	}
}

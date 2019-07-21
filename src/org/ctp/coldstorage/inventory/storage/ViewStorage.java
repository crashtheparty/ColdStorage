package org.ctp.coldstorage.inventory.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.inventory.ColdStorageInventory;
import org.ctp.coldstorage.nms.AnvilGUI;
import org.ctp.coldstorage.storage.Chest.ChestType;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.config.ItemSerialization;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class ViewStorage implements ColdStorageInventory, Anvilable{

	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private boolean opening, editing, choice;
	private String editType;
	private Storage storage;

	public ViewStorage(OfflinePlayer player, Storage storage) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
		this.storage = storage;
	}
	
	public ViewStorage(OfflinePlayer player, OfflinePlayer admin, Storage storage) {
		this.player = player;
		this.admin = admin;
		if(this.admin != null && this.admin instanceof Player) {
			setShow((Player) this.admin); 
		} else {
			setShow((Player) this.player); 
		}
		this.storage = storage;
	}
	
	@Override
	public OfflinePlayer getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}

	@Override
	public OfflinePlayer getAdmin() {
		return admin;
	}

	@Override
	public void setAdmin(OfflinePlayer player) {
		this.admin = player;
	}

	@Override
	public Player getShow() {
		return show;
	}

	@Override
	public void setShow(Player player) {
		this.show = player;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@SuppressWarnings("serial")
	@Override
	public void setInventory() {
		choice = false;
		editing = false;
		editType = null;
		Inventory inv = Bukkit.createInventory(null, 36, ChatUtils.getMessage(getCodes("%name%", storage.getName()), "inventory.viewstorage.title"));
		inv = open(inv);
		
		ItemStack itemStack = ItemSerialization.dataToItem(storage.getMaterial(), 1, storage.getMeta());
		if(itemStack.getType() == Material.AIR) {
			itemStack.setType(Material.BARRIER);
			ItemMeta meta = itemStack.getItemMeta();
			meta.setDisplayName(ChatUtils.getMessage(getCodes(), "info.bad_material"));
			itemStack.setItemMeta(meta);
			inv.setItem(4, itemStack);
		} else {
			ItemMeta meta = itemStack.getItemMeta();
			meta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewstorage.take_stack"));
			
			List<String> takeAllLore = new ArrayList<String>();
			String maxAmount = storage.getStorageType() == null ? ChatUtils.getMessage(getCodes(), "info.unknown") : "" + storage.getStorageType().getMaxAmountBase();
			takeAllLore.addAll(ChatUtils.getMessages(getCodes(new HashMap<String, Object>() {{
				put("%stored_amount%", storage.getStoredAmount()); put("%max_size%", maxAmount);
			}}), "inventory.viewstorage.type_meta"));
			boolean first = true;
			
			for(String m : storage.getMeta().split(" ")) {
				if(first) {
					takeAllLore.add(ChatUtils.getMessage(getCodes("%meta%", m), "inventory.viewstorage.meta_first"));
				} else {
					takeAllLore.add(ChatUtils.getMessage(getCodes("%meta%", m), "inventory.viewstorage.meta"));
				}
			}
			meta.setLore(takeAllLore);
			itemStack.setItemMeta(meta);
			inv.setItem(4, itemStack);
		
			ItemStack fillInventory = new ItemStack(Material.DRAGON_BREATH);
			ItemMeta fillInventoryMeta = fillInventory.getItemMeta();
			fillInventoryMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewstorage.fill_inventory"));
			fillInventory.setItemMeta(fillInventoryMeta);
			inv.setItem(14, fillInventory);
			
			ItemStack emptyInventory = new ItemStack(Material.GLASS_BOTTLE);
			ItemMeta emptyInventoryMeta = emptyInventory.getItemMeta();
			emptyInventoryMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewstorage.empty_inventory"));
			emptyInventoryMeta.setLore(Arrays.asList(ChatUtils.getMessage(getCodes(), "inventory.viewstorage.empty_inventory_info")));
			emptyInventory.setItemMeta(emptyInventoryMeta);
			inv.setItem(12, emptyInventory);
	
			ItemStack importChest = new ItemStack(Material.CHEST);
			ItemMeta importChestMeta = importChest.getItemMeta();
			importChestMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewstorage.import_chest"));
			importChestMeta.setLore(ChatUtils.getMessages(getCodes("%max_import%", 
					storage.getStorageType() != null ? storage.getStorageType().getMaxImport() : 0), 
					"inventory.viewstorage.edit_import_chest"));
			importChest.setItemMeta(importChestMeta);
			inv.setItem(29, importChest);
			
			ItemStack exportChest = new ItemStack(Material.HOPPER);
			ItemMeta exportChestMeta = exportChest.getItemMeta();
			exportChestMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewstorage.export_chest"));
			exportChestMeta.setLore(ChatUtils.getMessages(getCodes("%max_export%", 
					storage.getStorageType() != null ? storage.getStorageType().getMaxExport() : 0), 
					"inventory.viewstorage.edit_export_chest"));
			exportChest.setItemMeta(exportChestMeta);
			inv.setItem(33, exportChest);
			
			ItemStack editName = new ItemStack(Material.NAME_TAG);
			ItemMeta editNameMeta = editName.getItemMeta();
			editNameMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewstorage.edit_name"));
			editName.setItemMeta(editNameMeta);
			inv.setItem(31, editName);
			
			ItemStack editOrder = new ItemStack(Material.PAPER);
			ItemMeta editOrderMeta = editOrder.getItemMeta();
			editOrderMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewstorage.edit_display_order"));
			editOrderMeta.setLore(ChatUtils.getMessages(getCodes("%order%", storage.getOrderBy()), "inventory.viewstorage.display_order_meta"));
			editOrder.setItemMeta(editOrderMeta);
			inv.setItem(30, editOrder);
			
			ItemStack editInsertAll = new ItemStack(Material.COBBLESTONE);
			ItemMeta editInsertAllMeta = editInsertAll.getItemMeta();
			editInsertAllMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewstorage.toggle_insert_all"));
			editInsertAllMeta.setLore(ChatUtils.getMessages(getCodes("%insert_all%", storage.canInsertAll()), "inventory.viewstorage.insert_all_meta"));
			editInsertAll.setItemMeta(editInsertAllMeta);
			inv.setItem(32, editInsertAll);
		}

		if(admin != null) {
			ItemStack setAmount = new ItemStack(Material.DIAMOND);
			ItemMeta setAmountMeta = setAmount.getItemMeta();
			setAmountMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewstorage.set_amount"));
			setAmount.setItemMeta(setAmountMeta);
			inv.setItem(22, setAmount);
		}
		
		if(storage.getStoredAmount() == 0) {
			ItemStack remove = new ItemStack(Material.REDSTONE_BLOCK);
			ItemMeta removeMeta = remove.getItemMeta();
			removeMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.create_remove.remove_storage"));
			remove.setItemMeta(removeMeta);
			inv.setItem(35, remove);
		}
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(27, back);
	}
	
	public void editChests(ChestType type) {
		close(false);
		InventoryUtils.addInventory(show, new EditChests(player, admin, storage, type));
	}
	
	public void viewList() {
		close(false);
		InventoryUtils.addInventory(show, new ListStorage(player, admin));
	}
	
	public void toggleInsertAll() {
		storage.setCanInsertAll(!storage.canInsertAll());
		DatabaseUtils.updateCache(show, storage);
		setInventory();
	}
	
	public void openAnvil(String type) {
		editing = true;
		editType = type;
		inventory = null;
		AnvilGUI.createAnvil(show, this, false);
	}
	
	public void confirmDelete() {
		editing = true;
		choice = true;
		inventory = null;
		AnvilGUI.createAnvil(show, this, true);
	}

	@Override
	public void close(boolean external) {
		if(InventoryUtils.getInventory(show) != null) {
			if(!editing) {
				InventoryUtils.removeInventory(show);
			}
			if(!external) {
				show.closeInventory();
			}
		}
	}

	@Override
	public Inventory open(Inventory inv) {
		opening = true;
		if(inventory == null) {
			inventory = inv;
			show.openInventory(inv);
		} else {
			if(inv.getSize() == inventory.getSize()) {
				inv = show.getOpenInventory().getTopInventory();
				inventory = inv;
			} else {
				inventory = inv;
				show.openInventory(inv);
			}
		}
		for(int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, new ItemStack(Material.AIR));
		}
		if(opening) {
			opening = false;
		}
		return inv;
	}

	@Override
	public HashMap<String, Object> getCodes() {
		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%player%", player.getName());
		if(admin != null) {
			codes.put("%admin%", admin.getName());
		}
		codes.put("%show%", show.getName());
		return codes;
	}
	
	public HashMap<String, Object> getCodes(String string, Object object) {
		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%player%", player.getName());
		if(admin != null) {
			codes.put("%admin%", admin.getName());
		}
		codes.put("%show%", show.getName());
		codes.put(string, object);
		return codes;
	}
	
	public HashMap<String, Object> getCodes(HashMap<String, Object> objects) {
		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%player%", player.getName());
		if(admin != null) {
			codes.put("%admin%", admin.getName());
		}
		codes.put("%show%", show.getName());
		codes.putAll(objects);
		return codes;
	}

	@Override
	public boolean isOpening() {
		return opening;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	@Override
	public boolean isEditing() {
		return editing;
	}

	@Override
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	@Override
	public void setItemName(String name) {
		editing = false;
		if(editType != null) {
			if(editType.equals("name")) {
				storage.setName(name);
				DatabaseUtils.updateCache(show, storage);
				editType = null;
				setInventory();
				return;
			} else if (editType.equals("order")) {
				int order = -1;
				editType = null;
				try {
					order = Integer.parseInt(name);
				} catch(NumberFormatException ex) {
					HashMap<String, Object> codes = getCodes();
					codes.put("%num%", name);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_format"));
					setInventory();
					return;
				}
				if(order < 0) {
					HashMap<String, Object> codes = getCodes();
					codes.put("%num%", order);
					codes.put("%lowest%", 0);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_too_low"));
					order = 0;
				}
				storage.setOrderBy(order);
				DatabaseUtils.updateCache(show, storage);
				setInventory();
				return;
			} else if (editType.equals("set_amount")) {
				int setAmount = 0;
				editType = null;
				try {
					setAmount = Integer.parseInt(name);
				} catch(NumberFormatException ex) {
					HashMap<String, Object> codes = getCodes();
					codes.put("%num%", name);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_format"));
					setInventory();
					return;
				}
				if(setAmount < 0) {
					HashMap<String, Object> codes = getCodes();
					codes.put("%num%", setAmount);
					codes.put("%lowest%", 0);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_too_low"));
					setAmount = 0;
				}
				storage.setStoredAmount(setAmount);
				DatabaseUtils.updateCache(show, storage);
				setInventory();
				return;
			}
		}
		HashMap<String, Object> codes = getCodes();
		codes.put("%option%", editType);
		codes.put("%area%", "Edit Storage");
		ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.option_nonexistent"));
		setInventory();
	}

	@Override
	public void setChoice(String choice) {
		this.choice = false;
		if(choice.equals("confirm")) {
			DatabaseUtils.deleteCache(storage);
			viewList();
			return;
		} else if (choice.equals("deny")) {
			setInventory();
			return;
		}
		HashMap<String, Object> codes = getCodes();
		codes.put("%option%", choice);
		codes.put("%area%", "Choice");
		ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.option_nonexistent"));
		setInventory();
	}

	@Override
	public boolean isChoice() {
		return choice;
	}

}

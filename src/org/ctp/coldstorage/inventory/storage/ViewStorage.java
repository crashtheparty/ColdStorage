package org.ctp.coldstorage.inventory.storage;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.inventory.ColdStorageData;
import org.ctp.coldstorage.nms.AnvilGUINMS;
import org.ctp.coldstorage.storage.Chest.ChestType;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.coldstorage.utils.DatabaseUtils;

public class ViewStorage extends ColdStorageData implements Anvilable {

	private boolean choice;
	private String editType;
	private Storage storage;
	
	public ViewStorage(Player player, Storage storage) {
		super(player);
		this.storage = storage;
	}

	public ViewStorage(Player player, OfflinePlayer editing, Storage storage) {
		super(player, editing);
		this.storage = storage;
	}

	@SuppressWarnings("serial")
	@Override
	public void setInventory() {
		choice = false;
		setEdit(false);
		editType = null;
		Inventory inv = Bukkit.createInventory(null, 36, getChat().getMessage(getCodes("%name%", storage.getName()), "inventory.viewstorage.title"));
		inv = open(inv);
		
		ItemStack itemStack = ColdStorage.getPlugin().getItemSerial().dataToItem(storage.getMaterial(), 1, storage.getMeta());
		if(itemStack.getType() == Material.AIR) {
			itemStack.setType(Material.BARRIER);
			ItemMeta meta = itemStack.getItemMeta();
			meta.setDisplayName(getChat().getMessage(getCodes(), "info.bad_material"));
			itemStack.setItemMeta(meta);
			inv.setItem(4, itemStack);
		} else {
			ItemMeta meta = itemStack.getItemMeta();
			meta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewstorage.take_stack"));
			
			List<String> takeAllLore = new ArrayList<String>();
			String maxAmount = storage.getStorageType() == null ? getChat().getMessage(getCodes(), "info.unknown") : "" + storage.getStorageType().getMaxAmountBase();
			takeAllLore.addAll(getChat().getMessages(getCodes(new HashMap<String, Object>() {{
				put("%stored_amount%", storage.getStoredAmount()); put("%max_size%", maxAmount);
			}}), "inventory.viewstorage.type_meta"));
			boolean first = true;
			
			for(String m : storage.getMeta().split(" "))
				if(first) takeAllLore.add(getChat().getMessage(getCodes("%meta%", m), "inventory.viewstorage.meta_first"));
				else
					takeAllLore.add(getChat().getMessage(getCodes("%meta%", m), "inventory.viewstorage.meta"));
			meta.setLore(takeAllLore);
			itemStack.setItemMeta(meta);
			inv.setItem(4, itemStack);
		
			ItemStack fillInventory = new ItemStack(Material.DRAGON_BREATH);
			ItemMeta fillInventoryMeta = fillInventory.getItemMeta();
			fillInventoryMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewstorage.fill_inventory"));
			fillInventory.setItemMeta(fillInventoryMeta);
			inv.setItem(14, fillInventory);
			
			ItemStack emptyInventory = new ItemStack(Material.GLASS_BOTTLE);
			ItemMeta emptyInventoryMeta = emptyInventory.getItemMeta();
			emptyInventoryMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewstorage.empty_inventory"));
			emptyInventoryMeta.setLore(Arrays.asList(getChat().getMessage(getCodes(), "inventory.viewstorage.empty_inventory_info")));
			emptyInventory.setItemMeta(emptyInventoryMeta);
			inv.setItem(12, emptyInventory);
	
			ItemStack importChest = new ItemStack(Material.CHEST);
			ItemMeta importChestMeta = importChest.getItemMeta();
			importChestMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewstorage.import_chest"));
			importChestMeta.setLore(getChat().getMessages(getCodes("%max_import%", 
					storage.getStorageType() != null ? storage.getStorageType().getMaxImport() : 0), 
					"inventory.viewstorage.edit_import_chest"));
			importChest.setItemMeta(importChestMeta);
			inv.setItem(29, importChest);
			
			ItemStack exportChest = new ItemStack(Material.HOPPER);
			ItemMeta exportChestMeta = exportChest.getItemMeta();
			exportChestMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewstorage.export_chest"));
			exportChestMeta.setLore(getChat().getMessages(getCodes("%max_export%", 
					storage.getStorageType() != null ? storage.getStorageType().getMaxExport() : 0), 
					"inventory.viewstorage.edit_export_chest"));
			exportChest.setItemMeta(exportChestMeta);
			inv.setItem(33, exportChest);
			
			ItemStack editName = new ItemStack(Material.NAME_TAG);
			ItemMeta editNameMeta = editName.getItemMeta();
			editNameMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewstorage.edit_name"));
			editName.setItemMeta(editNameMeta);
			inv.setItem(31, editName);
			
			ItemStack editOrder = new ItemStack(Material.PAPER);
			ItemMeta editOrderMeta = editOrder.getItemMeta();
			editOrderMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewstorage.edit_display_order"));
			editOrderMeta.setLore(getChat().getMessages(getCodes("%order%", storage.getOrderBy()), "inventory.viewstorage.display_order_meta"));
			editOrder.setItemMeta(editOrderMeta);
			inv.setItem(30, editOrder);
			
			ItemStack editInsertAll = new ItemStack(Material.COBBLESTONE);
			ItemMeta editInsertAllMeta = editInsertAll.getItemMeta();
			editInsertAllMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewstorage.toggle_insert_all"));
			editInsertAllMeta.setLore(getChat().getMessages(getCodes("%insert_all%", storage.canInsertAll()), "inventory.viewstorage.insert_all_meta"));
			editInsertAll.setItemMeta(editInsertAllMeta);
			inv.setItem(32, editInsertAll);
		}

		if(!getEditing().getUniqueId().equals(getPlayer().getUniqueId())) {
			ItemStack setAmount = new ItemStack(Material.DIAMOND);
			ItemMeta setAmountMeta = setAmount.getItemMeta();
			setAmountMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewstorage.set_amount"));
			setAmount.setItemMeta(setAmountMeta);
			inv.setItem(22, setAmount);
		}
		
		if(storage.getStoredAmount() == 0) {
			ItemStack remove = new ItemStack(Material.REDSTONE_BLOCK);
			ItemMeta removeMeta = remove.getItemMeta();
			removeMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.create_remove.remove_storage"));
			remove.setItemMeta(removeMeta);
			inv.setItem(35, remove);
		}
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(27, back);
	}
	
	public void editChests(ChestType type) {
		close(false);
		ColdStorage.getPlugin().addInventory(new EditChests(getPlayer(), getEditing(), storage, type));
	}
	
	public void viewList() {
		close(false);
		ColdStorage.getPlugin().addInventory(new ListStorage(getPlayer(), getEditing()));
	}
	
	public void toggleInsertAll() {
		storage.setCanInsertAll(!storage.canInsertAll());
		DatabaseUtils.updateCache(getPlayer(), storage);
		setInventory();
	}
	
	public void openAnvil(String type) {
		setEdit(true);
		editType = type;
		setInventoryNull();
		AnvilGUINMS.createAnvil(getPlayer(), this, false);
	}
	
	public void confirmDelete() {
		setEdit(true);
		choice = true;
		setInventoryNull();
		AnvilGUINMS.createAnvil(getPlayer(), this, true);
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	@Override
	public void setItemName(String name) {
		setEdit(false);
		if(editType != null) if(editType.equals("name")) {
			storage.setName(name);
			DatabaseUtils.updateCache(getPlayer(), storage);
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
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_format"));
				setInventory();
				return;
			}
			if(order < 0) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", order);
				codes.put("%lowest%", 0);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_too_low"));
				order = 0;
			}
			storage.setOrderBy(order);
			DatabaseUtils.updateCache(getPlayer(), storage);
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
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_format"));
				setInventory();
				return;
			}
			if(setAmount < 0) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", setAmount);
				codes.put("%lowest%", 0);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_too_low"));
				setAmount = 0;
			}
			storage.setStoredAmount(setAmount);
			DatabaseUtils.updateCache(getPlayer(), storage);
			setInventory();
			return;
		}
		HashMap<String, Object> codes = getCodes();
		codes.put("%option%", editType);
		codes.put("%area%", "Edit Storage");
		getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.option_nonexistent"));
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
		getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.option_nonexistent"));
		setInventory();
	}

	@Override
	public boolean isChoice() {
		return choice;
	}

}

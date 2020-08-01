package org.ctp.coldstorage.inventory.admin;

import java.util.HashMap;

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
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.DatabaseUtils;

public class EditStorageType extends ColdStorageData implements Anvilable {

	private boolean modifyItem = false, choice = false;
	private StorageType type;
	private String editType;

	public EditStorageType(Player player, StorageType type) {
		super(player);
		this.type = type;
	}

	public EditStorageType(Player player, OfflinePlayer editing, StorageType type) {
		super(player, editing);
		this.type = type;
	}

	@Override
	public void setInventory() {
		choice = false;
		setEdit(false);
		Inventory inv = Bukkit.createInventory(null, 45, getChat().getMessage(getCodes(), "inventory.editstoragetype.title"));
		inv = open(inv);

		HashMap<String, Object> nameCodes = getCodes();
		nameCodes.put("%name%", type.getType());
		ItemStack name = new ItemStack(Material.NAME_TAG);
		ItemMeta nameMeta = name.getItemMeta();
		nameMeta.setDisplayName(getChat().getMessage(nameCodes, "inventory.editstoragetype.name"));
		name.setItemMeta(nameMeta);
		inv.setItem(4, name);

		HashMap<String, Object> importCodes = getCodes();
		importCodes.put("%max_import%", type.getMaxImport());
		ItemStack importItems = new ItemStack(Material.CHEST);
		ItemMeta importItemsMeta = importItems.getItemMeta();
		importItemsMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.editstoragetype.import"));
		importItemsMeta.setLore(getChat().getMessages(importCodes, "inventory.editstoragetype.max_import"));
		importItems.setItemMeta(importItemsMeta);
		inv.setItem(20, importItems);

		HashMap<String, Object> exportCodes = getCodes();
		exportCodes.put("%max_export%", type.getMaxExport());
		ItemStack exportItems = new ItemStack(Material.HOPPER);
		ItemMeta exportItemsMeta = exportItems.getItemMeta();
		exportItemsMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.editstoragetype.export"));
		exportItemsMeta.setLore(getChat().getMessages(exportCodes, "inventory.editstoragetype.max_export"));
		exportItems.setItemMeta(exportItemsMeta);
		inv.setItem(21, exportItems);

		HashMap<String, Object> vaultCodes = getCodes();
		vaultCodes.put("%vault_cost%", type.getVaultCost());
		ItemStack vaultCost = new ItemStack(Material.SUNFLOWER);
		ItemMeta vaultCostMeta = vaultCost.getItemMeta();
		vaultCostMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.editstoragetype.vault_cost"));
		vaultCostMeta.setLore(getChat().getMessages(vaultCodes, "inventory.editstoragetype.get_vault_cost"));
		vaultCost.setItemMeta(vaultCostMeta);
		inv.setItem(22, vaultCost);

		HashMap<String, Object> itemCodes = getCodes();
		itemCodes.put("%item_cost%", ColdStorage.getPlugin().getItemSerial().itemToString(type.getItemCost()));
		ItemStack itemCost = new ItemStack(Material.EMERALD);
		ItemMeta itemCostMeta = itemCost.getItemMeta();
		itemCostMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.editstoragetype.item_cost"));
		itemCostMeta.setLore(getChat().getMessages(itemCodes, "inventory.editstoragetype.get_item_cost"));
		itemCost.setItemMeta(itemCostMeta);
		inv.setItem(23, itemCost);

		HashMap<String, Object> amountCodes = getCodes();
		amountCodes.put("%max_amount%", type.getMaxAmountBase());
		ItemStack maxAmount = new ItemStack(Material.GOLD_INGOT);
		ItemMeta maxAmountMeta = maxAmount.getItemMeta();
		maxAmountMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.editstoragetype.max_amount"));
		maxAmountMeta.setLore(getChat().getMessages(amountCodes, "inventory.editstoragetype.get_max_amount"));
		maxAmount.setItemMeta(maxAmountMeta);
		inv.setItem(24, maxAmount);

		ItemStack permissions = new ItemStack(Material.PAPER);
		ItemMeta permissionsMeta = permissions.getItemMeta();
		permissionsMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.editstoragetype.permissions"));
		permissionsMeta.setLore(type.getPermissions());
		permissions.setItemMeta(permissionsMeta);
		inv.setItem(31, permissions);

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(36, back);

		ItemStack delete = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta deleteMeta = delete.getItemMeta();
		deleteMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.create_remove.delete_type"));
		delete.setItemMeta(deleteMeta);
		inv.setItem(44, delete);
	}

	public void modifyItem(ItemStack item) {
		type.setItemCost(item);
		DatabaseUtils.updateStorageType(getPlayer(), type);
		setModifyItem(false);
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
		AnvilGUINMS.createAnvil(getPlayer(), this, false);
	}

	public void openPermissions() {
		close(false);
		ColdStorage.getPlugin().addInventory(new EditTypePermissions(getPlayer(), getEditing(), type));
	}

	public void listStorageType() {
		close(false);
		ColdStorage.getPlugin().addInventory(new EditStorageTypeList(getPlayer(), getEditing()));
	}

	public StorageType getType() {
		return type;
	}

	public void setType(StorageType type) {
		this.type = type;
	}

	@Override
	public void setItemName(String name) {
		setEdit(false);
		if (editType != null) if (editType.equals("vault_cost")) {
			double vaultCost = 0;
			try {
				vaultCost = Double.parseDouble(name);
			} catch (NumberFormatException ex) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", name);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_format"));
				setInventory();
				return;
			}
			if (vaultCost < 0) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", vaultCost);
				codes.put("%lowest%", 0);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_too_low"));
				vaultCost = 0;
			}
			type.setVaultCost(vaultCost);
			DatabaseUtils.updateStorageType(getPlayer(), type);
			setInventory();
			return;
		} else if (editType.equals("max_amount")) {
			int maxAmount = 0;
			try {
				maxAmount = Integer.parseInt(name);
			} catch (NumberFormatException ex) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", name);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_format"));
				setInventory();
				return;
			}
			if (maxAmount < 100) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", maxAmount);
				codes.put("%lowest%", 100);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_too_low"));
				maxAmount = 100;
			}
			type.setMaxAmountBase(maxAmount);
			DatabaseUtils.updateStorageType(getPlayer(), type);
			setInventory();
			return;
		} else if (editType.equals("max_import")) {
			int maxImport = 0;
			try {
				maxImport = Integer.parseInt(name);
			} catch (NumberFormatException ex) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", name);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_format"));
				setInventory();
				return;
			}
			if (maxImport < 0) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", maxImport);
				codes.put("%lowest%", 0);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_too_low"));
				maxImport = 0;
			}
			type.setMaxImport(maxImport);
			DatabaseUtils.updateStorageType(getPlayer(), type);
			setInventory();
			return;
		} else if (editType.equals("max_export")) {
			int maxExport = 0;
			try {
				maxExport = Integer.parseInt(name);
			} catch (NumberFormatException ex) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", name);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_format"));
				setInventory();
				return;
			}
			if (maxExport < 0) {
				HashMap<String, Object> codes = getCodes();
				codes.put("%num%", maxExport);
				codes.put("%lowest%", 0);
				getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.number_too_low"));
				maxExport = 0;
			}
			type.setMaxExport(maxExport);
			DatabaseUtils.updateStorageType(getPlayer(), type);
			setInventory();
			return;
		}
		HashMap<String, Object> codes = getCodes();
		codes.put("%option%", editType);
		codes.put("%area%", "Edit Storage Type");
		getChat().sendMessage(getPlayer(), getChat().getMessage(codes, "exceptions.option_nonexistent"));
		setInventory();
	}

	public boolean isModifyItem() {
		return modifyItem;
	}

	public void setModifyItem(boolean modifyItem) {
		this.modifyItem = modifyItem;
	}

	@Override
	public void setChoice(String choice) {
		this.choice = false;
		if (choice.equals("confirm")) {
			DatabaseUtils.deleteStorageType(type);
			listStorageType();
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

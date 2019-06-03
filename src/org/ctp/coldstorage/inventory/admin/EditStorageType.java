package org.ctp.coldstorage.inventory.admin;

import java.util.HashMap;

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
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.config.ItemSerialization;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class EditStorageType implements ColdStorageInventory, Anvilable{

	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private boolean opening = false, editing = false, modifyItem = false, choice = false;
	private StorageType type;
	private String editType;
	
	public EditStorageType(OfflinePlayer player, StorageType type) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
		this.type = type;
	}
	
	public EditStorageType(OfflinePlayer player, OfflinePlayer admin, StorageType type) {
		this.player = player;
		this.admin = admin;
		if(this.admin != null && this.admin instanceof Player) {
			setShow((Player) this.admin); 
		} else {
			setShow((Player) this.player); 
		}
		this.type = type;
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

	@Override
	public void setInventory() {
		choice = false;
		editing = false;
		Inventory inv = Bukkit.createInventory(null, 45, ChatUtils.getMessage(getCodes(), "inventory.editstoragetype.title"));
		inv = open(inv);
		
		HashMap<String, Object> nameCodes = getCodes();
		nameCodes.put("%name%", type.getType());
		ItemStack name = new ItemStack(Material.NAME_TAG);
		ItemMeta nameMeta = name.getItemMeta();
		nameMeta.setDisplayName(ChatUtils.getMessage(nameCodes, "inventory.editstoragetype.name"));
		name.setItemMeta(nameMeta);
		inv.setItem(4, name);

		HashMap<String, Object> importCodes = getCodes();
		importCodes.put("%max_import%", type.getMaxImport());
		ItemStack importItems = new ItemStack(Material.CHEST);
		ItemMeta importItemsMeta = importItems.getItemMeta();
		importItemsMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.editstoragetype.import"));
		importItemsMeta.setLore(ChatUtils.getMessages(importCodes, "inventory.editstoragetype.max_import"));
		importItems.setItemMeta(importItemsMeta);
		inv.setItem(20, importItems);

		HashMap<String, Object> exportCodes = getCodes();
		exportCodes.put("%max_export%", type.getMaxExport());
		ItemStack exportItems = new ItemStack(Material.HOPPER);
		ItemMeta exportItemsMeta = exportItems.getItemMeta();
		exportItemsMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.editstoragetype.export"));
		exportItemsMeta.setLore(ChatUtils.getMessages(exportCodes, "inventory.editstoragetype.max_export"));
		exportItems.setItemMeta(exportItemsMeta);
		inv.setItem(21, exportItems);

		HashMap<String, Object> vaultCodes = getCodes();
		vaultCodes.put("%vault_cost%", type.getVaultCost());
		ItemStack vaultCost = new ItemStack(Material.SUNFLOWER);
		ItemMeta vaultCostMeta = vaultCost.getItemMeta();
		vaultCostMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.editstoragetype.vault_cost"));
		vaultCostMeta.setLore(ChatUtils.getMessages(vaultCodes, "inventory.editstoragetype.get_vault_cost"));
		vaultCost.setItemMeta(vaultCostMeta);
		inv.setItem(22, vaultCost);

		HashMap<String, Object> itemCodes = getCodes();
		itemCodes.put("%item_cost%", ItemSerialization.itemToString(type.getItemCost()));
		ItemStack itemCost = new ItemStack(Material.EMERALD);
		ItemMeta itemCostMeta = itemCost.getItemMeta();
		itemCostMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.editstoragetype.item_cost"));
		itemCostMeta.setLore(ChatUtils.getMessages(itemCodes, "inventory.editstoragetype.get_item_cost"));
		itemCost.setItemMeta(itemCostMeta);
		inv.setItem(23, itemCost);

		HashMap<String, Object> amountCodes = getCodes();
		amountCodes.put("%max_amount%", type.getMaxAmountBase());
		ItemStack maxAmount = new ItemStack(Material.GOLD_INGOT);
		ItemMeta maxAmountMeta = maxAmount.getItemMeta();
		maxAmountMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.editstoragetype.max_amount"));
		maxAmountMeta.setLore(ChatUtils.getMessages(amountCodes, "inventory.editstoragetype.get_max_amount"));
		maxAmount.setItemMeta(maxAmountMeta);
		inv.setItem(24, maxAmount);
		
		ItemStack permissions = new ItemStack(Material.PAPER);
		ItemMeta permissionsMeta = permissions.getItemMeta();
		permissionsMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.editstoragetype.permissions"));
		permissionsMeta.setLore(type.getPermissions());
		permissions.setItemMeta(permissionsMeta);
		inv.setItem(31, permissions);
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(36, back);
		
		ItemStack delete = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta deleteMeta = delete.getItemMeta();
		deleteMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.create_remove.delete_type"));
		delete.setItemMeta(deleteMeta);
		inv.setItem(44, delete);
	}
	
	public void modifyItem(ItemStack item) {
		type.setItemCost(item);
		DatabaseUtils.updateStorageType(show, type);
		setModifyItem(false);
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
	
	public void openPermissions() {
		close(false);
		InventoryUtils.addInventory(show, new EditTypePermissions(player, admin, type));
	}
	
	public void listStorageType() {
		close(false);
		InventoryUtils.addInventory(show, new EditStorageTypeList(player, admin));
	}

	@Override
	public void close(boolean external) {
		if(InventoryUtils.getInventory(show) != null) {
			InventoryUtils.removeInventory(show);
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
	public boolean isOpening() {
		return opening;
	}

	@Override
	public boolean isEditing() {
		return editing;
	}

	@Override
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public StorageType getType() {
		return type;
	}

	public void setType(StorageType type) {
		this.type = type;
	}

	@Override
	public void setItemName(String name) {
		editing = false;
		if(editType != null) {
			if(editType.equals("vault_cost")) {
				double vaultCost = 0;
				try {
					vaultCost = Double.parseDouble(name);
				} catch(NumberFormatException ex) {
					HashMap<String, Object> codes = getCodes();
					codes.put("%num%", name);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_format"));
					setInventory();
					return;
				}
				if(vaultCost < 0) {
					HashMap<String, Object> codes = getCodes();
					codes.put("%num%", vaultCost);
					codes.put("%lowest%", 0);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_too_low"));
					vaultCost = 0;
				}
				type.setVaultCost(vaultCost);
				DatabaseUtils.updateStorageType(show, type);
				setInventory();
				return;
			} else if (editType.equals("max_amount")) {
				int maxAmount = 0;
				try {
					maxAmount = Integer.parseInt(name);
				} catch(NumberFormatException ex) {
					HashMap<String, Object> codes = getCodes();
					codes.put("%num%", name);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_format"));
					setInventory();
					return;
				}
				if(maxAmount < 100) {
					HashMap<String, Object> codes = getCodes();
					codes.put("%num%", maxAmount);
					codes.put("%lowest%", 100);
					ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_too_low"));
					maxAmount = 100;
				}
				type.setMaxAmountBase(maxAmount);
				DatabaseUtils.updateStorageType(show, type);
				setInventory();
				return;
			} else if(editType.equals("max_import")) {
					int maxImport = 0;
					try {
						maxImport = Integer.parseInt(name);
					} catch(NumberFormatException ex) {
						HashMap<String, Object> codes = getCodes();
						codes.put("%num%", name);
						ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_format"));
						setInventory();
						return;
					}
					if(maxImport < 0) {
						HashMap<String, Object> codes = getCodes();
						codes.put("%num%", maxImport);
						codes.put("%lowest%", 0);
						ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_too_low"));
						maxImport = 0;
					}
					type.setMaxImport(maxImport);
					DatabaseUtils.updateStorageType(show, type);
					setInventory();
					return;
				} else if (editType.equals("max_export")) {
					int maxExport = 0;
					try {
						maxExport = Integer.parseInt(name);
					} catch(NumberFormatException ex) {
						HashMap<String, Object> codes = getCodes();
						codes.put("%num%", name);
						ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_format"));
						setInventory();
						return;
					}
					if(maxExport < 0) {
						HashMap<String, Object> codes = getCodes();
						codes.put("%num%", maxExport);
						codes.put("%lowest%", 0);
						ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.number_too_low"));
						maxExport = 0;
					}
					type.setMaxExport(maxExport);
					DatabaseUtils.updateStorageType(show, type);
					setInventory();
					return;
				}
		}
		HashMap<String, Object> codes = getCodes();
		codes.put("%option%", editType);
		codes.put("%area%", "Edit Storage Type");
		ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.option_nonexistent"));
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
		if(choice.equals("confirm")) {
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
		ChatUtils.sendMessage(show, ChatUtils.getMessage(codes, "exceptions.option_nonexistent"));
		setInventory();
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
	public boolean isChoice() {
		return choice;
	}

}

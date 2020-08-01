package org.ctp.coldstorage.inventory.draft;

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
import org.ctp.coldstorage.inventory.storage.ListStorage;
import org.ctp.coldstorage.nms.AnvilGUINMS;
import org.ctp.coldstorage.storage.Draft;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.PriceUtils;

public class ViewDraft extends ColdStorageData implements Anvilable {

	private boolean modifyItem, choice;
	private Draft draft;

	public ViewDraft(Player player, Draft draft) {
		super(player);
		this.draft = draft;
	}

	public ViewDraft(Player player, OfflinePlayer editing, Draft draft) {
		super(player, editing);
		this.draft = draft;
	}

	@Override
	public void setInventory() {
		choice = false;
		HashMap<String, Object> nameCodes = getCodes();
		nameCodes.put("%name%", draft.getName());
		Inventory inv = Bukkit.createInventory(null, 27, getChat().getMessage(nameCodes, "inventory.viewdraft.title"));
		inv = open(inv);
		
		ItemStack name = new ItemStack(Material.NAME_TAG);
		ItemMeta nameMeta = name.getItemMeta();
		nameMeta.setDisplayName(getChat().getMessage(nameCodes, "inventory.viewdraft.name"));
		nameMeta.setLore(getChat().getMessages(getCodes(), "inventory.viewdraft.name_lore"));
		name.setItemMeta(nameMeta);
		inv.setItem(12, name);
		
		ItemStack itemType = ColdStorage.getPlugin().getItemSerial().dataToItem(draft.getMaterial(), 1, draft.getMeta());
		if(draft.getMaterial() == null) itemType.setType(Material.BARRIER);
		else if(itemType.getType() == Material.AIR) itemType.setType(Material.BARRIER);
		if(modifyItem) itemType.setType(Material.EMERALD);
		ItemMeta itemTypeMeta = itemType.getItemMeta();
		String item = draft.getMaterialName();
		HashMap<String, Object> typeCodes = getCodes();
		typeCodes.put("%item_type%", item);
		itemTypeMeta.setDisplayName(getChat().getMessage(typeCodes, "inventory.viewdraft.item_type"));
		List<String> itemTypeLore = new ArrayList<String>();
		boolean first = true;
		for(String meta : draft.getMeta().split(" ")) {
			HashMap<String, Object> metaCodes = getCodes();
			metaCodes.put("%meta%", meta);
			if(first) itemTypeLore.add(getChat().getMessage(metaCodes, "inventory.viewdraft.meta_first"));
			else
				itemTypeLore.add(getChat().getMessage(metaCodes, "inventory.viewdraft.meta"));
			first = false;
		}
		itemTypeMeta.setLore(itemTypeLore);	
		itemType.setItemMeta(itemTypeMeta);
		inv.setItem(13, itemType);
		
		ItemStack storageType = new ItemStack(Material.IRON_BLOCK);
		ItemMeta storageTypeMeta = storageType.getItemMeta();
		
		storageTypeMeta.setDisplayName(getChat().getMessage(getCodes("%storage_type%", draft.getStorageTypeString()), "inventory.viewdraft.storage_type"));
		List<String> storageTypeLore = new ArrayList<String>();
		if(draft.getStorageType() != null) {
			storageTypeLore.add(getChat().getMessage(getCodes("%exists%", true), "inventory.viewdraft.exists"));
			storageTypeLore.add(getChat().getMessage(getCodes("%max_storage%", draft.getStorageType().getMaxAmountBase()), "inventory.viewdraft.max_storage"));
			storageTypeLore.add(getChat().getMessage(getCodes("%price%", PriceUtils.getStringCost(draft.getStorageType())), "inventory.viewdraft.price"));
			storageTypeLore.add(getChat().getMessage(getCodes("%max_number%", draft.getMaxStorages()), "inventory.viewdraft.max_number"));
			storageTypeLore.add(getChat().getMessage(getCodes("%max_import%", draft.getStorageType().getMaxImport()), "inventory.viewdraft.max_import"));
			storageTypeLore.add(getChat().getMessage(getCodes("%max_export%", draft.getStorageType().getMaxExport()), "inventory.viewdraft.max_export"));
		} else
			storageTypeLore.add(getChat().getMessage(getCodes("%exists%", false), "inventory.viewdraft.exists"));
		storageTypeMeta.setLore(storageTypeLore);
		storageType.setItemMeta(storageTypeMeta);
		inv.setItem(14, storageType);
		
		ItemStack buy = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta buyMeta = buy.getItemMeta();
		buyMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewdraft.buy_invalid"));
		if(draft.canBuy()) {
			buyMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewdraft.buy"));
			buy.setType(Material.EMERALD_BLOCK);
			buyMeta.setLore(Arrays.asList(getChat().getMessage(getCodes("%price%", PriceUtils.getStringCost(draft.getStorageType())), "inventory.viewdraft.buy_price")));
		} else {
			buyMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.viewdraft.buy_invalid"));
			List<String> lore = new ArrayList<String>();
			for(String reason : draft.getReasons())
				lore.add(getChat().getMessage(getCodes("%issue%", reason), "inventory.viewdraft.issues"));
			buyMeta.setLore(draft.getReasons());
		}
		buy.setItemMeta(buyMeta);
		inv.setItem(26, buy);
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(18, back);
		
		ItemStack delete = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta deleteMeta = delete.getItemMeta();
		deleteMeta.setDisplayName(getChat().getMessage(getCodes(), "inventory.create_remove.delete_draft"));
		delete.setItemMeta(deleteMeta);
		inv.setItem(17, delete);
	}
	
	public void viewDraftList() {
		close(false);
		ColdStorage.getPlugin().addInventory(new DraftList(getPlayer(), getEditing()));
	}
	
	public void openAnvil() {
		setEdit(true);
		setInventoryNull();
		AnvilGUINMS.createAnvil(getPlayer(), this, false);
	}
	
	public void editStorageType() {
		close(false);
		ColdStorage.getPlugin().addInventory(new StorageTypeList(getPlayer(), getEditing(), draft));
	}
	
	public void modifyItem(ItemStack item) {
		draft.setMaterial(item.getType());
		draft.setMeta(ColdStorage.getPlugin().getItemSerial().itemToData(item));
		DatabaseUtils.updateCache(getPlayer(), draft);
		setModifyItem(false);
		setInventory();
	}

	public Draft getDraft() {
		return draft;
	}

	public void setDraft(Draft draft) {
		this.draft = draft;
	}

	@Override
	public void setItemName(String name) {
		setEdit(false);
		draft.setName(name);
		DatabaseUtils.updateCache(getPlayer(), draft);
		setInventory();
	}

	public boolean isModifyItem() {
		return modifyItem;
	}

	public void setModifyItem(boolean modifyItem) {
		this.modifyItem = modifyItem;
	}

	public void attemptBuy() {
		if(PriceUtils.takeMoney(getPlayer(), draft, draft.getStorageType())) {
			close(false);
			ColdStorage.getPlugin().addInventory(new ListStorage(getPlayer(), getEditing()));
		} else {
			getChat().sendMessage(getPlayer(), getChat().getMessage(getCodes("%money%", PriceUtils.getStringCost(draft.getStorageType())), "exceptions.missing_money"));
			setInventory();
		}
	}
	
	public void confirmDelete() {
		setEdit(true);
		choice = true;
		setInventoryNull();
		AnvilGUINMS.createAnvil(getPlayer(), this, true);
	}

	@Override
	public void setChoice(String choice) {
		this.choice = false;
		if(choice.equals("confirm")) {
			DatabaseUtils.deleteCache(draft);
			viewDraftList();
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

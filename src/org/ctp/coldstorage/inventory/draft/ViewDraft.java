package org.ctp.coldstorage.inventory.draft;

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
import org.ctp.coldstorage.inventory.storage.ListStorage;
import org.ctp.coldstorage.nms.AnvilGUI;
import org.ctp.coldstorage.storage.Draft;
import org.ctp.coldstorage.utils.PriceUtils;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.config.ItemSerialization;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class ViewDraft implements ColdStorageInventory, Anvilable{

	private OfflinePlayer player, admin;
	private Player show;
	private Inventory inventory;
	private boolean opening, editing, modifyItem, choice;
	private Draft draft;

	public ViewDraft(OfflinePlayer player, Draft draft) {
		this.player = player;
		if(this.player instanceof Player) {
			setShow((Player) this.player); 
		}
		this.draft = draft;
	}
	
	public ViewDraft(OfflinePlayer player, OfflinePlayer admin, Draft draft) {
		this.player = player;
		this.admin = admin;
		if(this.admin != null && this.admin instanceof Player) {
			setShow((Player) this.admin); 
		} else {
			setShow((Player) this.player); 
		}
		this.draft = draft;
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
		HashMap<String, Object> nameCodes = getCodes();
		nameCodes.put("%name%", draft.getName());
		Inventory inv = Bukkit.createInventory(null, 27, ChatUtils.getMessage(nameCodes, "inventory.viewdraft.title"));
		inv = open(inv);
		
		ItemStack name = new ItemStack(Material.NAME_TAG);
		ItemMeta nameMeta = name.getItemMeta();
		nameMeta.setDisplayName(ChatUtils.getMessage(nameCodes, "inventory.viewdraft.name"));
		nameMeta.setLore(ChatUtils.getMessages(getCodes(), "inventory.viewdraft.name_lore"));
		name.setItemMeta(nameMeta);
		inv.setItem(12, name);
		
		ItemStack itemType = ItemSerialization.dataToItem(draft.getMaterial(), 1, draft.getMeta());
		String item = itemType.getType().name();
		if(itemType.getType() == Material.AIR) itemType.setType(Material.BARRIER);
		if(modifyItem) itemType.setType(Material.EMERALD);
		ItemMeta itemTypeMeta = itemType.getItemMeta();
		HashMap<String, Object> typeCodes = getCodes();
		typeCodes.put("%item_type%", item);
		itemTypeMeta.setDisplayName(ChatUtils.getMessage(typeCodes, "inventory.viewdraft.item_type"));
		List<String> itemTypeLore = new ArrayList<String>();
		boolean first = true;
		for(String meta : draft.getMeta().split(" ")) {
			HashMap<String, Object> metaCodes = getCodes();
			metaCodes.put("%meta%", meta);
			if(first) {
				itemTypeLore.add(ChatUtils.getMessage(metaCodes, "inventory.viewdraft.meta_first"));
			} else {
				itemTypeLore.add(ChatUtils.getMessage(metaCodes, "inventory.viewdraft.meta"));
			}
			first = false;
		}
		itemTypeMeta.setLore(itemTypeLore);	
		itemType.setItemMeta(itemTypeMeta);
		inv.setItem(13, itemType);
		
		ItemStack storageType = new ItemStack(Material.IRON_BLOCK);
		ItemMeta storageTypeMeta = storageType.getItemMeta();
		
		storageTypeMeta.setDisplayName(ChatUtils.getMessage(getCodes("%storage_type%", draft.getStorageTypeString()), "inventory.viewdraft.storage_type"));
		List<String> storageTypeLore = new ArrayList<String>();
		if(draft.getStorageType() != null) {
			storageTypeLore.add(ChatUtils.getMessage(getCodes("%exists%", true), "inventory.viewdraft.exists"));
			storageTypeLore.add(ChatUtils.getMessage(getCodes("%max_storage%", draft.getStorageType().getMaxAmountBase()), "inventory.viewdraft.max_storage"));
			storageTypeLore.add(ChatUtils.getMessage(getCodes("%price%", PriceUtils.getStringCost(draft.getStorageType())), "inventory.viewdraft.price"));
			storageTypeLore.add(ChatUtils.getMessage(getCodes("%max_number%", draft.getMaxStorages()), "inventory.viewdraft.max_number"));
			storageTypeLore.add(ChatUtils.getMessage(getCodes("%max_import%", draft.getStorageType().getMaxImport()), "inventory.viewdraft.max_import"));
			storageTypeLore.add(ChatUtils.getMessage(getCodes("%max_export%", draft.getStorageType().getMaxExport()), "inventory.viewdraft.max_export"));
		} else {
			storageTypeLore.add(ChatUtils.getMessage(getCodes("%exists%", false), "inventory.viewdraft.exists"));
		}
		storageTypeMeta.setLore(storageTypeLore);
		storageType.setItemMeta(storageTypeMeta);
		inv.setItem(14, storageType);
		
		ItemStack buy = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta buyMeta = buy.getItemMeta();
		buyMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewdraft.buy_invalid"));
		if(draft.canBuy()) {
			buyMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewdraft.buy"));
			buy.setType(Material.EMERALD_BLOCK);
			buyMeta.setLore(Arrays.asList(ChatUtils.getMessage(getCodes("%price%", PriceUtils.getStringCost(draft.getStorageType())), "inventory.viewdraft.buy_price")));
		} else {
			buyMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.viewdraft.buy_invalid"));
			List<String> lore = new ArrayList<String>();
			for(String reason : draft.getReasons()) {
				lore.add(ChatUtils.getMessage(getCodes("%issue%", reason), "inventory.viewdraft.issues"));
				
			}
			buyMeta.setLore(draft.getReasons());
		}
		buy.setItemMeta(buyMeta);
		inv.setItem(26, buy);
		
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.pagination.go_back"));
		back.setItemMeta(backMeta);
		inv.setItem(18, back);
		
		ItemStack delete = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta deleteMeta = delete.getItemMeta();
		deleteMeta.setDisplayName(ChatUtils.getMessage(getCodes(), "inventory.create_remove.delete_draft"));
		delete.setItemMeta(deleteMeta);
		inv.setItem(17, delete);
	}
	
	public void viewDraftList() {
		close(false);
		InventoryUtils.addInventory(show, new DraftList(player, admin));
	}
	
	public void openAnvil() {
		editing = true;
		inventory = null;
		AnvilGUI.createAnvil(show, this, false);
	}
	
	public void editStorageType() {
		close(false);
		InventoryUtils.addInventory(show, new StorageTypeList(player, admin, draft));
	}
	
	public void modifyItem(ItemStack item) {
		draft.setMaterial(item.getType());
		draft.setMeta(ItemSerialization.itemToData(item));
		DatabaseUtils.updateCache(show, draft);
		setModifyItem(false);
		setInventory();
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
	public boolean isOpening() {
		return opening;
	}

	public Draft getDraft() {
		return draft;
	}

	public void setDraft(Draft draft) {
		this.draft = draft;
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
		draft.setName(name);
		DatabaseUtils.updateCache(show, draft);
		setInventory();
	}

	public boolean isModifyItem() {
		return modifyItem;
	}

	public void setModifyItem(boolean modifyItem) {
		this.modifyItem = modifyItem;
	}

	public void attemptBuy() {
		if(PriceUtils.takeMoney(show, draft, draft.getStorageType())) {
			close(false);
			InventoryUtils.addInventory(show, new ListStorage(player, admin));
		} else {
			ChatUtils.sendMessage(show, ChatUtils.getMessage(getCodes("%money%", PriceUtils.getStringCost(draft.getStorageType())), "exceptions.missing_money"));
			setInventory();
		}
	}
	
	public void confirmDelete() {
		editing = true;
		choice = true;
		inventory = null;
		AnvilGUI.createAnvil(show, this, true);
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

package org.ctp.coldstorage.inventory;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.utils.ItemUtils;

public abstract class ColdStorageData implements InventoryData, Chatable {
	private final Player player;
	private final OfflinePlayer editing;
	private Inventory inventory;
	private boolean opening = false, edit = false;

	public ColdStorageData(Player player) {
		this.player = player;
		this.editing = player;
	}

	public ColdStorageData(Player player, OfflinePlayer editing) {
		this.player = player;
		this.editing = editing;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void setInventory(List<ItemStack> arg0) {
		setInventory();
	}

	public void setInventoryNull() {
		inventory = null;
	}

	@Override
	public Inventory open(Inventory inv) {
		opening = true;
		if (inventory == null) {
			inventory = inv;
			player.openInventory(inv);
		} else if (inv.getSize() == inventory.getSize()) {
			inv = player.getOpenInventory().getTopInventory();
			inventory = inv;
		} else {
			inventory = inv;
			player.openInventory(inv);
		}
		for(int i = 0; i < inventory.getSize(); i++)
			inventory.setItem(i, new ItemStack(Material.AIR));
		if (opening) opening = false;
		return inv;
	}

	@Override
	public void close(boolean external) {
		if (ColdStorage.getPlugin().hasInventory(this)) {
			if (getItems() != null) for(ItemStack item: getItems())
				ItemUtils.giveItemToPlayer(player, item, player.getLocation(), false);
			ColdStorage.getPlugin().removeInventory(this);
			if (!external) player.getOpenInventory().close();
		}
	}

	@Override
	public Block getBlock() {
		return null;
	}

	@Override
	public List<ItemStack> getItems() {
		return null;
	}

	@Override
	public void setItemName(String arg0) {}

	public OfflinePlayer getEditing() {
		return editing;
	}

	public boolean willEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public HashMap<String, Object> getCodes() {
		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%player%", player.getName());
		codes.put("%editing%", editing.getName());
		return codes;
	}

	public HashMap<String, Object> getCodes(String string, Object object) {
		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%player%", player.getName());
		codes.put("%editing%", editing.getName());
		codes.put(string, object);
		return codes;
	}

	public HashMap<String, Object> getCodes(HashMap<String, Object> objects) {
		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%player%", player.getName());
		codes.put("%editing%", editing.getName());
		codes.putAll(objects);
		return codes;
	}

	public boolean isOpening() {
		return opening;
	}

}

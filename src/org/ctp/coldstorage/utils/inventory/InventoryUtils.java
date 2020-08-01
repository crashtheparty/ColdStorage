package org.ctp.coldstorage.utils.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.Serialable;
import org.ctp.coldstorage.storage.Storage;
import org.ctp.crashapi.utils.ChatUtils;

public class InventoryUtils {
	public static int maxAddToInventory(Player player, ItemStack item) {
		int add = 0;

		Material type = item.getType();

		Inventory inv = player.getInventory();
		for(int i = 0; i < 36; i++) {
			ItemStack invItem = inv.getItem(i);
			if (invItem == null) add += type.getMaxStackSize();
			else if (invItem.getType().equals(type)) {
				if (Serialable.get().itemToData(item).equals(Serialable.get().itemToData(invItem))) add += (type.getMaxStackSize() - invItem.getAmount());
			} else if (invItem.getType().equals(Material.AIR)) add += type.getMaxStackSize();
		}
		return add;
	}

	public static int maxRemoveFromInventory(Storage storage, Player player, ItemStack item) {
		if (storage.getStorageType() == null) {
			Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "exceptions.missing_storage_type"));
			return 0;
		}
		if (storage.getStoredAmount() >= storage.getStorageType().getMaxAmountBase()) return 0;
		int remove = 0;

		Material type = item.getType();

		Inventory inv = player.getInventory();
		for(int i = 0; i < 36; i++) {
			ItemStack invItem = inv.getItem(i);
			if (invItem == null) {

			} else if (invItem.getType().equals(type)) if (Serialable.get().itemToData(item).equals(Serialable.get().itemToData(invItem))) remove += invItem.getAmount();
		}
		if (remove + storage.getStoredAmount() > storage.getStorageType().getMaxAmountBase()) remove = storage.getStorageType().getMaxAmountBase() - storage.getStoredAmount();
		return remove;
	}

	public static int addItems(Player player, ItemStack itemAdd) {
		Inventory inv = player.getInventory();
		int amount = itemAdd.getAmount();
		Material material = itemAdd.getType();
		String metadata = Serialable.get().itemToData(itemAdd);

		for(int i = 0; i < 36; i++) {
			int addItem = material.getMaxStackSize();
			ItemStack item = inv.getItem(i);
			if (item == null) {
				if (addItem > amount) addItem = amount;
				player.getInventory().setItem(i, Serialable.get().dataToItem(material, addItem, metadata));
				amount -= addItem;
			} else if (item.getType().equals(material)) {
				if (Serialable.get().itemToData(itemAdd).equals(Serialable.get().itemToData(item))) {
					int setItem = item.getAmount();
					if (addItem > amount + setItem) addItem = amount + setItem;
					player.getInventory().setItem(i, Serialable.get().dataToItem(material, addItem, metadata));
					amount = amount - addItem + setItem;
				}
			} else if (item.getType().equals(Material.AIR)) {
				if (addItem > amount) addItem = amount;
				player.getInventory().setItem(i, Serialable.get().dataToItem(material, material.getMaxStackSize(), metadata));
				amount -= addItem;
			}
			if (amount <= 0) break;
		}
		return amount;
	}
}

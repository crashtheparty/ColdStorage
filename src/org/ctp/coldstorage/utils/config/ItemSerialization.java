package org.ctp.coldstorage.utils.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

public class ItemSerialization {
	@SuppressWarnings("deprecation")
	public static String itemToString(ItemStack item) {
		String itemString = "";
		if (item.getType() != null) {
			itemString = itemString + "id@" + item.getTypeId();
		}
		itemString = itemString + " amount@" + item.getAmount();

		if (item.getItemMeta().getDisplayName() != null) {
			itemString = itemString
					+ " name@"
					+ item.getItemMeta().getDisplayName().replace(" ", "_")
							.replace("§", "&");
		}
		if (item.getDurability() != 0) {
			itemString = itemString + " damage@" + item.getDurability();
		}
		Map<Enchantment, Integer> isEnch = item.getEnchantments();
		if (isEnch.size() > 0) {
			for (Map.Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
				itemString = itemString + " enchant@"
						+ ench.getKey().getName() + "@"
						+ ench.getValue();
			}
		}

		List<String> isLore = item.getItemMeta().getLore();
		if ((item.getItemMeta().getLore() != null) && (isLore.size() != 0)) {
			for (String lore : isLore) {
				itemString = itemString + " lore@"
						+ lore.replace(" ", "_").replace("§", "&");
			}
		}

		if ((item.getType().equals(Material.SKULL_ITEM))
				&& (item.getData().equals(new MaterialData(item.getType(),
						(byte) 3)))) {
			itemString = itemString + " owner@"
					+ ((SkullMeta) item.getItemMeta()).getOwner();
		}

		return itemString;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack stringToItem(String itemString) {
		ItemStack is = null;
		Boolean createdItemStack = Boolean.valueOf(false);

		String[] serializedItem = itemString.split(" ");
		for (String itemInfo : serializedItem) {
			String[] itemAttribute = itemInfo.split("@");
			if (itemAttribute[0].equals("id")) {
				is = new ItemStack(Material.getMaterial(Integer.valueOf(
						itemAttribute[1]).intValue()));
				createdItemStack = Boolean.valueOf(true);
			} else if ((itemAttribute[0].equals("damage"))
					&& (createdItemStack.booleanValue())) {
				is.setDurability(Short.valueOf(itemAttribute[1]).shortValue());
			} else if ((itemAttribute[0].equals("amount"))
					&& (createdItemStack.booleanValue())) {
				is.setAmount(Integer.valueOf(itemAttribute[1]).intValue());
			} else if ((itemAttribute[0].equals("name"))
					&& (createdItemStack.booleanValue())) {
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
						itemAttribute[1].replace("_", " ")));
				is.setItemMeta(im);
			} else if ((itemAttribute[0].equals("enchant"))
					&& (createdItemStack.booleanValue())) {
				if (Enchantment.getByName(itemAttribute[1]) != null) {
					is.addUnsafeEnchantment(Enchantment
							.getByName(itemAttribute[1]),
							Integer.valueOf(itemAttribute[2]).intValue());
				} else {
					org.bukkit.Bukkit.getLogger().warning(
							"[LobbyTools] Wrong enchantment name: "
									+ itemAttribute[1]);
					org.bukkit.Bukkit.getLogger().warning(
							"[LobbyTools] Please fix the name in config!");
				}
			} else if ((itemAttribute[0].equals("lore"))
					&& (createdItemStack.booleanValue())) {
				ItemMeta im = is.getItemMeta();
				List<String> il = new ArrayList<String>();

				if (is.getItemMeta().getLore() != null) {
					for (String lore : is.getItemMeta().getLore())
						if (lore != null)
							il.add(ChatColor.translateAlternateColorCodes('&',
									lore.replace("_", " ")));
				}
				il.add(ChatColor.translateAlternateColorCodes('&',
						itemAttribute[1].replace("_", " ")));
				im.setLore(il);
				is.setItemMeta(im);
			} else if ((itemAttribute[0].equals("owner"))
					&& (createdItemStack.booleanValue())) {
				SkullMeta im = (SkullMeta) is.getItemMeta();
				im.setOwner(itemAttribute[1]);
				is.setItemMeta(im);
			}
		}
		return is;
	}
}
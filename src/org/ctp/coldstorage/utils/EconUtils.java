package org.ctp.coldstorage.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.utils.config.ConfigUtilities;

import net.milkbowl.vault.economy.Economy;

public class EconUtils {

	public static boolean takeMoney(Player player) {
		if(ConfigUtilities.VAULT){
			Economy econ = ColdStorage.getEconomy();
			double price = ConfigUtilities.PRICE;
			if(econ.getBalance(player) >= price){
				econ.withdrawPlayer(player, price);
				return true;
			}
		}else{
			int reward = 0;
			ItemStack item = ConfigUtilities.PRICE_ITEM;
			for (int j = 1; j <= 64; j++) {
				ItemStack rewardItem = new ItemStack(item.getType(), j);
				rewardItem.setItemMeta(item.getItemMeta());
				rewardItem.setDurability(item.getDurability());
				if (player.getInventory().contains(rewardItem)) {
					reward += j;
				}
			}
			if(reward >= item.getAmount()){
				player.getInventory().removeItem(item);
				return true;
			}
		}
		
		return false;
	}
	
	public static String stringifyPrice() {
		String price = "";
		if(ConfigUtilities.VAULT) {
			price += ColdStorage.getEconomy().format(ConfigUtilities.PRICE);
		} else {
			price += ConfigUtilities.PRICE_ITEM.toString();
		}
		return price;
	}
	
}

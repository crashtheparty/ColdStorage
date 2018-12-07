package org.ctp.coldstorage.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.utils.config.ConfigUtilities;

import net.milkbowl.vault.economy.Economy;

public class EconUtils {

	public static boolean takeMoney(Player player, boolean admin) {
		if(admin || player.hasPermission("coldstorage.free")) {
			return true;
		}
		
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
				if(rewardItem.getItemMeta() instanceof Damageable && item.getItemMeta() instanceof Damageable) {
					((Damageable) rewardItem.getItemMeta()).setDamage(((Damageable) item.getItemMeta()).getDamage());
				}
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
	
	public static String stringifyRefund(Player player) {
		String price = "";
		if(player.hasPermission("coldstorage.free")) {
			return "No Refund";
		}
		if(ConfigUtilities.VAULT) {
			price += ColdStorage.getEconomy().format(ConfigUtilities.PRICE_REFUND);
		} else {
			price += ConfigUtilities.PRICE_ITEM_REFUND.toString();
		}
		return price;
	}
	
	public static String stringifyPrice(Player player) {
		String price = "";
		if(player.hasPermission("coldstorage.free")) {
			return "Free";
		}
		if(ConfigUtilities.VAULT) {
			price += ColdStorage.getEconomy().format(ConfigUtilities.PRICE);
		} else {
			price += ConfigUtilities.PRICE_ITEM.toString();
		}
		return price;
	}
	
}

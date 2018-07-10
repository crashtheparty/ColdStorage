package org.ctp.coldstorage.utils.config;

import org.bukkit.inventory.ItemStack;

public class ConfigUtilities {
	
	public static int MAX_STORAGE_SIZE = 2000000;
	public static int PRICE = 1000;
	public static ItemStack PRICE_ITEM;
	public static boolean VAULT;
	
	public static void getFromConfigs(SimpleConfig config) {
		if(config.contains("max_storage_size")) {
			MAX_STORAGE_SIZE = config.getInt("max_storage_size");
			if(MAX_STORAGE_SIZE > 2000000) {
				config.set("max_storage_size", 2000000);
				config.saveConfig();
			}
		}
		
		if(config.contains("price")) {
			PRICE = config.getInt("price");
		}
		
		if(config.contains("price_item")) {
			PRICE_ITEM = ItemSerialization.stringToItem(config.getString("price_item"));
		}
		
		if(config.contains("vault")) {
			VAULT = config.getBoolean("vault");
		}
	}

}

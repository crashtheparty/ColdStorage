package org.ctp.coldstorage.utils.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ConfigUtilities {
	
	public static int MAX_STORAGE_SIZE = 2000000;
	public static int PRICE = 1000;
	public static ItemStack PRICE_ITEM;
	public static boolean VAULT;
	public static String OPEN_MESSAGE = "Opening cold storage...";
	public static List<String> ALIASES = new ArrayList<String>();
	
	public static void getFromConfigs(SimpleConfig config) {
		if(config.contains("max_storage_size")) {
			MAX_STORAGE_SIZE = config.getInt("max_storage_size");
			if(MAX_STORAGE_SIZE > 2000000) {
				config.set("max_storage_size", 2000000);
				MAX_STORAGE_SIZE = 2000000;
				config.saveConfig();
			}
		}
		
		if(config.contains("open_message")) {
			OPEN_MESSAGE = config.getString("open_message");
		}
		
		if(config.contains("aliases")) {
			ALIASES = config.getStringList("aliases");
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

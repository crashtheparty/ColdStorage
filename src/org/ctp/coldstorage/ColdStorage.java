package org.ctp.coldstorage;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.ctp.coldstorage.database.SQLite;
import org.ctp.coldstorage.listeners.InventoryClick;
import org.ctp.coldstorage.utils.config.ConfigUtilities;
import org.ctp.coldstorage.utils.config.ItemSerialization;
import org.ctp.coldstorage.utils.config.SimpleConfig;
import org.ctp.coldstorage.utils.config.SimpleConfigManager;

import net.milkbowl.vault.economy.Economy;

public class ColdStorage extends JavaPlugin {

	public static ColdStorage plugin;
	private static SQLite db;
	private static SimpleConfig CONFIG;
	public SimpleConfigManager manager;
	private static Economy ECON = null;

	public static SimpleConfig getDefaultConfig() {
		return CONFIG;
	}

	public void onEnable() {
		plugin = this;

		try {
			File dataFolder = plugin.getDataFolder();
			if (!dataFolder.exists()) {
				dataFolder.mkdirs();
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

		getCommand("coldstorage").setExecutor(new org.ctp.coldstorage.commands.ColdStorage());

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new InventoryClick(), this);
		
		initiateConfigs();

		db = new SQLite(plugin);
		db.load();
	}

	public static SQLite getDb() {
		return db;
	}

	private void initiateConfigs() {
		manager = new SimpleConfigManager(this);
		String[] header = { "ColdStorage", "Developed and written by", "crashtheparty" };
		CONFIG = manager.getNewConfig("config.yml", header);
		CONFIG.addDefault("max_storage_size", Integer.valueOf(2000000));
		CONFIG.addDefault("price", 1000);
		CONFIG.addDefault("price_item", ItemSerialization.itemToString(new ItemStack(Material.DIAMOND, 4)));
		if (hasVault()) {
			CONFIG.addDefault("vault", true);
		} else {
			CONFIG.addDefault("vault", false);
		}
		CONFIG.saveConfig();
		getLogger().info("Config initialized");
		ConfigUtilities.getFromConfigs(CONFIG);
	}
	
	public boolean hasVault() {
		if(!Bukkit.getPluginManager().isPluginEnabled("Vault")){
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		ECON = rsp.getProvider();
		return ECON != null;
	}
	
	public static Economy getEconomy() {
		return ECON;
	}

}

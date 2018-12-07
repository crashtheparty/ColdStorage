package org.ctp.coldstorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.ctp.coldstorage.commands.ColdStorageAdmin;
import org.ctp.coldstorage.commands.ColdStorageOpen;
import org.ctp.coldstorage.commands.ColdStorageReload;
import org.ctp.coldstorage.database.SQLite;
import org.ctp.coldstorage.listeners.ChatMessage;
import org.ctp.coldstorage.listeners.InventoryClick;
import org.ctp.coldstorage.listeners.InventoryClose;
import org.ctp.coldstorage.utils.ChatUtilities;
import org.ctp.coldstorage.utils.alias.Alias;
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
	private List<Alias> commands = new ArrayList<Alias>();

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
		
		initiateConfigs();
		getLogger().info("Config initialized.");
		getCommand("csadmin").setExecutor(new ColdStorageAdmin());
		getCommand("csopen").setExecutor(new ColdStorageOpen());
		getCommand("csreload").setExecutor(new ColdStorageReload());
		
		setAliases();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new InventoryClick(), this);
		pm.registerEvents(new InventoryClose(), this);
		pm.registerEvents(new ChatMessage(), this);

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
		CONFIG.addDefault("price_refund", 250);
		CONFIG.addDefault("price_item_refund", ItemSerialization.itemToString(new ItemStack(Material.DIAMOND, 1)));
		CONFIG.addDefault("aliases", Arrays.asList());
		CONFIG.addDefault("open_message", "Opening cold storage...");
		if (hasVault()) {
			CONFIG.addDefault("vault", true);
		} else {
			CONFIG.addDefault("vault", false);
		}
		CONFIG.saveConfig();
		ConfigUtilities.getFromConfigs(CONFIG);
	}
	
	private void removeAliases() throws IllegalStateException {
		for(Alias command : commands) {
			command.unregister();
		}
		commands = new ArrayList<Alias>();
	}
	
	private void setAliases() {
		for(String alias : ConfigUtilities.ALIASES) {
			try {
				commands.add(new Alias(alias, getCommand("csopen")));
			} catch (Exception e) {
				ChatUtilities.sendToConsole(Level.WARNING, "Command alias " + alias + " couldn't be added.");
			}
		}
	}
	
	public void reloadConfigs(CommandSender sender) {
		List<String> oldAliases = ConfigUtilities.ALIASES;
		initiateConfigs();
		List<String> newAliases = ConfigUtilities.ALIASES;
		boolean newAlias = false;
		for(int i = 0; i < oldAliases.size(); i++) {
			if(!oldAliases.get(i).equals(newAliases.get(i))) {
				newAlias = true;
			}
		}
		if(oldAliases.size() != newAliases.size()) {
			newAlias = true;
		}
		getLogger().info("Config reloaded.");
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			ChatUtilities.sendMessage(player, "Config reloaded.");
		}
		
		if(newAlias) {
			try {
				removeAliases();
				setAliases();
				if(sender instanceof Player) {
					Player player = (Player) sender;
					ChatUtilities.sendMessage(player, "Please note that command autocomplete will not work with updated aliases until a reload.");
				} else {
					ChatUtilities.sendToConsole(Level.WARNING, "Please note that command autocomplete will not work with updated aliases until a reload.");
				}
			} catch(IllegalStateException ex) {
				ex.printStackTrace();
			}
		}
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

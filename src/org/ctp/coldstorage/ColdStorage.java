package org.ctp.coldstorage;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.ctp.coldstorage.commands.AddChest;
import org.ctp.coldstorage.commands.Admin;
import org.ctp.coldstorage.commands.Open;
import org.ctp.coldstorage.commands.Reload;
import org.ctp.coldstorage.database.SQLite;
import org.ctp.coldstorage.listeners.BlockListener;
import org.ctp.coldstorage.listeners.ChatMessage;
import org.ctp.coldstorage.listeners.InventoryClick;
import org.ctp.coldstorage.listeners.InventoryClose;
import org.ctp.coldstorage.listeners.PlayerListener;
import org.ctp.coldstorage.threads.ImportExportThread;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.config.Configuration;
import org.ctp.coldstorage.version.BukkitVersion;
import org.ctp.coldstorage.version.PluginVersion;
import org.ctp.coldstorage.version.VersionCheck;

import net.milkbowl.vault.economy.Economy;

public class ColdStorage extends JavaPlugin{
	
	private static ColdStorage PLUGIN;
	private SQLite db;
	private BukkitVersion bukkitVersion;
	private PluginVersion pluginVersion;
	private VersionCheck check;
	private Configuration config;
	private boolean initializing = true;
	private static Economy ECON = null;
	private static Boolean HAS_VAULT = null;
	
	public void onEnable() {
		PLUGIN = this;
		setBukkitVersion(new BukkitVersion());
		setPluginVersion(new PluginVersion(this, getDescription().getVersion()));
		if(!bukkitVersion.isVersionAllowed()) {
			Bukkit.getLogger().log(Level.WARNING, "Bukkit Version " + bukkitVersion.getVersion() + " is not compatible with this plugin. Anvil GUI is not supported.");
		}
		
		getCommand("open").setExecutor(new Open());
		getCommand("admin").setExecutor(new Admin());
		getCommand("csreload").setExecutor(new Reload());
		getCommand("chest").setExecutor(new AddChest());
		
		config = new Configuration(this);
		config.createConfigFiles();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new InventoryClick(), this);
		pm.registerEvents(new InventoryClose(), this);
		pm.registerEvents(new ChatMessage(), this);
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new BlockListener(), this);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PLUGIN, new ImportExportThread(), 8l, 8l);
		
		db = new SQLite(PLUGIN);
		db.load();
		DatabaseUtils.loadValues();
		
		check = new VersionCheck(pluginVersion, "https://raw.githubusercontent.com/crashtheparty/ColdStorage/master/VersionHistory", 
				"https://www.spigotmc.org/resources/cold-storage.59581/", "https://github.com/crashtheparty/ColdStorage", 
				config.getMainConfig().getBoolean("get_latest_version"));
		pm.registerEvents(check, this);
		checkVersion();
		setInitializing(false);
	}
	
	public static ColdStorage getPlugin() {
		return PLUGIN;
	}

	public SQLite getDb() {
		return db;
	}

	public BukkitVersion getBukkitVersion() {
		return bukkitVersion;
	}

	public void setBukkitVersion(BukkitVersion bukkitVersion) {
		this.bukkitVersion = bukkitVersion;
	}

	public PluginVersion getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(PluginVersion pluginVersion) {
		this.pluginVersion = pluginVersion;
	}
	
	private void checkVersion(){
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, check, 20l, 20 * 60 * 60 * 4l);
    }
	
	public static boolean hasVault() {
		if(HAS_VAULT == null) {
			if(!Bukkit.getPluginManager().isPluginEnabled("Vault")){
				return false;
			}
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer()
					.getServicesManager().getRegistration(Economy.class);
			if (rsp == null) {
				return false;
			}
			ECON = rsp.getProvider();
			HAS_VAULT = ECON != null;
		}
		return HAS_VAULT;
	}
	
	public static Economy getEconomy() {
		return ECON;
	}
	
	public Configuration getConfiguration() {
		return config;
	}

	public boolean isInitializing() {
		return initializing;
	}

	public void setInitializing(boolean initializing) {
		this.initializing = initializing;
	}
}

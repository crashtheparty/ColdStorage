package org.ctp.coldstorage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.ctp.coldstorage.commands.ColdStorageCommand;
import org.ctp.coldstorage.database.CSBackup;
import org.ctp.coldstorage.database.CSDatabase;
import org.ctp.coldstorage.listeners.*;
import org.ctp.coldstorage.threads.ImportExportThread;
import org.ctp.coldstorage.utils.Configurations;
import org.ctp.coldstorage.utils.DatabaseUtils;
import org.ctp.coldstorage.utils.commands.CSCommand;
import org.ctp.crashapi.CrashAPI;
import org.ctp.crashapi.CrashAPIPlugin;
import org.ctp.crashapi.config.yaml.YamlConfig;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.item.ItemSerialization;
import org.ctp.crashapi.utils.ChatUtils;
import org.ctp.crashapi.version.*;
import org.ctp.crashapi.version.Version.VersionType;

import net.milkbowl.vault.economy.Economy;

public class ColdStorage extends CrashAPIPlugin {

	private static ColdStorage PLUGIN;
	private static Economy ECON = null;
	private static Boolean HAS_VAULT = null;
	private CSDatabase db;
	private CSBackup backup;
	private PluginVersion pluginVersion;
	private VersionCheck check;
	private Configurations config;
	private List<InventoryData> inventories = new ArrayList<InventoryData>();

	@Override
	public void onEnable() {
		PLUGIN = this;
		BukkitVersion bukkitVersion = CrashAPI.getPlugin().getBukkitVersion();
		setPluginVersion(new PluginVersion(this, new Version(getDescription().getVersion(), VersionType.UNKNOWN)));
		if (!bukkitVersion.isVersionAllowed()) Bukkit.getLogger().log(Level.WARNING, "Bukkit Version " + bukkitVersion.getVersion() + " is not compatible with this plugin. Anvil GUI is not supported.");

		backup = new CSBackup(PLUGIN);
		backup.load();

		config = Configurations.getConfigurations();
		config.onEnable();

		ColdStorageCommand c = new ColdStorageCommand();
		getCommand("ColdStorage").setExecutor(c);
		getCommand("ColdStorage").setTabCompleter(c);
		for(CSCommand s: ColdStorageCommand.getCommands()) {
			PluginCommand command = getCommand(s.getCommand());
			if (command != null) {
				command.setExecutor(c);
				command.setTabCompleter(c);
				command.setAliases(s.getAliases());
			} else
				getChat().sendWarning("Couldn't find command '" + s.getCommand() + ".'");
		}

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new InventoryClick(), this);
		pm.registerEvents(new InventoryClose(), this);
		pm.registerEvents(new ChatMessage(), this);
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new BlockListener(), this);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(PLUGIN, new ImportExportThread(), 8l, 8l);

		db = new CSDatabase(PLUGIN);
		db.load();
		DatabaseUtils.loadValues();

		check = new VersionCheck(pluginVersion, "https://raw.githubusercontent.com/crashtheparty/ColdStorage/master/VersionHistory", "https://www.spigotmc.org/resources/cold-storage.59581/", "https://github.com/crashtheparty/ColdStorage", config.getConfig().getBoolean("get_latest_version"), false);
		pm.registerEvents(check, this);
		checkVersion();
	}

	public static ColdStorage getPlugin() {
		return PLUGIN;
	}

	public CSDatabase getDb() {
		return db;
	}

	public CSBackup getBackup() {
		return backup;
	}

	@Override
	public PluginVersion getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(PluginVersion pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	private void checkVersion() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, check, 20l, 20 * 60 * 60 * 4l);
	}

	public static boolean hasVault() {
		if (HAS_VAULT == null) {
			if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) return false;
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp == null) return false;
			ECON = rsp.getProvider();
			HAS_VAULT = ECON != null;
		}
		return HAS_VAULT;
	}

	public static Economy getEconomy() {
		return ECON;
	}

	public Configurations getConfiguration() {
		return config;
	}

	@Override
	public ChatUtils getChat() {
		return ChatUtils.getUtils(PLUGIN);
	}

	@Override
	public Configurations getConfigurations() {
		return Configurations.getConfigurations();
	}

	@Override
	public ItemSerialization getItemSerial() {
		return ItemSerialization.getItemSerial(PLUGIN);
	}

	@Override
	public YamlConfig getLanguageFile() {
		return getConfigurations().getLanguageConfig().getConfig();
	}

	@Override
	public String getStarter() {
		return getLanguageFile().getString("starter");
	}

	public InventoryData getInventory(Player player) {
		for(InventoryData inv: inventories)
			if (inv.getPlayer().getUniqueId().equals(player.getUniqueId())) return inv;

		return null;
	}

	public boolean hasInventory(InventoryData inv) {
		return inventories.contains(inv);
	}

	public void addInventory(InventoryData inv) {
		inventories.add(inv);
		inv.setInventory();
	}

	public void removeInventory(InventoryData inv) {
		inventories.remove(inv);
	}
}

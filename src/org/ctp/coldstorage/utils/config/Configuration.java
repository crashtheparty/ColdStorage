package org.ctp.coldstorage.utils.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.alias.Alias;
import org.ctp.coldstorage.utils.yamlconfig.YamlConfig;

public class Configuration {

	private File mainFile, dataFolder;
	private YamlConfig mainConfig;
	private LanguageFiles languageFiles;
	private List<String> adminAliases = new ArrayList<String>();
	private List<String> openAliases = new ArrayList<String>();
	private List<String> chestAliases = new ArrayList<String>();
	private List<Alias> adminCommands = new ArrayList<Alias>();
	private List<Alias> openCommands = new ArrayList<Alias>();
	private List<Alias> chestCommands = new ArrayList<Alias>();

	public Configuration(ColdStorage plugin) {
		dataFolder = plugin.getDataFolder();
	}

	public YamlConfig getMainConfig() {
		return mainConfig;
	}

	public YamlConfig getLanguageFile() {
		return languageFiles.getLanguageConfig();
	}

	public void createConfigFiles() {
		try {
			if (!dataFolder.exists()) {
				dataFolder.mkdirs();
			}
			mainFile = new File(dataFolder + "/config.yml");
			YamlConfiguration.loadConfiguration(mainFile);
			mainFile();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		loadLangFile(dataFolder);
		getMainConfig().setComments(getMainConfig().getBoolean("use_comments"));
		getLanguageFile().setComments(getMainConfig().getBoolean("use_comments"));

		getMainConfig().saveConfig();
		getLanguageFile().saveConfig();
		
		reloadAliases(null);
	}
	
	private void removeAliases(String type) throws IllegalStateException {
		if(type.equals("admin")) {
			for(Alias command : adminCommands) {
				command.unregister();
			}
			adminCommands = new ArrayList<Alias>();
		} else if (type.equals("open")) {
			for(Alias command : openCommands) {
				command.unregister();
			}
			openCommands = new ArrayList<Alias>();
		} else if (type.equals("chest")) {
			for(Alias command : chestCommands) {
				command.unregister();
			}
			chestCommands = new ArrayList<Alias>();
		}
	}
	
	private void setAliases(String type) {
		if(type.equals("admin")) {
			if(adminAliases != null && adminAliases.size() > 0) {
				for(String alias : adminAliases) {
					try {
						adminCommands.add(new Alias(alias, ColdStorage.getPlugin().getCommand("admin")));
					} catch (Exception e) {
						ChatUtils.sendToConsole(Level.WARNING, "Command alias " + alias + " couldn't be added.");
					}
				}
			}
		} else if (type.equals("open")) {
			if(openAliases != null && openAliases.size() > 0) {
				for(String alias : openAliases) {
					try {
						openCommands.add(new Alias(alias, ColdStorage.getPlugin().getCommand("open")));
					} catch (Exception e) {
						ChatUtils.sendToConsole(Level.WARNING, "Command alias " + alias + " couldn't be added.");
					}
				}
			}
		} else if (type.equals("chest")) {
			if(chestAliases != null && chestAliases.size() > 0) {
				for(String alias : chestAliases) {
					try {
						chestCommands.add(new Alias(alias, ColdStorage.getPlugin().getCommand("chest")));
					} catch (Exception e) {
						ChatUtils.sendToConsole(Level.WARNING, "Command alias " + alias + " couldn't be added.");
					}
				}
			}
		}
	}
	
	private void reloadAliases(CommandSender sender) {
		adminAliases = mainConfig.getStringList("admin.aliases");
		openAliases = mainConfig.getStringList("open.aliases");
		chestAliases = mainConfig.getStringList("chest.aliases");
		try {
			removeAliases("admin");
			setAliases("admin");
			removeAliases("open");
			setAliases("open");
			removeAliases("chest");
			setAliases("chest");
			if(!ColdStorage.getPlugin().isInitializing()) {
				if(sender != null && sender instanceof Player) {
					Player player = (Player) sender;
					ChatUtils.sendMessage(player, "Please note that command autocomplete will not work with updated aliases until the server is reloaded.");
				} else {
					ChatUtils.sendWarning("Please note that command autocomplete will not work with updated aliases until the server is reloaded.");
				}
			}
		} catch(IllegalStateException ex) {
			ex.printStackTrace();
		}
	}

	public void save(CommandSender sender) {
		getMainConfig().setComments(getMainConfig().getBoolean("use_comments"));
		getLanguageFile().setComments(getMainConfig().getBoolean("use_comments"));

		getMainConfig().saveConfig();
		loadLangFile(dataFolder);
		
		reloadAliases(sender);
	}

	public void reload(CommandSender sender) {
		try {
			mainFile();
			ColdStorage.getPlugin().getDb().migrateData();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		save(sender);
	}

	private void loadLangFile(File dataFolder) {
		if (languageFiles == null) {
			languageFiles = new LanguageFiles(this);
		} else {
			languageFiles.updateLanguage();
		}
	}

	public Language getLanguage() {
		return Language.getLanguage(mainConfig.getString("language"));
	}

	private void mainFile() {
		if(ColdStorage.getPlugin().isInitializing()) {
			ChatUtils.sendInfo("Loading default config...");
		}

		String[] header = { "Cold Storage", "Plugin by", "crashtheparty" };
		mainConfig = new YamlConfig(mainFile, header);

		mainConfig.getFromConfig();
		
		mainConfig.addDefault("open.aliases", Arrays.asList("csopen"), new String[] {"Set command aliases for the /open command"});
		mainConfig.addDefault("admin.aliases", Arrays.asList("csadmin"), new String[] {"Set command aliases for the /admin command"});
		mainConfig.addDefault("chest.aliases", Arrays.asList("cschest"), new String[] {"Set command aliases for the /chest command"});
		mainConfig.addDefault("use_comments", true, new String[] {"See helpful comments in this file"});
		mainConfig.addDefault("get_latest_version", true, new String[] {"Gets latest version from the github"});
		mainConfig.addDefault("language", "en_us", new String[] {"Default language for the language file"});
		mainConfig.addDefault("language_file", "language.yml", new String[] {"Default language file name"});
		mainConfig.addDefault("anvil_edits", ColdStorage.getPlugin().getBukkitVersion().isVersionAllowed(), 
				new String[] {"Whether the anvil or the chat is used to edit values in the plugin.", "Defaults to false when using unsupported version."});
		mainConfig.addDefault("default_permission_type", 5, new String[] 
				{"If user has no permissions, this is the max number of each storage type they can use",
				"Set to -1 for infinite"});
		mainConfig.addDefault("default_permission_global", 10, new String[] {"If user has no permissions, this is the max number of storages they can use",
				"Set to -1 for infinite"});
		mainConfig.addDefault("migrate_material_names", false, new String[] {"Migrate material names between Minecraft versions"});
		mainConfig.addDefault("use_comments", true, new String[] {"See helpful comments in this file"});
		if (ColdStorage.hasVault()) {
			mainConfig.addDefault("vault", true, new String[] {"Vault is installed. Default value is true."});
		} else {
			mainConfig.addDefault("vault", false, new String[] {"Vault is not installed. Vault cannot be set to true."});
		}
		
		if(!ColdStorage.getPlugin().getBukkitVersion().isVersionAllowed()) {
			mainConfig.set("anvil_edits", false);
		}
		if(!ColdStorage.hasVault()) {
			mainConfig.set("vault", false);
		}
		mainConfig.saveConfig();

		if(ColdStorage.getPlugin().isInitializing()) {
			ChatUtils.sendInfo("Default config initialized!");
		}
	}
	
	public File getDataFolder() {
		return dataFolder;
	}
	
	public int getMaxStoragesType() {
		return mainConfig.getInt("default_permission_type");
	}
	
	public int getMaxStorages() {
		return mainConfig.getInt("default_permission_global");
	}
	
	public boolean getAnvilEdits() {
		return mainConfig.getBoolean("anvil_edits");
	}
	
	public StorageType getLegacyData() {
		boolean addLegacy = false;
		int maxStorageSize = 2000000;
		int price = 1000;
		ItemStack priceItem = new ItemStack(Material.DIAMOND, 4);
		if(mainConfig.contains("max_storage_size")) {
			addLegacy = true;
			maxStorageSize = mainConfig.getInt("max_storage_size");
		}
		if(mainConfig.contains("price")) {
			addLegacy = true;
			price = mainConfig.getInt("price");
		}
		if(mainConfig.contains("price_item")) {
			addLegacy = true;
			priceItem = ItemSerialization.stringToItem(mainConfig.getString("price_item"));
		}
		
		if(addLegacy) {
			mainConfig.removeKey("max_storage_size");
			mainConfig.removeKey("price");
			mainConfig.removeKey("price_item");
			mainConfig.removeKey("price_refund");
			mainConfig.removeKey("price_item_refund");
			mainConfig.saveConfig();
			return new StorageType("Legacy", 0, 0, price, priceItem, maxStorageSize);
		}
		return null;
	}
}

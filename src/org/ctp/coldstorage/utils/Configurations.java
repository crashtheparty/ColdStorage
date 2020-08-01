package org.ctp.coldstorage.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.Serialable;
import org.ctp.coldstorage.storage.StorageType;
import org.ctp.coldstorage.utils.config.CSLanguageFile;
import org.ctp.coldstorage.utils.config.MainConfiguration;
import org.ctp.crashapi.api.LanguageConfiguration;
import org.ctp.crashapi.config.CrashConfigurations;
import org.ctp.crashapi.config.Language;
import org.ctp.crashapi.config.yaml.YamlConfig;
import org.ctp.crashapi.db.BackupDB;

public class Configurations implements CrashConfigurations {

	private final static Configurations CONFIGURATIONS = new Configurations();
	private MainConfiguration CONFIG;
	private LanguageConfiguration LANGUAGE;

	private List<CSLanguageFile> LANGUAGE_FILES = new ArrayList<CSLanguageFile>();

	private Configurations() {

	}

	public static Configurations getConfigurations() {
		return CONFIGURATIONS;
	}

	@Override
	public void onEnable() {
		File dataFolder = ColdStorage.getPlugin().getDataFolder();

		try {
			if (!dataFolder.exists()) dataFolder.mkdirs();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		BackupDB db = ColdStorage.getPlugin().getBackup();

		CONFIG = new MainConfiguration(ColdStorage.getPlugin(), dataFolder, db);

		String languageFile = CONFIG.getString("language_file");
		Language lang = Language.getLanguage(CONFIG.getString("language"));
		if (!lang.getLocale().equals(CONFIG.getString("language"))) CONFIG.updatePath("language", lang.getLocale());

		File languages = new File(dataFolder + "/language");

		if (!languages.exists()) languages.mkdirs();

		LANGUAGE_FILES.add(new CSLanguageFile(dataFolder, Language.US));

		for(CSLanguageFile file: LANGUAGE_FILES)
			if (file.getLanguage() == lang) LANGUAGE = new LanguageConfiguration(dataFolder, languageFile, file, db);

		if (LANGUAGE == null) LANGUAGE = new LanguageConfiguration(dataFolder, languageFile, LANGUAGE_FILES.get(0), db);

		save();
	}

	@Override
	public void save() {
		save(null);
	}

	public void save(CommandSender sender) {
		CONFIG.setComments(CONFIG.getBoolean("use_comments"));
		LANGUAGE.setComments(CONFIG.getBoolean("use_comments"));
		CONFIG.save();
		LANGUAGE.save();
	}

	public void reload(CommandSender sender) {
		CONFIG.reload();
		LANGUAGE.reload();
		try {
			ColdStorage.getPlugin().getDb().migrateData();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		save(sender);
	}

	public MainConfiguration getConfig() {
		return CONFIG;
	}

	public LanguageConfiguration getLanguageConfig() {
		return LANGUAGE;
	}

	public StorageType getLegacyData() {
		boolean addLegacy = false;
		int maxStorageSize = 2000000;
		int price = 1000;
		ItemStack priceItem = new ItemStack(Material.DIAMOND, 4);
		YamlConfig mainConfig = CONFIG.getConfig();
		if (mainConfig.contains("max_storage_size")) {
			addLegacy = true;
			maxStorageSize = mainConfig.getInt("max_storage_size");
		}
		if (mainConfig.contains("price")) {
			addLegacy = true;
			price = mainConfig.getInt("price");
		}
		if (mainConfig.contains("price_item")) {
			addLegacy = true;
			priceItem = Serialable.get().stringToItem(mainConfig.getString("price_item"));
		}

		if (addLegacy) {
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

	public int getMaxStoragesType() {
		return CONFIG.getConfig().getInt("default_permission_type");
	}

	public int getMaxStorages() {
		return CONFIG.getConfig().getInt("default_permission_global");
	}

	public boolean getAnvilEdits() {
		return CONFIG.getConfig().getBoolean("anvil_edits");
	}

}

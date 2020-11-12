package org.ctp.coldstorage.utils.config;

import java.io.File;

import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.utils.Configurations;
import org.ctp.crashapi.CrashAPI;
import org.ctp.crashapi.CrashAPIPlugin;
import org.ctp.crashapi.config.Configuration;
import org.ctp.crashapi.config.yaml.YamlConfigBackup;
import org.ctp.crashapi.db.BackupDB;

public class MainConfiguration extends Configuration {

	public MainConfiguration(CrashAPIPlugin plugin, File dataFolder, BackupDB backup) {
		super(plugin, new File(dataFolder + "/config.yml"), backup, new String[] { "Cold Storage", "Plugin by", "crashtheparty" });
	}

	@Override
	public void migrateVersion() {
		YamlConfigBackup config = getConfig();
		if (!CrashAPI.getPlugin().getBukkitVersion().isVersionAllowed()) config.set("anvil_edits", false);
		if (!ColdStorage.hasVault()) config.set("vault", false);
		config.saveConfig();
	}

	@Override
	public void setDefaults() {
		if (Configurations.isInitializing()) getChat().sendInfo("Initializing default config...");

		YamlConfigBackup config = getConfig();

		config.getFromConfig();

		config.addDefault("use_comments", true, new String[] { "See helpful comments in this file" });
		config.addDefault("get_latest_version", true, new String[] { "Gets latest version from the github" });
		config.addDefault("language", "en_us", new String[] { "Default language for the language file" });
		config.addDefault("language_file", "language.yml", new String[] { "Default language file name" });
		config.addDefault("anvil_edits", CrashAPI.getPlugin().getBukkitVersion().isVersionAllowed(), new String[] { "Whether the anvil or the chat is used to edit values in the plugin.", "Defaults to false when using unsupported version." });
		config.addDefault("default_permission_type", 5, new String[] { "If user has no permissions, this is the max number of each storage type they can use", "Set to -1 for infinite" });
		config.addDefault("default_permission_global", 10, new String[] { "If user has no permissions, this is the max number of storages they can use", "Set to -1 for infinite" });
		config.addDefault("migrate_material_names", false, new String[] { "Migrate material names between Minecraft versions" });
		config.addDefault("use_comments", true, new String[] { "See helpful comments in this file" });
		if (ColdStorage.hasVault()) config.addDefault("vault", true, new String[] { "Vault is installed. Default value is true." });
		else
			config.addDefault("vault", false, new String[] { "Vault is not installed. Vault cannot be set to true." });

		config.writeDefaults();

		if (Configurations.isInitializing()) getChat().sendInfo("Default config initialized!");
	}

}

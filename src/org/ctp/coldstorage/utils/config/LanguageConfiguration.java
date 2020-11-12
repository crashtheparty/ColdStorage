package org.ctp.coldstorage.utils.config;

import java.io.File;

import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.utils.Configurations;
import org.ctp.crashapi.config.Configuration;
import org.ctp.crashapi.config.CrashLanguageFile;
import org.ctp.crashapi.config.Language;
import org.ctp.crashapi.config.yaml.YamlConfigBackup;
import org.ctp.crashapi.db.BackupDB;

public class LanguageConfiguration extends Configuration {

	private CrashLanguageFile language;

	public LanguageConfiguration(File file, String languageFile, CrashLanguageFile language, BackupDB db) {
		super(ColdStorage.getPlugin(), new File(file + "/" + languageFile), db, new String[] {});

		this.language = language;

		migrateVersion();
		save();
	}

	@Override
	public void setDefaults() {
		if (Configurations.isInitializing()) getPlugin().sendInfo("Initializing language configuration...");
		YamlConfigBackup config = getConfig();
		config.addDefault("starter", "&8[&dCold Storage&8]");
		config.copyDefaults(language.getConfig());

		config.writeDefaults();
		if (Configurations.isInitializing()) getPlugin().sendInfo("Language configuration initialized!");
	}

	@Override
	public void migrateVersion() {}

	@Override
	public void repairConfig() {}

	public Language getLanguage() {
		return language.getLanguage();
	}

}

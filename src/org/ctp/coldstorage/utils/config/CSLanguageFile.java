package org.ctp.coldstorage.utils.config;

import java.io.File;

import org.ctp.crashapi.config.CrashLanguageFile;
import org.ctp.crashapi.config.Language;
import org.ctp.crashapi.config.yaml.YamlConfig;
import org.ctp.crashapi.utils.CrashConfigUtils;

public class CSLanguageFile extends CrashLanguageFile {

	public CSLanguageFile(File dataFolder, Language language) {
		super(dataFolder, language);
		File tempFile = CrashConfigUtils.getTempFile(this.getClass(), "/resources/" + language.getLocale() + ".yml");

		YamlConfig config = getConfig();
		config.getFromConfig();

		YamlConfig defaultConfig = new YamlConfig(tempFile, new String[] {});
		defaultConfig.getFromConfig();
		for(String str: defaultConfig.getAllEntryKeys())
			if (defaultConfig.get(str) != null) if (str.startsWith("config_comments.")) config.addComments(str, defaultConfig.getStringList(str).toArray(new String[] {}));
			else
				config.addDefault(str, defaultConfig.get(str));

		config.saveConfig();
	}
}

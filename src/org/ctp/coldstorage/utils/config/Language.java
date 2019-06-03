package org.ctp.coldstorage.utils.config;

import java.util.ArrayList;
import java.util.List;

public enum Language{
	US("en_us");
	
	private String locale;
	
	Language(String l){
		locale = l;
	}

	public String getLocale() {
		return locale;
	}
	
	public static Language getLanguage(String value) {
		for(Language lang : values()) {
			if(lang.getLocale().equalsIgnoreCase(value)) {
				return lang;
			}
		}
		return US;
	}

	public static List<String> getValues() {
		List<String> langs = new ArrayList<String>();
		for(Language lang : values()) {
			langs.add(lang.getLocale());
		}
		return langs;
	}
}
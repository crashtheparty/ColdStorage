package org.ctp.coldstorage.utils;

import org.bukkit.Material;
import org.ctp.coldstorage.ColdStorage;

public class MaterialUtils {

	public static Material getMaterial(String value) {
		value = value.toUpperCase();
		
		try {
			return Material.valueOf(value);
		} catch (IllegalArgumentException ex) {
			
		}
		return null;
	}

	public static Material migrateMaterial(String value) {
		value = value.toUpperCase();
		
		switch(ColdStorage.getPlugin().getBukkitVersion().getVersionNumber()) {
		case 1:
		case 2:
		case 3:
			break;
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
			if(value.equals("SIGN")) {
				value = "OAK_SIGN";
			}
			if(value.equals("CACTUS_GREEN")) {
				value = "GREEN_DYE";
			}
			if(value.equals("ROSE_RED")) {
				value = "RED_DYE";
			}
			if(value.equals("DANDELION_YELLOW")) {
				value = "YELLOW_DYE";
			}
			if(value.equals("STONE_SLAB")) {
				value = "SMOOTH_STONE_SLAB";
			}
			break;
		}try {
			return Material.valueOf(value);
		} catch (IllegalArgumentException ex) {
			ChatUtils.sendWarning("Issue with finding value of " + value + ". Turning to null and disabling.");
		}
		return null;
	}
}

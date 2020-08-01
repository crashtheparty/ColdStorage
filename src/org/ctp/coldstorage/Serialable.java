package org.ctp.coldstorage;

import org.ctp.crashapi.item.ItemSerialization;

public interface Serialable {

	default ItemSerialization getSerial() {
		return ColdStorage.getPlugin().getItemSerial();
	}

	public static ItemSerialization get() {
		return ColdStorage.getPlugin().getItemSerial();
	}
}

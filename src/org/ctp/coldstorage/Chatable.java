package org.ctp.coldstorage;

import org.ctp.crashapi.utils.ChatUtils;

public interface Chatable {

	default ChatUtils getChat() {
		return ColdStorage.getPlugin().getChat();
	}

	public static ChatUtils get() {
		return ColdStorage.getPlugin().getChat();
	}
}

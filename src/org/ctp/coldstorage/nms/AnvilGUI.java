package org.ctp.coldstorage.nms;

import org.bukkit.entity.Player;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.nms.anvil.AnvilGUI_v1_13_R1;
import org.ctp.coldstorage.nms.anvil.AnvilGUI_v1_13_R2;
import org.ctp.coldstorage.nms.anvil.AnvilGUI_v1_14_R1;
import org.ctp.coldstorage.utils.ChatUtils;

public class AnvilGUI {
	public static void createAnvil(Player player, Anvilable anvil, boolean choice) {
		if(ColdStorage.getPlugin().getConfiguration().getAnvilEdits()) {
			switch(ColdStorage.getPlugin().getBukkitVersion().getVersionNumber()) {
			case 1:
				AnvilGUI_v1_13_R1.createAnvil(player, anvil, choice);
				break;
			case 2:
			case 3:
				AnvilGUI_v1_13_R2.createAnvil(player, anvil, choice);
				break;
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				AnvilGUI_v1_14_R1.createAnvil(player, anvil, choice);
				break;
			}
		} else {
			anvil.close(false);
			ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "listeners.use_chat"));
		}
	}
}

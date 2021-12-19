package org.ctp.coldstorage.nms;

import org.bukkit.entity.Player;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.nms.anvil.AnvilGUI_v1_16_R3;
import org.ctp.coldstorage.nms.anvil.AnvilGUI_v1_17_R1;
import org.ctp.coldstorage.nms.anvil.AnvilGUI_v1_18_R1;
import org.ctp.crashapi.CrashAPI;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.utils.ChatUtils;

public class AnvilGUINMS {
	public static void createAnvil(Player player, InventoryData anvil, boolean choice) {
		if (ColdStorage.getPlugin().getConfiguration().getAnvilEdits()) switch (CrashAPI.getPlugin().getBukkitVersion().getVersionNumber()) {
			case 16:
				AnvilGUI_v1_16_R3.createAnvil(player, anvil, choice);
				break;
			case 18:
				AnvilGUI_v1_17_R1.createAnvil(player, anvil, choice);
				break;
			case 19:
				AnvilGUI_v1_18_R1.createAnvil(player, anvil, choice);
				break;
		}
		else {
			anvil.close(false);
			Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "listeners.use_chat"));
		}
	}
}

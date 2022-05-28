package org.ctp.coldstorage.nms;

import org.bukkit.entity.Player;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.nms.anvil.*;
import org.ctp.crashapi.CrashAPI;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.nms.NMS;
import org.ctp.crashapi.utils.ChatUtils;

public class AnvilGUINMS extends NMS {
	public static void createAnvil(Player player, InventoryData anvil, boolean choice) {
		if (ColdStorage.getPlugin().getConfiguration().getAnvilEdits()) switch (CrashAPI.getPlugin().getBukkitVersion().getVersionNumber()) {
			case 16:
				AnvilGUI_v1_16_R3.createAnvil(player, anvil, choice);
				break;
			default:
				if (isSimilarOrAbove(getVersionNumbers(), 1, 18, 2)) AnvilGUI_3.createAnvil(player, anvil, choice);
				else if (isSimilarOrAbove(getVersionNumbers(), 1, 18, 0)) AnvilGUI_2.createAnvil(player, anvil, choice);
				else if (isSimilarOrAbove(getVersionNumbers(), 1, 17, 0)) AnvilGUI_1.createAnvil(player, anvil, choice);
				break;
		}
		else {
			anvil.close(false);
			Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "listeners.use_chat"));
		}
	}
}

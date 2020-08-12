package org.ctp.coldstorage.nms;

import org.bukkit.entity.Player;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.nms.anvil.*;
import org.ctp.crashapi.CrashAPI;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.utils.ChatUtils;

public class AnvilGUINMS {
	public static void createAnvil(Player player, InventoryData anvil, boolean choice) {
		if (ColdStorage.getPlugin().getConfiguration().getAnvilEdits()) switch (CrashAPI.getPlugin().getBukkitVersion().getVersionNumber()) {
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
			case 9:
			case 10:
			case 11:
				AnvilGUI_v1_15_R1.createAnvil(player, anvil, choice);
				break;
			case 12:
				AnvilGUI_v1_16_R1.createAnvil(player, anvil, choice);
				break;
			case 13:
				AnvilGUI_v1_16_R2.createAnvil(player, anvil, choice);
				break;
		}
		else {
			anvil.close(false);
			Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "listeners.use_chat"));
		}
	}
}

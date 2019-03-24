package org.ctp.coldstorage.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.InventoryUtilities;
import org.ctp.coldstorage.utils.config.ConfigUtilities;

public class ColdStorageOpen implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(player.hasPermission("coldstorage.open")) {
				ChatUtils.sendMessage(player, ConfigUtilities.OPEN_MESSAGE);
				InventoryUtilities.addInventory(player);
			} else {
				ChatUtils.sendMessage(player, "You do not have permission!");
			}
		}
		return false;
	}

}

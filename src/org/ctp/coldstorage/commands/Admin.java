package org.ctp.coldstorage.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ctp.coldstorage.inventory.admin.AdminList;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.inventory.InventoryUtils;

public class Admin implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(player.hasPermission("coldstorage.admin")) {
				ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "commands.admin"));
				AdminList list = new AdminList(player);
				InventoryUtils.addInventory(player, list);
			} else {
				ChatUtils.sendMessage(player, ChatUtils.getMessage(ChatUtils.getCodes(), "commands.no_permission"));
			}
		}
		return false;
	}

}

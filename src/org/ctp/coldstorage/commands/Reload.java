package org.ctp.coldstorage.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ctp.coldstorage.ColdStorage;

public class Reload implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("coldstorage.reload")) {
			ColdStorage.getPlugin().getConfiguration().reload(sender);
		}
		return false;
	}

}

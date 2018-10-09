package org.ctp.coldstorage.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ctp.coldstorage.ColdStorage;

public class ColdStorageReload implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		ColdStorage.plugin.reloadConfigs(sender);
		return false;
	}

}

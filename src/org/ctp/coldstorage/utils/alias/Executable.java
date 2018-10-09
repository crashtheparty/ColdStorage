package org.ctp.coldstorage.utils.alias;

import org.bukkit.command.CommandSender;

public abstract interface Executable {
	public abstract void execute(CommandSender sender, String[] params);
}
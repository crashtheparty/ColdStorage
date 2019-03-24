package org.ctp.coldstorage.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.InventoryUtilities;
import org.ctp.coldstorage.utils.config.ConfigUtilities;

public class ColdStorageAdmin implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(player.hasPermission("coldstorage.admin")) {
				if(args.length > 0) {
					OfflinePlayer[] players = Bukkit.getOfflinePlayers();
					OfflinePlayer oPlayer = null;
					for(OfflinePlayer o : players) {
						if(args[0].equalsIgnoreCase(o.getName())) {
							oPlayer = o;
							break;
						}
					}
					if(oPlayer != null) {
						if(oPlayer.getUniqueId().equals(player.getUniqueId())) {
							ChatUtils.sendMessage(player, "Can't open your own cold storage as admin. Use /csopen.");
							return false;
						}
						if(oPlayer.isOp()) {
							ChatUtils.sendMessage(player, "Can't open an op's cold storage.");
							return false;
						}
						ChatUtils.sendMessage(player, ConfigUtilities.OPEN_MESSAGE);
						InventoryUtilities.addInventory(player, oPlayer);
					} else {
						ChatUtils.sendMessage(player, "Player specified does not exist!");
					}
				} else {
					ChatUtils.sendMessage(player, "Must specify a player!");
				}
			}
		}
		return false;
	}
}

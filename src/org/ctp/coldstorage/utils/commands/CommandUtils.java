package org.ctp.coldstorage.utils.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.commands.ColdStorageCommand;
import org.ctp.coldstorage.inventory.admin.AdminList;
import org.ctp.coldstorage.inventory.storage.ListStorage;
import org.ctp.coldstorage.utils.StorageUtils;
import org.ctp.crashapi.commands.CrashCommand;
import org.ctp.crashapi.utils.ChatUtils;
import org.ctp.crashapi.utils.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CommandUtils {

	public static boolean addChest(CommandSender sender, CrashCommand details, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission(details.getPermission())) {
				Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.add_chest"));
				StorageUtils.allowNewChest(player);
				return true;
			} else
				Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.no_permission"));
		} else
			Chatable.get().sendWarning(Chatable.get().getMessage(ChatUtils.getCodes(), "commands.no_console"));
		return false;
	}

	public static boolean admin(CommandSender sender, CrashCommand details, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission(details.getPermission())) {
				Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.admin"));
				AdminList list = new AdminList(player);
				ColdStorage.getPlugin().addInventory(list);
				return true;
			} else
				Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.no_permission"));
		} else
			Chatable.get().sendWarning(Chatable.get().getMessage(ChatUtils.getCodes(), "commands.no_console"));
		return false;
	}

	public static boolean open(CommandSender sender, CrashCommand details, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission(details.getPermission())) {
				Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.open"));
				ListStorage list = new ListStorage(player);
				ColdStorage.getPlugin().addInventory(list);
				return true;
			} else
				Chatable.get().sendMessage(player, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.no_permission"));
		} else
			Chatable.get().sendWarning(Chatable.get().getMessage(ChatUtils.getCodes(), "commands.no_console"));
		return false;
	}

	public static boolean reload(CommandSender sender, CrashCommand details, String[] args) {
		Player p = null;
		if (sender instanceof Player) p = (Player) sender;
		if (sender.hasPermission("coldstorage.reload")) {
			ColdStorage.getPlugin().getConfiguration().reload(sender);
			return true;
		}
		Chatable.get().sendMessage(sender, p, Chatable.get().getMessage(ChatUtils.getCodes(), "commands.no_console"), Level.WARNING);
		return false;
	}

	public static boolean printHelp(CommandSender sender, String label) {
		Player player = null;
		if (sender instanceof Player) player = (Player) sender;
		for(CSCommand command: ColdStorageCommand.getCommands())
			if (sender.hasPermission(command.getPermission()) && ColdStorageCommand.containsCommand(command, label)) {
				Chatable.get().sendMessage(sender, player, StringUtils.decodeString("\n" + command.getFullUsage()), Level.INFO);
				return true;
			}
		return printHelp(sender, 1);
	}

	@SuppressWarnings("unchecked")
	public static boolean printHelp(CommandSender sender, int page) {
		Player player = null;
		if (sender instanceof Player) player = (Player) sender;

		List<CSCommand> playerCommands = new ArrayList<CSCommand>();
		for(CSCommand command: ColdStorageCommand.getCommands())
			if (sender.hasPermission(command.getPermission())) playerCommands.add(command);

		if ((page - 1) * 5 > playerCommands.size()) return printHelp(sender, page - 1);

		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%page%", page);
		String commandsPage = Chatable.get().getMessage(codes, "commands.help.commands_page");
		if (player != null) {
			JSONArray json = new JSONArray();
			JSONObject first = new JSONObject();
			first.put("text", "\n" + ChatColor.DARK_BLUE + "******");
			JSONObject second = new JSONObject();
			if (page > 1) {
				second.put("text", ChatColor.GREEN + "<<<");
				HashMap<Object, Object> action = new HashMap<Object, Object>();
				action.put("action", "run_command");
				action.put("value", "/es help " + (page - 1));
				second.put("clickEvent", action);
			} else
				second.put("text", ChatColor.DARK_BLUE + "***");
			JSONObject third = new JSONObject();
			third.put("text", ChatColor.DARK_BLUE + "****** " + commandsPage + ChatColor.DARK_BLUE + " ******");
			JSONObject fourth = new JSONObject();
			if (playerCommands.size() > page * 5) {
				fourth.put("text", ChatColor.GREEN + ">>>");
				HashMap<Object, Object> action = new HashMap<Object, Object>();
				action.put("action", "run_command");
				action.put("value", "/es help " + (page + 1));
				fourth.put("clickEvent", action);
			} else
				fourth.put("text", ChatColor.DARK_BLUE + "***");
			JSONObject fifth = new JSONObject();
			fifth.put("text", ChatColor.DARK_BLUE + "******" + "\n");
			json.add(first);
			json.add(second);
			json.add(third);
			json.add(fourth);
			json.add(fifth);
			for(int i = 0; i < 5; i++) {
				int num = i + (page - 1) * 5;
				if (num >= playerCommands.size()) break;
				CrashCommand c = playerCommands.get(num);
				JSONObject name = new JSONObject();
				JSONObject desc = new JSONObject();
				JSONObject action = new JSONObject();
				action.put("action", "run_command");
				action.put("value", "/es help " + c.getCommand());
				name.put("text", ChatColor.GOLD + c.getCommand());
				name.put("clickEvent", action);
				json.add(name);
				HashMap<String, Object> descCodes = new HashMap<String, Object>();
				descCodes.put("%description%", Chatable.get().getMessage(ChatUtils.getCodes(), c.getDescriptionPath()));
				desc.put("text", shrink(Chatable.get().getMessage(descCodes, "commands.help.commands_info_shrink")) + "\n");
				json.add(desc);
			}
			json.add(first);
			json.add(second);
			json.add(third);
			json.add(fourth);
			json.add(fifth);
			Chatable.get().sendRawMessage(player, json.toJSONString());
		} else {
			String message = "\n" + ChatColor.DARK_BLUE + "******" + (page > 1 ? "<<<" : "***") + "****** " + commandsPage + ChatColor.DARK_BLUE + " ******" + (playerCommands.size() > page * 5 ? ">>>" : "***") + "******" + "\n";
			for(int i = 0; i < 5; i++) {
				int num = i + (page - 1) * 5;
				if (num >= playerCommands.size()) break;
				CrashCommand c = playerCommands.get(num);
				HashMap<String, Object> descCodes = new HashMap<String, Object>();
				descCodes.put("%command%", c.getCommand());
				descCodes.put("%description%", Chatable.get().getMessage(ChatUtils.getCodes(), c.getDescriptionPath()));
				message += shrink(Chatable.get().getMessage(descCodes, "commands.help.commands_info_shrink")) + "\n";
			}
			message += "\n" + ChatColor.DARK_BLUE + "******" + (page > 1 ? "<<<" : "***") + "****** " + commandsPage + ChatColor.DARK_BLUE + " ******" + (playerCommands.size() > page * 5 ? ">>>" : "***") + "******" + "\n";
			Chatable.get().sendToConsole(Level.INFO, message);
		}

		return true;
	}

	private static String shrink(String s) {
		if (s.length() > 60) return s.substring(0, 58) + "...";
		return s;
	}
}

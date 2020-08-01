package org.ctp.coldstorage.commands;

import java.util.*;
import java.util.logging.Level;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.utils.commands.CSCommand;
import org.ctp.coldstorage.utils.commands.CSCommandCallable;
import org.ctp.coldstorage.utils.commands.CommandUtils;

public class ColdStorageCommand implements CommandExecutor, TabCompleter {

	private static List<CSCommand> commands;
	private final CSCommand open = new CSCommand("csopen", "commands.aliases.csopen", "commands.descriptions.csopen", "commands.usage.csopen", "coldstorage.open");
	private final CSCommand admin = new CSCommand("csadmin", "commands.aliases.csadmin", "commands.descriptions.csadmin", "commands.usage.csadmin", "coldstorage.admin");
	private final CSCommand reload = new CSCommand("csreload", "commands.aliases.csreload", "commands.descriptions.csreload", "commands.usage.csreload", "coldstorage.reload");
	private final CSCommand chest = new CSCommand("cschest", "commands.aliases.cschest", "commands.descriptions.cschest", "commands.usage.cschest", "coldstorage.chest");
	private final CSCommand help = new CSCommand("cshelp", "commands.aliases.cshelp", "commands.descriptions.cshelp", "commands.usage.cshelp", "coldstorage.help");
	
	public ColdStorageCommand() {
		commands = new ArrayList<CSCommand>();
		commands.add(open);
		commands.add(admin);
		commands.add(reload);
		commands.add(chest);
		commands.add(help);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		label = label.substring(label.indexOf(':') + 1);
		for(CSCommand c: commands) {
			String[] check;
			if (containsCommand(c, label)) {
				check = new String[args.length + 1];
				check[0] = label;
				for(int i = 0; i < args.length; i++)
					check[i + 1] = args[i];
				args = check;
				break;
			}
		}
		if (args.length == 0 || args.length == 1 && containsCommand(help, args[0])) return CommandUtils.printHelp(sender, 1);
		else if (args.length == 2 && containsCommand(help, args[0])) {
			int page = 0;
			try {
				page = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {

			}
			if (page > 0) return CommandUtils.printHelp(sender, page);
			else
				return CommandUtils.printHelp(sender, args[1]);
		}
		final String[] finalArgs = args;
		Player player = null;
		if (sender instanceof Player) player = (Player) sender;
		for(CSCommand command: commands) {
			if (command == help) continue;
			if (containsCommand(command, args[0])) try {
				return new CSCommandCallable(command, sender, finalArgs).call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		HashMap<String, Object> codes = new HashMap<String, Object>();
		codes.put("%command%", args[0]);
		Chatable.get().sendMessage(sender, player, Chatable.get().getMessage(codes, "commands.no-command"), Level.WARNING);
		return true;
	}

	public static boolean containsCommand(CSCommand details, String s) {
		return s.equals(details.getCommand()) || details.getAliases().contains(s);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> all = new ArrayList<String>();

		label = label.substring(label.indexOf(':') + 1);
		for(CSCommand c: commands) {
			String[] check;
			if (containsCommand(c, label)) {
				check = new String[args.length + 1];
				check[0] = label;
				for(int i = 0; i < args.length; i++)
					check[i + 1] = args[i];
				args = check;
				break;
			}
		}
		int i = args.length - 1;

		if (i == 0) all.addAll(help(sender, args[i]));
		if (i > 0 && noArgCommands(0).contains(args[0])) return all;
		if (i == 1 && containsCommand(help, args[0]) && sender.hasPermission(help.getPermission())) all.addAll(help(sender, args[i]));

		return all;
	}

	private List<String> removeComplete(List<String> strings, String startsWith) {
		Iterator<String> iter = strings.iterator();
		while (iter.hasNext()) {
			String entry = iter.next();
			boolean remove = true;
			if (entry.startsWith(startsWith)) remove = false;// is fine
			if (entry.indexOf('_') > -1) {
				String split = entry.substring(entry.indexOf('_') + 1);
				while (split.length() > 0) {
					if (split.startsWith(startsWith)) remove = false;// is fine
					if (split.indexOf('_') > -1) split = split.substring(split.indexOf('_') + 1);
					else
						split = "";
				}
			}
			if (remove) iter.remove();
		}
		return strings;
	}

	private List<String> help(CommandSender sender, String startsWith) {
		List<String> strings = new ArrayList<String>();
		for(CSCommand command: commands)
			if (sender.hasPermission(command.getPermission())) {
				strings.add(command.getCommand());
				strings.addAll(command.getAliases());
			}
		return removeComplete(strings, startsWith);
	}

	private List<String> noArgCommands(int i) {
		List<String> strings = new ArrayList<String>();
		if (i == 0) {
			strings.add(chest.getCommand());
			strings.addAll(chest.getAliases());
			strings.add(admin.getCommand());
			strings.addAll(admin.getAliases());
			strings.add(open.getCommand());
			strings.addAll(open.getAliases());
			strings.add(reload.getCommand());
			strings.addAll(reload.getAliases());
		}
		return strings;
	}

	public static List<CSCommand> getCommands() {
		return commands;
	}
}

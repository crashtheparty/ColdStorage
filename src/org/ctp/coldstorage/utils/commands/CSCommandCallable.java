package org.ctp.coldstorage.utils.commands;

import org.bukkit.command.CommandSender;
import org.ctp.crashapi.commands.CrashCommandCallable;

public class CSCommandCallable implements CrashCommandCallable {

	private final CSCommand command;
	private final String[] args;
	private final CommandSender sender;

	public CSCommandCallable(CSCommand command, CommandSender sender, String[] args) {
		this.command = command;
		this.sender = sender;
		this.args = args;
	}

	@Override
	public CSCommand getCommand() {
		return command;
	}

	@Override
	public String[] getArgs() {
		return args;
	}

	@Override
	public CommandSender getSender() {
		return sender;
	}

	@Override
	public Boolean call() throws Exception {
		boolean run = false;
		try {
			run = fromCommand();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!run && sender.hasPermission(command.getPermission())) CommandUtils.printHelp(sender, command.getCommand());
		return true;
	}

	@Override
	public Boolean fromCommand() {
		switch (command.getCommand()) {
			case "csadmin":
				return CommandUtils.admin(sender, command, args);
			case "cschest":
				return CommandUtils.addChest(sender, command, args);
			case "csopen":
				return CommandUtils.open(sender, command, args);
			case "csreload":
				return CommandUtils.reload(sender, command, args);
		}
		return null;
	}

}

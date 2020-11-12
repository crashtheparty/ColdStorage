package org.ctp.coldstorage.utils.alias;

import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public final class Alias implements Nameable, Executable {
	private static final Pattern ILLEGAL_CHARACTERS = Pattern.compile("[\\s\\/:*?\"<>|#]");
	private String prefix, name;
	private String description;
	private String permission;
	private AliasCommand command;
	private PluginCommand pCommand;

	public Alias(String name, PluginCommand pCommand) throws Exception {
		if (!isValid(name)) throw new IllegalArgumentException("Name cannot contain illegal characters");
		this.name = name;
		this.pCommand = pCommand;
		this.prefix = pCommand.getPlugin().getName().toLowerCase();
		permission = pCommand.getPermission();
		if (permission == null) permission = "";
		command = new AliasCommand(this);
		if (!command.register()) throw new Exception("Failed to register the alias as a command");
	}

	public static boolean isValid(String name) {
		return !ILLEGAL_CHARACTERS.matcher(name).find();
	}

	public void setName(String name) {
		if (!isValid(name)) throw new IllegalArgumentException("Name cannot contain illegal characters");
		this.name = name;
		command.unregister();
		command = new AliasCommand(this);
		command.register();
	}

	public void unregister() {
		command.unregister();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public void execute(final CommandSender sender, final String[] params) {
		pCommand.execute(sender, getName(), params);
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
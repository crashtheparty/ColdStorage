package org.ctp.coldstorage.utils.commands;

import java.util.HashMap;

import org.ctp.coldstorage.Chatable;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.crashapi.commands.CrashCommand;
import org.ctp.crashapi.utils.ChatUtils;

public class CSCommand extends CrashCommand {

	public CSCommand(String command, String aliasesPath, String descriptionPath, String usagePath, String permission) {
		super(ColdStorage.getPlugin(), command, aliasesPath, descriptionPath, usagePath, permission);
	}

	@Override
	public String getFullUsage() {
		HashMap<String, Object> codes = ChatUtils.getCodes();
		String usage = "";
		usage += "/cs " + getCommand() + ": " + Chatable.get().getMessage(codes, getDescriptionPath()) + "\n";
		codes.put("%usage%", Chatable.get().getMessage(codes, getUsagePath() + ".string"));
		codes.put("%aliases%", getAliasesString());
		usage += Chatable.get().getMessage(codes, getUsagePath() + ".main");
		return usage;
	}

}

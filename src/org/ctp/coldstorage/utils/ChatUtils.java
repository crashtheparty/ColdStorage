package org.ctp.coldstorage.utils;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.ctp.coldstorage.ColdStorage;

public class ChatUtils {

	public static void sendMessage(Player player, String message) {
		if (message != null && !message.trim().equals("")) {
			player.sendMessage(starter() + message);
		}
	}

	public static void sendMessage(Player player, String message, String url) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " [{\"text\":\""
				+ starter() + message + "\"},{\"text\":\"" + url
				+ "\", \"italic\": true, \"color\": \"green\", \"clickEvent\":{\"action\":\"open_url\",\"value\":\""
				+ url + "\"}}]");
	}

	public static void sendMessage(Player player, String[] messages) {
		for(String s: messages) {
			sendMessage(player, s);
		}
	}

	public static void sendRawMessage(Player player, String json) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + json);
	}

	private static String starter(){
		return ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "ColdStorage" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;
	}

	public static void sendToConsole(Level level, String message) {
		ColdStorage.plugin.getLogger().log(level, message);
	}

	public static void sendWarning(String message) {
		sendToConsole(Level.WARNING, message);
	}

	public static void sendInfo(String message) {
		sendToConsole(Level.INFO, message);
	}

	public static void sendSevere(String message) {
		sendToConsole(Level.SEVERE, message);
	}

	public static HashMap<String, Object> getCodes() {
		return new HashMap<String, Object>();
	}

	/**
	 * Hides text in color codes
	 *
	 * @param text
	 *            The text to hide
	 * @return The hidden text
	 */
	@Nonnull
	public static String hideText(@Nonnull String text) {
		Objects.requireNonNull(text, "text can not be null!");

		StringBuilder output = new StringBuilder();

		String hex = asciiToHex(text);

		for(char c: hex.toCharArray()) {
			output.append(ChatColor.COLOR_CHAR).append(c);
		}

		return output.toString();
	}

	/**
	 * Reveals the text hidden in color codes
	 *
	 * @param text
	 *            The hidden text
	 * @throws IllegalArgumentException
	 *             if an error occurred while decoding.
	 * @return The revealed text
	 */
	@Nonnull
	public static String revealText(@Nonnull String text) {
		Objects.requireNonNull(text, "text can not be null!");

		if (text.isEmpty()) {
			return text;
		}

		char[] chars = text.toCharArray();

		char[] hexChars = new char[chars.length / 2];

		IntStream.range(0, chars.length).filter(value -> value % 2 != 0)
				.forEach(value -> hexChars[value / 2] = chars[value]);
		
		String newChars = "";
		for(char c : hexChars) {
			newChars += c;
		}

		return new String(hexToASCII(newChars));
	}

	private static String asciiToHex(String asciiValue) {
		char[] chars = asciiValue.toCharArray();
		StringBuffer hex = new StringBuffer();
		for(int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}
		return hex.toString();
	}

	private static String hexToASCII(String hexValue) {
		StringBuilder output = new StringBuilder("");
		for(int i = 0; i < hexValue.length(); i += 2) {
			String str = hexValue.substring(i, i + 2);
			output.append((char) Integer.parseInt(str, 16));
		}
		return output.toString();
	}
}

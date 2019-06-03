package org.ctp.coldstorage.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.ctp.coldstorage.ColdStorage;

public class ChatUtils {
	
	public static void sendToConsole(String message) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(starter()) + message);
	}
	
	public static void sendMessage(Player player, String message, String url) {
		Bukkit.getServer().dispatchCommand(
		        Bukkit.getConsoleSender(),
		        "tellraw " + player.getName() + 
		        " [{\"text\":\"" + starter() + message + "\"},{\"text\":\"" + url + "\", \"italic\": true, \"color\": \"green\", \"clickEvent\":{\"action\":\"open_url\",\"value\":\"" +
		        url + "\"}}]");
	}
	
	public static void broadcast(String message){
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(starter() + message);
		}
	}
	
	public static void broadcast(String message, List<Player> players){
		for(Player player : players){
			player.sendMessage(starter() + message);
		}
	}
	
	public static String getMessage(HashMap<String, Object> codes, String location) {
		String s = "";
		try {
			s = translateCodes(codes, ChatColor.translateAlternateColorCodes('&',
					ColdStorage.getPlugin().getConfiguration().getLanguageFile().getString(location)));
		} catch (Exception e) {

		}
		if (s.isEmpty())
			return location + " must be a string.";
		return s;
	}

	public static List<String> getMessages(HashMap<String, Object> codes, String location) {
		List<String> messages = ColdStorage.getPlugin().getConfiguration().getLanguageFile()
				.getStringList(location);
		if (messages == null) {
			messages = new ArrayList<String>();
			messages.add(ColdStorage.getPlugin().getConfiguration().getLanguageFile().getString(location));
		}
		for(int i = 0; i < messages.size(); i++) {
			messages.set(i, translateCodes(codes, ChatColor.translateAlternateColorCodes('&', messages.get(i))));
		}
		if (messages.size() == 0)
			messages.add(location + " must be a list or a string.");
		return messages;
	}

	private static String translateCodes(HashMap<String, Object> codes, String str) {
		for(Iterator<java.util.Map.Entry<String, Object>> it = codes.entrySet().iterator(); it.hasNext();) {
			java.util.Map.Entry<String, Object> e = it.next();
			if (e.getValue() != null) {
				str = str.replaceAll(e.getKey(), e.getValue().toString());
			}
		}
		return str;
	}

	public static HashMap<String, Object> getCodes() {
		return new HashMap<String, Object>();
	}

	public static HashMap<String, Object> getCodes(String string, Object object) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(string, object);
		return map;
	}
	
	public static void sendToConsole(Level level, String message) {
		ColdStorage.getPlugin().getLogger().log(level, message);
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
	
	private static String starter(){
		return getMessage(ChatUtils.getCodes(), "coldstorage.starter");
	}
	
	public static void sendMessage(Player player, String message){
		player.sendMessage(starter() + message);
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

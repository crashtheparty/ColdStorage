package org.ctp.coldstorage.utils;

import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.WHITE;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
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
	
	private static String starter(){
		return DARK_GRAY + "[" + GOLD + "ColdStorage" + DARK_GRAY + "] " + WHITE;
	}
	
	public static void sendMessage(Player player, String message){
		player.sendMessage(starter() + message);
	}
	
    /**
     * Hides text in color codes
     *
     * @param text The text to hide
     * @return The hidden text
     */
    @Nonnull
    public static String hideText(@Nonnull String text) {
        Objects.requireNonNull(text, "text can not be null!");

        StringBuilder output = new StringBuilder();

        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        String hex = Hex.encodeHexString(bytes);

        for (char c : hex.toCharArray()) {
            output.append(ChatColor.COLOR_CHAR).append(c);
        }

        return output.toString();
    }

    /**
     * Reveals the text hidden in color codes
     *
     * @param text The hidden text
     * @throws IllegalArgumentException if an error occurred while decoding.
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

        IntStream.range(0, chars.length)
                .filter(value -> value % 2 != 0)
                .forEach(value -> hexChars[value / 2] = chars[value]);

        try {
            return new String(Hex.decodeHex(hexChars), StandardCharsets.UTF_8);
        } catch (DecoderException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Couldn't decode text", e);
        }
    }
}

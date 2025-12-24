package fr.minecasa.utils;

import net.md_5.bungee.api.ChatColor;

public class ChatUtils {

    private static final String PREFIX = "§6§l[§c§lMINECASA§6§l] §r";

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String format(String message) {
        return PREFIX + colorize(message);
    }

    public static String formatError(String message) {
        return PREFIX + "§c" + message;
    }

    public static String formatSuccess(String message) {
        return PREFIX + "§a" + message;
    }

    public static String formatInfo(String message) {
        return PREFIX + "§b" + message;
    }

    public static String formatWarning(String message) {
        return PREFIX + "§e" + message;
    }

    public static String createLine(char character, int length) {
        return String.valueOf(character).repeat(Math.max(0, length));
    }

    public static String createSeparator() {
        return "§7§m" + createLine('-', 40);
    }

    public static void sendGameStartMessage() {
        String[] lines = {
            "",
            "§6§l╔══════════════════════════════════╗",
            "§6§l║  §c§lLA PARTIE COMMENCE !         §6§l║",
            "§6§l║                                  §6§l║",
            "§6§l║  §e§lObjectif: §f§lRécupérer les cibles  §6§l║",
            "§6§l║  §e§lMission 1: §f§lEscorter 3 NPCs     §6§l║",
            "§6§l╚══════════════════════════════════╝",
            ""
        };

        for (String line : lines) {
            org.bukkit.Bukkit.broadcastMessage(line);
        }
    }

    public static void sendCountdownMessage(int seconds) {
        if (seconds <= 5 || seconds % 10 == 0) {
            org.bukkit.Bukkit.broadcastMessage(format("§eLa partie commence dans §c§l" + seconds + " §esecondes !"));
        }
    }

    public static void sendMissionComplete(String missionName) {
        String[] lines = {
            "",
            "§a§l╔══════════════════════════════════╗",
            "§a§l║  §2§lMISSION ACCOMPLIE !          §a§l║",
            "§a§l║                                  §a§l║",
            "§a§l║  §f§l" + centerText(missionName, 30) + "  §a§l║",
            "§a§l╚══════════════════════════════════╝",
            ""
        };

        for (String line : lines) {
            org.bukkit.Bukkit.broadcastMessage(line);
        }
    }

    private static String centerText(String text, int length) {
        if (text.length() >= length) return text.substring(0, length);
        int padding = (length - text.length()) / 2;
        return " ".repeat(padding) + text;
    }
}

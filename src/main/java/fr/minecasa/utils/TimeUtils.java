package fr.minecasa.utils;

public class TimeUtils {

    public static String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String formatTimeColored(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String color = "§a";
        if (totalSeconds <= 60) {
            color = "§c";
        } else if (totalSeconds <= 180) {
            color = "§e";
        }

        return color + String.format("%02d:%02d", minutes, seconds);
    }

    public static String formatCountdown(int seconds) {
        if (seconds <= 5) {
            return "§c§l" + seconds;
        } else if (seconds <= 10) {
            return "§e§l" + seconds;
        } else {
            return "§a" + seconds;
        }
    }
}

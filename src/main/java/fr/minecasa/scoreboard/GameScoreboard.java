package fr.minecasa.scoreboard;

import fr.minecasa.missions.Mission;
import fr.minecasa.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class GameScoreboard {

    private final Player player;
    private Scoreboard scoreboard;
    private Objective objective;

    public GameScoreboard(Player player) {
        this.player = player;
        createScoreboard();
    }

    private void createScoreboard() {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("game", "dummy", formatTitle());

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public void update(int gameTime, Mission currentMission, int missionNumber, int totalMissions) {
        clearScoreboard();

        int line = 11;
        setLine(line--, "§7§m                    ");
        setLine(line--, "");
        setLine(line--, "§f§lTemps: " + TimeUtils.formatTimeColored(gameTime));
        setLine(line--, "");
        setLine(line--, "§e§lMission " + missionNumber + "/" + totalMissions);

        if (currentMission != null) {
            setLine(line--, "§7" + truncate(currentMission.getName(), 20));
            setLine(line--, "");
            setLine(line--, "§a§lProgression:");
            setLine(line--, "  §f" + currentMission.getProgressString() + " §7(" + currentMission.getProgressPercentage() + "%)");

            String progressBar = createProgressBar(currentMission.getProgressPercentage());
            setLine(line--, "  " + progressBar);
        } else {
            setLine(line--, "§7Aucune mission");
            setLine(line--, "");
            setLine(line--, "");
            setLine(line--, "");
        }

        setLine(line--, "");
        setLine(line, "§7§m                    ");
    }

    private void setLine(int line, String text) {
        Team team = scoreboard.getTeam("line" + line);
        if (team == null) {
            team = scoreboard.registerNewTeam("line" + line);
        }

        String entry = ChatColor.values()[Math.min(line, 15)].toString();
        if (!team.hasEntry(entry)) {
            team.addEntry(entry);
        }

        team.setPrefix(text);
        objective.getScore(entry).setScore(line);
    }

    private void clearScoreboard() {
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
    }

    private String formatTitle() {
        return "§6§l⚡ §c§lMINECASA §6§l⚡";
    }

    private String createProgressBar(int percentage) {
        int bars = 10;
        int filled = (int) ((percentage / 100.0) * bars);

        StringBuilder bar = new StringBuilder("§8[");
        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                bar.append("§a■");
            } else {
                bar.append("§7■");
            }
        }
        bar.append("§8]");

        return bar.toString();
    }

    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    public void remove() {
        if (objective != null) {
            objective.unregister();
        }
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}

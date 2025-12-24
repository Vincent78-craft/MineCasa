package fr.minecasa.scoreboard;

import fr.minecasa.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class LobbyScoreboard {

    private final Player player;
    private Scoreboard scoreboard;
    private Objective objective;

    public LobbyScoreboard(Player player) {
        this.player = player;
        createScoreboard();
    }

    private void createScoreboard() {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("lobby", "dummy", formatTitle());

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public void update(int countdown, int playerCount) {
        clearScoreboard();

        setLine(9, "§7§m                    ");
        setLine(8, "");
        setLine(7, "§f§lJoueurs: §a" + playerCount);
        setLine(6, "");
        if (countdown > 0) {
            setLine(5, "§e§lDébut dans:");
            setLine(4, "  " + TimeUtils.formatCountdown(countdown) + " §esecondes");
        } else {
            setLine(5, "§a§lEn attente...");
            setLine(4, "");
        }
        setLine(3, "");
        setLine(2, "§6§lMinimum: §e1 joueur");
        setLine(1, "");
        setLine(0, "§7§m                    ");
    }

    private void setLine(int line, String text) {
        Team team = scoreboard.getTeam("line" + line);
        if (team == null) {
            team = scoreboard.registerNewTeam("line" + line);
        }

        String entry = ChatColor.values()[line].toString();
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

    public void remove() {
        if (objective != null) {
            objective.unregister();
        }
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}

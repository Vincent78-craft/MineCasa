package fr.minecasa.scoreboard;

import fr.minecasa.MineCasa;
import fr.minecasa.game.GameState;
import fr.minecasa.missions.Mission;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final MineCasa plugin;
    private final Map<UUID, LobbyScoreboard> lobbyScoreboards;
    private final Map<UUID, GameScoreboard> gameScoreboards;

    public ScoreboardManager(MineCasa plugin) {
        this.plugin = plugin;
        this.lobbyScoreboards = new HashMap<>();
        this.gameScoreboards = new HashMap<>();
    }

    public void createLobbyScoreboard(Player player) {
        removeLobbyScoreboard(player);
        LobbyScoreboard scoreboard = new LobbyScoreboard(player);
        lobbyScoreboards.put(player.getUniqueId(), scoreboard);
    }

    public void createGameScoreboard(Player player) {
        removeGameScoreboard(player);
        GameScoreboard scoreboard = new GameScoreboard(player);
        gameScoreboards.put(player.getUniqueId(), scoreboard);
    }

    public void removeLobbyScoreboard(Player player) {
        LobbyScoreboard scoreboard = lobbyScoreboards.remove(player.getUniqueId());
        if (scoreboard != null) {
            scoreboard.remove();
        }
    }

    public void removeGameScoreboard(Player player) {
        GameScoreboard scoreboard = gameScoreboards.remove(player.getUniqueId());
        if (scoreboard != null) {
            scoreboard.remove();
        }
    }

    public void updateLobbyScoreboard(Player player, int countdown, int playerCount) {
        LobbyScoreboard scoreboard = lobbyScoreboards.get(player.getUniqueId());
        if (scoreboard != null) {
            scoreboard.update(countdown, playerCount);
        }
    }

    public void updateGameScoreboard(Player player, int gameTime, Mission currentMission, int missionNumber, int totalMissions) {
        GameScoreboard scoreboard = gameScoreboards.get(player.getUniqueId());
        if (scoreboard != null) {
            scoreboard.update(gameTime, currentMission, missionNumber, totalMissions);
        }
    }

    public void updateAllLobbyScoreboards(int countdown, int playerCount) {
        for (UUID uuid : lobbyScoreboards.keySet()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                updateLobbyScoreboard(player, countdown, playerCount);
            }
        }
    }

    public void updateAllGameScoreboards(int gameTime, Mission currentMission, int missionNumber, int totalMissions) {
        for (UUID uuid : gameScoreboards.keySet()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                updateGameScoreboard(player, gameTime, currentMission, missionNumber, totalMissions);
            }
        }
    }

    public void switchToGameScoreboard(Player player) {
        removeLobbyScoreboard(player);
        createGameScoreboard(player);
    }

    public void switchToLobbyScoreboard(Player player) {
        removeGameScoreboard(player);
        createLobbyScoreboard(player);
    }

    public void removeAllScoreboards() {
        for (UUID uuid : new HashMap<>(lobbyScoreboards).keySet()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                removeLobbyScoreboard(player);
            }
        }

        for (UUID uuid : new HashMap<>(gameScoreboards).keySet()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                removeGameScoreboard(player);
            }
        }
    }
}

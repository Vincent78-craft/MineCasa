package fr.minecasa.game;

import fr.minecasa.MineCasa;
import fr.minecasa.missions.Mission;
import fr.minecasa.missions.MissionManager;
import fr.minecasa.missions.types.EscortMission;
import fr.minecasa.scoreboard.ScoreboardManager;
import fr.minecasa.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GameManager {

    private final MineCasa plugin;
    private final Map<UUID, GamePlayer> players;
    private GameState state;
    private int countdown;
    private int gameTime;
    private BukkitTask countdownTask;
    private BukkitTask gameTask;

    private final MissionManager missionManager;
    private final ScoreboardManager scoreboardManager;

    public GameManager(MineCasa plugin) {
        this.plugin = plugin;
        this.players = new HashMap<>();
        this.state = GameState.LOBBY;
        this.countdown = 10;
        this.gameTime = 0;

        this.missionManager = new MissionManager(plugin);
        this.scoreboardManager = new ScoreboardManager(plugin);

        registerMissions();
    }

    private void registerMissions() {
        EscortMission escortMission = new EscortMission(
            plugin,
            plugin.getNpcManager(),
            plugin.getMapManager().getDeliveryZone()
        );
        missionManager.registerMission(escortMission);
    }

    public void addPlayer(Player player) {
        GamePlayer gamePlayer = new GamePlayer(player);
        players.put(player.getUniqueId(), gamePlayer);

        player.teleport(plugin.getMapManager().getLobbySpawn());
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().clear();

        scoreboardManager.createLobbyScoreboard(player);
        updateLobbyScoreboards();

        player.sendMessage(ChatUtils.format("§aBienvenue sur §6§lMINECASA §a!"));

        if (state == GameState.LOBBY && players.size() >= 1 && countdownTask == null) {
            startCountdown();
        }
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        scoreboardManager.removeLobbyScoreboard(player);
        scoreboardManager.removeGameScoreboard(player);

        if (state == GameState.LOBBY || state == GameState.STARTING) {
            updateLobbyScoreboards();

            if (players.size() < 1 && countdownTask != null) {
                stopCountdown();
                Bukkit.broadcastMessage(ChatUtils.format("§cPas assez de joueurs ! Compte à rebours annulé."));
            }
        }

        if (state == GameState.IN_GAME && players.isEmpty()) {
            endGame();
        }
    }

    private void startCountdown() {
        if (countdownTask != null) return;

        state = GameState.STARTING;
        countdown = 10;

        Bukkit.broadcastMessage(ChatUtils.format("§aLe jeu commence dans §e10 secondes §a!"));

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (players.size() < 1) {
                    stopCountdown();
                    Bukkit.broadcastMessage(ChatUtils.format("§cPas assez de joueurs ! Annulation..."));
                    return;
                }

                if (countdown <= 0) {
                    startGame();
                    this.cancel();
                    return;
                }

                ChatUtils.sendCountdownMessage(countdown);
                updateLobbyScoreboards();
                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void stopCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        state = GameState.LOBBY;
        countdown = 10;
        updateLobbyScoreboards();
    }

    private void startGame() {
        state = GameState.IN_GAME;
        gameTime = 0;

        ChatUtils.sendGameStartMessage();

        plugin.getMapManager().createDeliveryZoneMarker();

        for (UUID uuid : players.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.teleport(plugin.getMapManager().getGameSpawn());
                scoreboardManager.switchToGameScoreboard(player);
            }
        }

        missionManager.startNextMission();

        startGameTimer();
    }

    private void startGameTimer() {
        gameTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (state != GameState.IN_GAME) {
                    this.cancel();
                    return;
                }

                gameTime++;
                updateGameScoreboards();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void onMissionComplete() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (missionManager.hasNextMission()) {
                Bukkit.broadcastMessage(ChatUtils.format("§eProchaine mission dans 5 secondes..."));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    missionManager.startNextMission();
                }, 100L);
            } else {
                Bukkit.broadcastMessage(ChatUtils.formatSuccess("§a§l✓ Toutes les missions sont terminées !"));
                Bukkit.getScheduler().runTaskLater(plugin, this::endGame, 100L);
            }
        }, 40L);
    }

    private void endGame() {
        state = GameState.ENDING;

        if (gameTask != null) {
            gameTask.cancel();
        }

        missionManager.stopCurrentMission();

        String[] lines = {
            "",
            "§6§l╔══════════════════════════════════╗",
            "§6§l║     §e§lFIN DE LA PARTIE !          §6§l║",
            "§6§l║                                  §6§l║",
            "§6§l║  §f§lTemps total: §e" + fr.minecasa.utils.TimeUtils.formatTime(gameTime) + "           §6§l║",
            "§6§l║  §f§lJoueurs: §a" + players.size() + "                  §6§l║",
            "§6§l╚══════════════════════════════════╝",
            ""
        };

        for (String line : lines) {
            Bukkit.broadcastMessage(line);
        }

        Bukkit.getScheduler().runTaskLater(plugin, this::resetGame, 100L);
    }

    private void resetGame() {
        missionManager.reset();
        scoreboardManager.removeAllScoreboards();

        plugin.getMapManager().removeDeliveryZoneMarker();

        for (UUID uuid : new HashSet<>(players.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.teleport(plugin.getMapManager().getLobbySpawn());
                scoreboardManager.createLobbyScoreboard(player);
            }
        }

        state = GameState.LOBBY;
        gameTime = 0;
        countdown = 10;

        updateLobbyScoreboards();

        if (players.size() >= 1) {
            startCountdown();
        }
    }

    private void updateLobbyScoreboards() {
        scoreboardManager.updateAllLobbyScoreboards(countdown, players.size());
    }

    private void updateGameScoreboards() {
        Mission currentMission = missionManager.getCurrentMission();
        int missionNumber = missionManager.getCurrentMissionNumber();
        int totalMissions = missionManager.getTotalMissions();

        scoreboardManager.updateAllGameScoreboards(gameTime, currentMission, missionNumber, totalMissions);
    }

    public GameState getState() {
        return state;
    }

    public Map<UUID, GamePlayer> getPlayers() {
        return players;
    }

    public GamePlayer getGamePlayer(UUID uuid) {
        return players.get(uuid);
    }

    public MissionManager getMissionManager() {
        return missionManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public void shutdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        if (gameTask != null) {
            gameTask.cancel();
        }
        missionManager.stopCurrentMission();
        scoreboardManager.removeAllScoreboards();
        plugin.getMapManager().removeDeliveryZoneMarker();
    }
}

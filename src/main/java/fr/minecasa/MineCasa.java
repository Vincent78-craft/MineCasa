package fr.minecasa;

import fr.minecasa.game.GameManager;
import fr.minecasa.listeners.NpcInteractionListener;
import fr.minecasa.listeners.PlayerConnectionListener;
import fr.minecasa.map.MapManager;
import fr.minecasa.npcs.NpcManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MineCasa extends JavaPlugin {

    private GameManager gameManager;
    private NpcManager npcManager;
    private MapManager mapManager;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        getLogger().info("╔═══════════════════════════════════╗");
        getLogger().info("║                                   ║");
        getLogger().info("║      MINECASA - Initialisation    ║");
        getLogger().info("║   Inspiré de La Casa de Papel     ║");
        getLogger().info("║                                   ║");
        getLogger().info("╚═══════════════════════════════════╝");

        if (!checkDependencies()) {
            getLogger().severe("Dépendances manquantes ! Désactivation du plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        initializeManagers();
        registerListeners();

        long loadTime = System.currentTimeMillis() - startTime;
        getLogger().info("╔═══════════════════════════════════╗");
        getLogger().info("║  MINECASA activé avec succès !    ║");
        getLogger().info("║  Temps de chargement: " + loadTime + "ms      ║");
        getLogger().info("╚═══════════════════════════════════╝");
    }

    @Override
    public void onDisable() {
        getLogger().info("Désactivation de MineCasa...");

        if (gameManager != null) {
            gameManager.shutdown();
        }

        if (npcManager != null) {
            npcManager.removeAllTargets();
        }

        getLogger().info("╔═══════════════════════════════════╗");
        getLogger().info("║  MINECASA désactivé avec succès ! ║");
        getLogger().info("╚═══════════════════════════════════╝");
    }

    private boolean checkDependencies() {
        getLogger().info("✓ Utilisation du système de NPCs natif (aucune dépendance requise)");
        return true;
    }

    private void initializeManagers() {
        getLogger().info("Initialisation des managers...");

        mapManager = new MapManager(this);
        getLogger().info("✓ MapManager initialisé");

        npcManager = new NpcManager(this);
        getLogger().info("✓ NpcManager initialisé");

        gameManager = new GameManager(this);
        getLogger().info("✓ GameManager initialisé");

        getLogger().info("Tous les managers sont initialisés !");
    }

    private void registerListeners() {
        getLogger().info("Enregistrement des listeners...");

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getLogger().info("✓ PlayerConnectionListener enregistré");

        getServer().getPluginManager().registerEvents(new NpcInteractionListener(this), this);
        getLogger().info("✓ NpcInteractionListener enregistré");

        getLogger().info("Tous les listeners sont enregistrés !");
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public NpcManager getNpcManager() {
        return npcManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }
}

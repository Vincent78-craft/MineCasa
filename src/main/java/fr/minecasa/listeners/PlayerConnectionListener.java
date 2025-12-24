package fr.minecasa.listeners;

import fr.minecasa.MineCasa;
import fr.minecasa.utils.ChatUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final MineCasa plugin;

    public PlayerConnectionListener(MineCasa plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        plugin.getGameManager().addPlayer(event.getPlayer());

        plugin.getServer().broadcastMessage(
            ChatUtils.format("§a§l+ §e" + event.getPlayer().getName() + " §aa rejoint le jeu §7(" +
                plugin.getGameManager().getPlayers().size() + " joueurs)")
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        plugin.getGameManager().removePlayer(event.getPlayer());

        plugin.getServer().broadcastMessage(
            ChatUtils.format("§c§l- §e" + event.getPlayer().getName() + " §ca quitté le jeu §7(" +
                plugin.getGameManager().getPlayers().size() + " joueurs)")
        );
    }
}

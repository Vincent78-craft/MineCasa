package fr.minecasa.listeners;

import fr.minecasa.MineCasa;
import fr.minecasa.game.GameState;
import fr.minecasa.npcs.NpcTarget;
import fr.minecasa.utils.ChatUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NpcInteractionListener implements Listener {

    private final MineCasa plugin;

    public NpcInteractionListener(MineCasa plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onNpcInteract(PlayerInteractEntityEvent event) {
        if (plugin.getGameManager().getState() != GameState.IN_GAME) {
            return;
        }

        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if (!entity.hasMetadata("minecasa_target")) {
            return;
        }

        event.setCancelled(true);

        NpcTarget target = plugin.getNpcManager().getTargetByEntity(entity);

        if (target == null) {
            return;
        }

        if (target.isDelivered()) {
            player.sendMessage(ChatUtils.formatInfo("§7Cette cible a déjà été livrée."));
            return;
        }

        if (target.isFollowing()) {
            if (target.getFollowingPlayer().equals(player.getUniqueId())) {
                player.sendMessage(ChatUtils.formatWarning("§eCette cible vous suit déjà !"));
            } else {
                player.sendMessage(ChatUtils.formatError("§cCette cible suit déjà un autre joueur !"));
            }
            return;
        }

        target.startFollowing(player);
        plugin.getNpcManager().startFollowing(target, player);

        player.sendMessage(ChatUtils.formatSuccess("§a§l✓ §e" + target.getDisplayName() + " §avous suit maintenant !"));
        player.sendMessage(ChatUtils.formatInfo("§7Amenez-la à la zone de livraison !"));

        plugin.getLogger().info(player.getName() + " a commencé à escorter " + target.getDisplayName());
    }
}

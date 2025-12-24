package fr.minecasa.npcs;

import fr.minecasa.MineCasa;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class NpcManager {

    private final MineCasa plugin;
    private final Map<String, NpcTarget> targets;
    private final Map<UUID, NpcTarget> entityToTarget;

    public NpcManager(MineCasa plugin) {
        this.plugin = plugin;
        this.targets = new HashMap<>();
        this.entityToTarget = new HashMap<>();
        plugin.getLogger().info("NpcManager initialisé (système natif)");
    }

    public NpcTarget createTarget(String targetId, String displayName, Location location) {
        try {
            NpcTarget target = new NpcTarget(targetId, displayName, location);
            targets.put(targetId, target);

            Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

            villager.setCustomName("§e§l" + displayName);
            villager.setCustomNameVisible(true);
            villager.setAI(false);
            villager.setSilent(true);
            villager.setInvulnerable(true);
            villager.setCollidable(false);
            villager.setProfession(Villager.Profession.NITWIT);
            villager.setVillagerType(Villager.Type.PLAINS);
            villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, false, false));

            villager.setMetadata("minecasa_npc", new FixedMetadataValue(plugin, targetId));
            villager.setMetadata("minecasa_target", new FixedMetadataValue(plugin, true));

            target.setEntity(villager);
            entityToTarget.put(villager.getUniqueId(), target);

            plugin.getLogger().info("NPC créé: " + displayName + " à " + location);
            return target;
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors de la création du NPC " + targetId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void removeTarget(String targetId) {
        try {
            NpcTarget target = targets.get(targetId);
            if (target != null && target.getEntity() != null) {
                Entity entity = target.getEntity();
                entityToTarget.remove(entity.getUniqueId());
                entity.remove();
                targets.remove(targetId);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors de la suppression du NPC " + targetId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeAllTargets() {
        for (NpcTarget target : new ArrayList<>(targets.values())) {
            removeTarget(target.getTargetId());
        }
        entityToTarget.clear();
        targets.clear();
    }

    public NpcTarget getTarget(String targetId) {
        return targets.get(targetId);
    }

    public NpcTarget getTargetByEntity(Entity entity) {
        return entityToTarget.get(entity.getUniqueId());
    }

    public Collection<NpcTarget> getAllTargets() {
        return targets.values();
    }

    public void startFollowing(NpcTarget target, Player player) {
        target.startFollowing(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!target.isFollowing() || target.isDelivered()) {
                    this.cancel();
                    return;
                }

                Player followPlayer = Bukkit.getPlayer(target.getFollowingPlayer());
                if (followPlayer == null || !followPlayer.isOnline()) {
                    target.stopFollowing();
                    this.cancel();
                    return;
                }

                Entity entity = target.getEntity();
                if (entity == null || !entity.isValid()) {
                    target.stopFollowing();
                    this.cancel();
                    return;
                }

                Location npcLoc = entity.getLocation();
                Location playerLoc = followPlayer.getLocation();
                double distance = npcLoc.distance(playerLoc);

                if (distance > 2.0 && distance < 30.0) {
                    Location targetLoc = playerLoc.clone();
                    double speed = 0.25;

                    double dx = (targetLoc.getX() - npcLoc.getX()) * speed;
                    double dy = (targetLoc.getY() - npcLoc.getY()) * speed;
                    double dz = (targetLoc.getZ() - npcLoc.getZ()) * speed;

                    Location newLoc = npcLoc.clone().add(dx, dy, dz);
                    newLoc.setYaw(npcLoc.getYaw());
                    newLoc.setPitch(npcLoc.getPitch());

                    entity.teleport(newLoc);
                } else if (distance >= 30.0) {
                    Location newLoc = playerLoc.clone().subtract(2, 0, 0);
                    entity.teleport(newLoc);
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
}

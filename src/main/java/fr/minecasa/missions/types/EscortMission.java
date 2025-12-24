package fr.minecasa.missions.types;

import fr.minecasa.MineCasa;
import fr.minecasa.missions.Mission;
import fr.minecasa.missions.MissionProgress;
import fr.minecasa.npcs.NpcManager;
import fr.minecasa.npcs.NpcTarget;
import fr.minecasa.map.Zone;
import fr.minecasa.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class EscortMission implements Mission {

    private final MineCasa plugin;
    private final String id;
    private final String name;
    private final String description;
    private final NpcManager npcManager;
    private final Zone deliveryZone;
    private final MissionProgress progress;
    private final List<NpcTarget> targets;
    private boolean active;
    private BukkitRunnable checkTask;

    public EscortMission(MineCasa plugin, NpcManager npcManager, Zone deliveryZone) {
        this.plugin = plugin;
        this.id = "escort_mission_1";
        this.name = "Mission d'Escorte";
        this.description = "Escorter 3 cibles vers la zone de livraison";
        this.npcManager = npcManager;
        this.deliveryZone = deliveryZone;
        this.progress = new MissionProgress(id, 3);
        this.targets = new ArrayList<>();
        this.active = false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void start() {
        active = true;
        progress.setCurrent(0);
        createTargets();
        startZoneChecker();

        Bukkit.broadcastMessage(ChatUtils.formatInfo("§e§lMission: §f" + name));
        Bukkit.broadcastMessage(ChatUtils.formatInfo("§7" + description));
        Bukkit.broadcastMessage(ChatUtils.format("§aAllez récupérer les §e§l3 cibles §aet amenez-les à la zone !"));
    }

    @Override
    public void stop() {
        active = false;
        if (checkTask != null) {
            checkTask.cancel();
        }
        for (NpcTarget target : targets) {
            npcManager.removeTarget(target.getTargetId());
        }
        targets.clear();
    }

    @Override
    public boolean isCompleted() {
        return progress.isCompleted();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void checkProgress(Player player) {
        // Progress is checked automatically in the zone checker
    }

    @Override
    public int getProgressPercentage() {
        return progress.getPercentage();
    }

    @Override
    public String getProgressString() {
        return progress.getProgressString();
    }

    private void createTargets() {
        Location spawn1 = deliveryZone.getCenter().clone().add(20, 0, 20);
        Location spawn2 = deliveryZone.getCenter().clone().add(-20, 0, 20);
        Location spawn3 = deliveryZone.getCenter().clone().add(0, 0, -20);

        NpcTarget target1 = npcManager.createTarget("cible_1", "Cible 1", spawn1);
        NpcTarget target2 = npcManager.createTarget("cible_2", "Cible 2", spawn2);
        NpcTarget target3 = npcManager.createTarget("cible_3", "Cible 3", spawn3);

        if (target1 != null) targets.add(target1);
        if (target2 != null) targets.add(target2);
        if (target3 != null) targets.add(target3);
    }

    private void startZoneChecker() {
        checkTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!active) {
                    this.cancel();
                    return;
                }

                for (NpcTarget target : targets) {
                    if (target.isFollowing() && !target.isDelivered()) {
                        Entity entity = target.getEntity();
                        if (entity != null && entity.isValid()) {
                            Location npcLoc = entity.getLocation();
                            if (deliveryZone.isInZone(npcLoc)) {
                                deliverTarget(target);
                            }
                        }
                    }
                }

                if (progress.isCompleted() && active) {
                    completeMission();
                }
            }
        };
        checkTask.runTaskTimer(plugin, 0L, 10L);
    }

    private void deliverTarget(NpcTarget target) {
        target.setDelivered(true);
        target.stopFollowing();
        progress.increment();

        Player player = Bukkit.getPlayer(target.getFollowingPlayer());
        if (player != null) {
            player.sendMessage(ChatUtils.formatSuccess("§a§l✓ §aCible livrée ! §7(" + progress.getProgressString() + ")"));
        }

        Bukkit.broadcastMessage(ChatUtils.format("§e" + target.getDisplayName() + " §aa été livrée ! §7(" + progress.getProgressString() + ")"));

        npcManager.removeTarget(target.getTargetId());
    }

    private void completeMission() {
        active = false;
        if (checkTask != null) {
            checkTask.cancel();
        }

        ChatUtils.sendMissionComplete(name);
        plugin.getGameManager().onMissionComplete();
    }

    public List<NpcTarget> getTargets() {
        return targets;
    }

    public MissionProgress getProgress() {
        return progress;
    }
}

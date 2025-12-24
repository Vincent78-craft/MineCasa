package fr.minecasa.npcs;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NpcTarget {

    private final String targetId;
    private final String displayName;
    private Entity entity;
    private final Location spawnLocation;
    private UUID followingPlayer;
    private boolean isFollowing;
    private boolean isDelivered;

    public NpcTarget(String targetId, String displayName, Location spawnLocation) {
        this.targetId = targetId;
        this.displayName = displayName;
        this.spawnLocation = spawnLocation;
        this.isFollowing = false;
        this.isDelivered = false;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public UUID getFollowingPlayer() {
        return followingPlayer;
    }

    public void setFollowingPlayer(UUID followingPlayer) {
        this.followingPlayer = followingPlayer;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public void startFollowing(Player player) {
        this.followingPlayer = player.getUniqueId();
        this.isFollowing = true;
    }

    public void stopFollowing() {
        this.followingPlayer = null;
        this.isFollowing = false;
    }
}

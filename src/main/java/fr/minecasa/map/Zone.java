package fr.minecasa.map;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Zone {

    private final String id;
    private final String name;
    private final Location center;
    private final double radius;
    private final ZoneType type;

    public Zone(String id, String name, Location center, double radius, ZoneType type) {
        this.id = id;
        this.name = name;
        this.center = center;
        this.radius = radius;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public ZoneType getType() {
        return type;
    }

    public boolean isInZone(Location location) {
        if (!location.getWorld().equals(center.getWorld())) {
            return false;
        }

        double distance = location.distance(center);
        return distance <= radius;
    }

    public double getDistanceToZone(Location location) {
        if (!location.getWorld().equals(center.getWorld())) {
            return Double.MAX_VALUE;
        }

        double distance = location.distance(center);
        return Math.max(0, distance - radius);
    }

    public enum ZoneType {
        DELIVERY,
        SPAWN,
        SAFE,
        DANGER
    }
}

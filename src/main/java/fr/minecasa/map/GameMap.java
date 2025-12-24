package fr.minecasa.map;

import org.bukkit.Location;

import java.util.*;

public class GameMap {

    private final String mapId;
    private final String name;
    private final Location lobbySpawn;
    private final Location gameSpawn;
    private final Map<String, Zone> zones;

    public GameMap(String mapId, String name, Location lobbySpawn, Location gameSpawn) {
        this.mapId = mapId;
        this.name = name;
        this.lobbySpawn = lobbySpawn;
        this.gameSpawn = gameSpawn;
        this.zones = new HashMap<>();
    }

    public String getMapId() {
        return mapId;
    }

    public String getName() {
        return name;
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public Location getGameSpawn() {
        return gameSpawn;
    }

    public void addZone(Zone zone) {
        zones.put(zone.getId(), zone);
    }

    public void removeZone(String zoneId) {
        zones.remove(zoneId);
    }

    public Zone getZone(String zoneId) {
        return zones.get(zoneId);
    }

    public Collection<Zone> getAllZones() {
        return zones.values();
    }

    public Optional<Zone> getZoneAtLocation(Location location) {
        return zones.values().stream()
                .filter(zone -> zone.isInZone(location))
                .findFirst();
    }

    public List<Zone> getZonesByType(Zone.ZoneType type) {
        return zones.values().stream()
                .filter(zone -> zone.getType() == type)
                .toList();
    }
}

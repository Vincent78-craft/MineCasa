package fr.minecasa.map;

import fr.minecasa.MineCasa;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class MapManager {

    private final MineCasa plugin;
    private GameMap currentMap;

    public MapManager(MineCasa plugin) {
        this.plugin = plugin;
        loadDefaultMap();
    }

    private void loadDefaultMap() {
        World world = Bukkit.getWorlds().get(0);
        Location lobbySpawn = new Location(world, 0, 65, 0);
        Location gameSpawn = new Location(world, 100, 65, 100);

        currentMap = new GameMap("default", "Map Par Défaut", lobbySpawn, gameSpawn);

        Zone deliveryZone = new Zone(
            "delivery",
            "Zone de Livraison",
            new Location(world, 150, 65, 150),
            5.0,
            Zone.ZoneType.DELIVERY
        );
        currentMap.addZone(deliveryZone);

        plugin.getLogger().info("Map par défaut chargée: " + currentMap.getName());
    }

    public void createDeliveryZoneMarker() {
        Zone deliveryZone = getDeliveryZone();
        if (deliveryZone == null) {
            plugin.getLogger().warning("Zone de livraison introuvable !");
            return;
        }

        Location center = deliveryZone.getCenter();
        double radius = deliveryZone.getRadius();
        World world = center.getWorld();

        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        int radiusInt = (int) Math.ceil(radius);

        for (int x = -radiusInt; x <= radiusInt; x++) {
            for (int z = -radiusInt; z <= radiusInt; z++) {
                double distance = Math.sqrt(x * x + z * z);

                if (distance >= radius - 0.5 && distance <= radius + 0.5) {
                    Location blockLoc = new Location(world, centerX + x, centerY, centerZ + z);
                    blockLoc.getBlock().setType(Material.WHITE_WOOL);
                }
            }
        }

        plugin.getLogger().info("Marqueur de zone de livraison créé (cercle de laine blanche)");
    }

    public void removeDeliveryZoneMarker() {
        Zone deliveryZone = getDeliveryZone();
        if (deliveryZone == null) {
            return;
        }

        Location center = deliveryZone.getCenter();
        double radius = deliveryZone.getRadius();
        World world = center.getWorld();

        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        int radiusInt = (int) Math.ceil(radius);

        for (int x = -radiusInt; x <= radiusInt; x++) {
            for (int z = -radiusInt; z <= radiusInt; z++) {
                double distance = Math.sqrt(x * x + z * z);

                if (distance >= radius - 0.5 && distance <= radius + 0.5) {
                    Location blockLoc = new Location(world, centerX + x, centerY, centerZ + z);
                    if (blockLoc.getBlock().getType() == Material.WHITE_WOOL) {
                        blockLoc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }

        plugin.getLogger().info("Marqueur de zone de livraison supprimé");
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(GameMap map) {
        this.currentMap = map;
    }

    public Zone getDeliveryZone() {
        return currentMap.getZone("delivery");
    }

    public Location getLobbySpawn() {
        return currentMap.getLobbySpawn();
    }

    public Location getGameSpawn() {
        return currentMap.getGameSpawn();
    }
}

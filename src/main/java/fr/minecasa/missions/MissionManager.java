package fr.minecasa.missions;

import fr.minecasa.MineCasa;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MissionManager {

    private final MineCasa plugin;
    private final List<Mission> missions;
    private Mission currentMission;
    private int currentMissionIndex;

    public MissionManager(MineCasa plugin) {
        this.plugin = plugin;
        this.missions = new ArrayList<>();
        this.currentMissionIndex = -1;
    }

    public void registerMission(Mission mission) {
        missions.add(mission);
        plugin.getLogger().info("Mission enregistrée: " + mission.getName());
    }

    public void startNextMission() {
        if (currentMission != null && currentMission.isActive()) {
            currentMission.stop();
        }

        currentMissionIndex++;
        if (currentMissionIndex < missions.size()) {
            currentMission = missions.get(currentMissionIndex);
            currentMission.start();
            plugin.getLogger().info("Mission démarrée: " + currentMission.getName());
        } else {
            plugin.getLogger().info("Toutes les missions sont terminées !");
        }
    }

    public void stopCurrentMission() {
        if (currentMission != null) {
            currentMission.stop();
        }
    }

    public Mission getCurrentMission() {
        return currentMission;
    }

    public List<Mission> getAllMissions() {
        return new ArrayList<>(missions);
    }

    public Optional<Mission> getMissionById(String id) {
        return missions.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    public boolean hasNextMission() {
        return currentMissionIndex + 1 < missions.size();
    }

    public void checkProgress(Player player) {
        if (currentMission != null && currentMission.isActive()) {
            currentMission.checkProgress(player);
        }
    }

    public void reset() {
        if (currentMission != null && currentMission.isActive()) {
            currentMission.stop();
        }
        currentMissionIndex = -1;
        currentMission = null;
    }

    public int getTotalMissions() {
        return missions.size();
    }

    public int getCurrentMissionNumber() {
        return currentMissionIndex + 1;
    }
}

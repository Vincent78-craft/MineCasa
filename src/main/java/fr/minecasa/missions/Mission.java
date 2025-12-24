package fr.minecasa.missions;

import org.bukkit.entity.Player;

public interface Mission {

    String getId();

    String getName();

    String getDescription();

    void start();

    void stop();

    boolean isCompleted();

    boolean isActive();

    void checkProgress(Player player);

    int getProgressPercentage();

    String getProgressString();
}

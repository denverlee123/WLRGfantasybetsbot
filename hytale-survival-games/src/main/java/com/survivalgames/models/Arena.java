package com.survivalgames.models;

import com.hytale.api.world.Location;
import java.util.ArrayList;
import java.util.List;

public class Arena {
    private final String name;
    private Location lobbySpawn;
    private final List<Location> spawnPoints;
    private Location corner1;
    private Location corner2;
    private int borderSize;

    public Arena(String name) {
        this.name = name;
        this.spawnPoints = new ArrayList<>();
        this.borderSize = 500;
    }

    public String getName() {
        return name;
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public void setLobbySpawn(Location lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
    }

    public List<Location> getSpawnPoints() {
        return spawnPoints;
    }

    public void addSpawnPoint(Location location) {
        spawnPoints.add(location);
    }

    public void clearSpawnPoints() {
        spawnPoints.clear();
    }

    public Location getCorner1() {
        return corner1;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public boolean isSetup() {
        return lobbySpawn != null && !spawnPoints.isEmpty() && corner1 != null && corner2 != null;
    }
}

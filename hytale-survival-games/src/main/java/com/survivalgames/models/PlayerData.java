package com.survivalgames.models;

import com.hytale.api.entity.Player;

public class PlayerData {
    private final Player player;
    private Kit selectedKit;
    private boolean isAlive;
    private boolean isSpectator;
    private int kills;
    private int deaths;
    private int wins;
    private int gamesPlayed;

    public PlayerData(Player player) {
        this.player = player;
        this.isAlive = true;
        this.isSpectator = false;
        this.kills = 0;
        this.deaths = 0;
        this.wins = 0;
        this.gamesPlayed = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public Kit getSelectedKit() {
        return selectedKit;
    }

    public void setSelectedKit(Kit kit) {
        this.selectedKit = kit;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        this.kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void addDeath() {
        this.deaths++;
    }

    public int getWins() {
        return wins;
    }

    public void addWin() {
        this.wins++;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public void resetGameStats() {
        this.isAlive = true;
        this.isSpectator = false;
    }
}

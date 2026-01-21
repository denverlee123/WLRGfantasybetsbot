package com.survivalgames.models;

public enum GameState {
    WAITING,      // Waiting for players to join
    STARTING,     // Countdown before game starts
    GRACE_PERIOD, // Game started but no PvP yet
    ACTIVE,       // Active game with PvP enabled
    ENDING        // Game is ending
}

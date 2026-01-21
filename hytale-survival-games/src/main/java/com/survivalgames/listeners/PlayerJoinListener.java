package com.survivalgames.listeners;

import com.hytale.api.event.EventListener;
import com.hytale.api.event.player.PlayerJoinEvent;
import com.survivalgames.SurvivalGamesPlugin;

public class PlayerJoinListener implements EventListener<PlayerJoinEvent> {

    private final SurvivalGamesPlugin plugin;

    public PlayerJoinListener(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEvent(PlayerJoinEvent event) {
        // Initialize player data if needed
        plugin.getGameManager().getPlayerData(event.getPlayer());
    }
}

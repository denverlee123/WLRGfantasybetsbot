package com.survivalgames.listeners;

import com.hytale.api.event.EventListener;
import com.hytale.api.event.player.PlayerQuitEvent;
import com.survivalgames.SurvivalGamesPlugin;

public class PlayerQuitListener implements EventListener<PlayerQuitEvent> {

    private final SurvivalGamesPlugin plugin;

    public PlayerQuitListener(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEvent(PlayerQuitEvent event) {
        // Remove player from game if they're in one
        plugin.getGameManager().handlePlayerQuit(event.getPlayer());
    }
}

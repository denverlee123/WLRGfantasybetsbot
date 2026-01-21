package com.survivalgames.listeners;

import com.hytale.api.entity.Player;
import com.hytale.api.event.EventListener;
import com.hytale.api.event.player.PlayerInteractEvent;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.game.Game;
import com.survivalgames.models.GameState;

public class PlayerInteractListener implements EventListener<PlayerInteractEvent> {

    private final SurvivalGamesPlugin plugin;

    public PlayerInteractListener(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check if player is in a game
        if (!plugin.getGameManager().isPlayerInGame(player)) {
            return;
        }

        String arenaName = plugin.getGameManager().getPlayerArena(player);
        Game game = plugin.getGameManager().getGame(arenaName);

        if (game == null) {
            return;
        }

        // Prevent interactions during waiting/starting phase
        if (game.getState() == GameState.WAITING || game.getState() == GameState.STARTING) {
            event.setCancelled(true);
        }

        // Prevent spectators from interacting
        if (!game.isPlayerAlive(player)) {
            event.setCancelled(true);
        }
    }
}

package com.survivalgames.listeners;

import com.hytale.api.entity.Player;
import com.hytale.api.event.EventListener;
import com.hytale.api.event.entity.PlayerDeathEvent;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.game.Game;

public class PlayerDeathListener implements EventListener<PlayerDeathEvent> {

    private final SurvivalGamesPlugin plugin;

    public PlayerDeathListener(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEvent(PlayerDeathEvent event) {
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

        // Get killer if available
        Player killer = event.getKiller();

        // Handle death in game
        game.handlePlayerDeath(player, killer);

        // Prevent death (keep player as spectator)
        event.setCancelled(true);
    }
}

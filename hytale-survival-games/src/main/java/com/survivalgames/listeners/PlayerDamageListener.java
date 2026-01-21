package com.survivalgames.listeners;

import com.hytale.api.entity.Player;
import com.hytale.api.event.EventListener;
import com.hytale.api.event.entity.EntityDamageByEntityEvent;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.game.Game;
import com.survivalgames.models.GameState;

public class PlayerDamageListener implements EventListener<EntityDamageByEntityEvent> {

    private final SurvivalGamesPlugin plugin;

    public PlayerDamageListener(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEvent(EntityDamageByEntityEvent event) {
        // Check if both entities are players
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        // Check if players are in a game
        if (!plugin.getGameManager().isPlayerInGame(victim) ||
            !plugin.getGameManager().isPlayerInGame(attacker)) {
            return;
        }

        String arenaName = plugin.getGameManager().getPlayerArena(victim);
        Game game = plugin.getGameManager().getGame(arenaName);

        if (game == null) {
            return;
        }

        // Prevent PvP during grace period
        if (game.getState() == GameState.GRACE_PERIOD || game.getState() == GameState.WAITING || game.getState() == GameState.STARTING) {
            event.setCancelled(true);
            attacker.sendMessage("§cPvP is not enabled yet!");
            return;
        }

        // Prevent spectators from dealing damage
        if (!game.isPlayerAlive(attacker)) {
            event.setCancelled(true);
            return;
        }

        // Prevent damage to spectators
        if (!game.isPlayerAlive(victim)) {
            event.setCancelled(true);
        }
    }
}

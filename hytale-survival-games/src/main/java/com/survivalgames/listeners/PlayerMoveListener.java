package com.survivalgames.listeners;

import com.hytale.api.entity.Player;
import com.hytale.api.event.EventListener;
import com.hytale.api.event.player.PlayerMoveEvent;
import com.hytale.api.world.Location;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.game.Game;
import com.survivalgames.models.Arena;
import com.survivalgames.models.GameState;

public class PlayerMoveListener implements EventListener<PlayerMoveEvent> {

    private final SurvivalGamesPlugin plugin;

    public PlayerMoveListener(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if player is in a game
        if (!plugin.getGameManager().isPlayerInGame(player)) {
            return;
        }

        String arenaName = plugin.getGameManager().getPlayerArena(player);
        Game game = plugin.getGameManager().getGame(arenaName);

        if (game == null || game.getState() == GameState.WAITING || game.getState() == GameState.STARTING) {
            return;
        }

        Arena arena = game.getArena();
        Location to = event.getTo();

        // Check if player is outside border
        if (arena.getCorner1() != null && arena.getCorner2() != null) {
            double minX = Math.min(arena.getCorner1().getX(), arena.getCorner2().getX());
            double maxX = Math.max(arena.getCorner1().getX(), arena.getCorner2().getX());
            double minZ = Math.min(arena.getCorner1().getZ(), arena.getCorner2().getZ());
            double maxZ = Math.max(arena.getCorner1().getZ(), arena.getCorner2().getZ());

            int borderSize = game.getCurrentBorderSize();
            double centerX = (minX + maxX) / 2;
            double centerZ = (minZ + maxZ) / 2;

            double halfBorder = borderSize / 2.0;

            if (to.getX() < centerX - halfBorder || to.getX() > centerX + halfBorder ||
                to.getZ() < centerZ - halfBorder || to.getZ() > centerZ + halfBorder) {

                // Damage player for being outside border
                player.damage(2.0);
                player.sendMessage("§c§lYou are outside the border! Get back inside!");
            }
        }
    }
}

package com.survivalgames.game;

import com.hytale.api.entity.Player;
import com.hytale.api.scheduler.Task;
import com.hytale.api.world.Location;
import com.hytale.api.world.World;
import com.hytale.api.entity.component.Teleport;
import com.hytale.api.entity.component.TransformComponent;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.config.ConfigManager;
import com.survivalgames.models.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Game {

    private final SurvivalGamesPlugin plugin;
    private final Arena arena;
    private final Set<Player> players;
    private final Set<Player> alivePlayers;
    private final Set<Player> spectators;
    private GameState state;
    private Task countdownTask;
    private Task gameTask;
    private int countdown;
    private int gameTime;
    private int currentBorderSize;

    public Game(SurvivalGamesPlugin plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.players = ConcurrentHashMap.newKeySet();
        this.alivePlayers = ConcurrentHashMap.newKeySet();
        this.spectators = ConcurrentHashMap.newKeySet();
        this.state = GameState.WAITING;
        this.gameTime = 0;
        this.currentBorderSize = arena.getBorderSize();
    }

    public boolean addPlayer(Player player) {
        ConfigManager config = plugin.getConfigManager();

        if (state != GameState.WAITING && state != GameState.STARTING) {
            player.sendMessage("§cThis game has already started!");
            return false;
        }

        if (players.size() >= config.getMaxPlayers()) {
            player.sendMessage("§cThis game is full!");
            return false;
        }

        players.add(player);
        alivePlayers.add(player);

        // Teleport to lobby
        teleportPlayer(player, arena.getLobbySpawn());

        // Clear inventory
        clearPlayerInventory(player);

        // Broadcast join message
        broadcast("§e" + player.getName() + " §7has joined the game! §8[§a" + players.size() + "§8/§a" + config.getMaxPlayers() + "§8]");

        // Check if we should start countdown
        if (players.size() >= config.getMinPlayers() && state == GameState.WAITING) {
            startCountdown();
        }

        return true;
    }

    public void removePlayer(Player player) {
        boolean wasAlive = alivePlayers.contains(player);

        players.remove(player);
        alivePlayers.remove(player);
        spectators.remove(player);

        // Teleport to lobby spawn
        if (arena.getLobbySpawn() != null) {
            teleportPlayer(player, arena.getLobbySpawn());
        }

        // Clear inventory and restore
        clearPlayerInventory(player);
        player.sendMessage("§aYou have left the game!");

        broadcast("§e" + player.getName() + " §7has left the game!");

        // Check if game should end
        if (wasAlive && state == GameState.ACTIVE || state == GameState.GRACE_PERIOD) {
            checkWinCondition();
        }

        // Cancel countdown if not enough players
        if (players.size() < plugin.getConfigManager().getMinPlayers() && state == GameState.STARTING) {
            cancelCountdown();
        }
    }

    private void startCountdown() {
        state = GameState.STARTING;
        countdown = plugin.getConfigManager().getCountdownSeconds();

        broadcast("§aGame starting in §e" + countdown + " §aseconds!");
        broadcast("§7Select your kit with §e/kit§7!");

        countdownTask = plugin.getScheduler().runRepeating(() -> {
            countdown--;

            if (countdown <= 0) {
                startGame();
            } else if (countdown <= 5 || countdown % 10 == 0) {
                broadcast("§eGame starting in §c" + countdown + " §eseconds!");
            }
        }, 20L, 20L); // Run every second (20 ticks)
    }

    private void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        state = GameState.WAITING;
        broadcast("§cCountdown cancelled! Not enough players.");
    }

    public void forceStart() {
        if (players.isEmpty()) {
            return;
        }

        if (countdownTask != null) {
            countdownTask.cancel();
        }

        startGame();
    }

    private void startGame() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }

        state = GameState.GRACE_PERIOD;
        gameTime = 0;

        broadcast("§a§l========================================");
        broadcast("§e§lSURVIVAL GAMES");
        broadcast("");
        broadcast("§7Grace Period: §e" + plugin.getConfigManager().getGracePeriodSeconds() + " seconds");
        broadcast("§7Players: §e" + alivePlayers.size());
        broadcast("§a§l========================================");

        // Give players their kits
        for (Player player : players) {
            PlayerData data = plugin.getGameManager().getPlayerData(player);
            Kit kit = data.getSelectedKit();
            if (kit == null) {
                // Give default kit
                kit = plugin.getConfigManager().getAllKits().iterator().next();
            }
            giveKit(player, kit);
        }

        // Teleport players to spawn points
        List<Location> spawnPoints = new ArrayList<>(arena.getSpawnPoints());
        Collections.shuffle(spawnPoints);

        int i = 0;
        for (Player player : players) {
            Location spawn = spawnPoints.get(i % spawnPoints.size());
            teleportPlayer(player, spawn);
            i++;
        }

        // Start game timer
        gameTask = plugin.getScheduler().runRepeating(() -> {
            gameTime++;

            // Grace period end
            if (gameTime == plugin.getConfigManager().getGracePeriodSeconds()) {
                state = GameState.ACTIVE;
                broadcast("§c§lGrace period ended! PvP is now enabled!");
            }

            // Border shrink
            if (gameTime >= plugin.getConfigManager().getBorderShrinkStart()) {
                if (gameTime % 30 == 0) { // Shrink every 30 seconds
                    shrinkBorder();
                }
            }

            // Check win condition every 5 seconds
            if (gameTime % 5 == 0) {
                checkWinCondition();
            }
        }, 20L, 20L);
    }

    public void stopGame() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }

        if (gameTask != null) {
            gameTask.cancel();
            gameTask = null;
        }

        // Teleport all players to lobby
        for (Player player : players) {
            if (arena.getLobbySpawn() != null) {
                teleportPlayer(player, arena.getLobbySpawn());
            }
            clearPlayerInventory(player);
            plugin.getGameManager().setPlayerArena(player, null);
        }

        players.clear();
        alivePlayers.clear();
        spectators.clear();
        state = GameState.WAITING;
        gameTime = 0;
        currentBorderSize = arena.getBorderSize();
    }

    private void shrinkBorder() {
        int shrinkAmount = (int) (plugin.getConfigManager().getBorderShrinkSpeed() * 10);
        currentBorderSize = Math.max(50, currentBorderSize - shrinkAmount);
        broadcast("§c§lThe border is shrinking! New size: §e" + currentBorderSize + " blocks");
    }

    public void handlePlayerDeath(Player player, Player killer) {
        if (!alivePlayers.contains(player)) {
            return;
        }

        alivePlayers.remove(player);
        spectators.add(player);

        PlayerData data = plugin.getGameManager().getPlayerData(player);
        data.addDeath();
        data.setAlive(false);
        data.setSpectator(true);

        if (killer != null) {
            PlayerData killerData = plugin.getGameManager().getPlayerData(killer);
            killerData.addKill();
            broadcast("§c" + player.getName() + " §7was killed by §c" + killer.getName() + " §8[§e" + alivePlayers.size() + " §7remaining§8]");
        } else {
            broadcast("§c" + player.getName() + " §7died! §8[§e" + alivePlayers.size() + " §7remaining§8]");
        }

        // Make player spectator
        player.sendMessage("§cYou have been eliminated! You are now spectating.");

        checkWinCondition();
    }

    private void checkWinCondition() {
        if (alivePlayers.size() == 1) {
            Player winner = alivePlayers.iterator().next();
            endGame(winner);
        } else if (alivePlayers.isEmpty()) {
            endGame(null);
        }
    }

    private void endGame(Player winner) {
        state = GameState.ENDING;

        if (gameTask != null) {
            gameTask.cancel();
            gameTask = null;
        }

        broadcast("§a§l========================================");
        if (winner != null) {
            broadcast("§6§lGAME OVER!");
            broadcast("");
            broadcast("§eWinner: §a" + winner.getName());
            PlayerData winnerData = plugin.getGameManager().getPlayerData(winner);
            winnerData.addWin();
            broadcast("§7Kills: §e" + winnerData.getKills());
            broadcast("");
        } else {
            broadcast("§6§lGAME OVER!");
            broadcast("§cNo winner!");
            broadcast("");
        }
        broadcast("§a§l========================================");

        // Schedule game cleanup
        plugin.getScheduler().runDelayed(() -> {
            stopGame();
        }, 100L); // 5 seconds
    }

    private void teleportPlayer(Player player, Location location) {
        // Use Hytale's proper teleportation API
        World world = location.getWorld();
        world.runOnWorldThread(() -> {
            TransformComponent transform = player.getComponent(TransformComponent.class);
            if (transform != null) {
                Teleport teleport = new Teleport(
                    location.toVector3f(),
                    transform.getRotation()
                );
                player.addComponent(Teleport.class, teleport);
            }
        });
    }

    private void clearPlayerInventory(Player player) {
        // Clear player inventory using Hytale API
        player.getInventory().clear();
    }

    private void giveKit(Player player, Kit kit) {
        clearPlayerInventory(player);

        for (String itemString : kit.getItems()) {
            String[] parts = itemString.split(":");
            String itemId = parts[0] + ":" + parts[1];
            int amount = parts.length > 2 ? Integer.parseInt(parts[2]) : 1;

            // Give item to player using Hytale's item system
            player.getInventory().addItem(itemId, amount);
        }

        player.sendMessage("§aYou received the §e" + kit.getName() + " §akit!");
    }

    private void broadcast(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public GameState getState() {
        return state;
    }

    public Arena getArena() {
        return arena;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public Set<Player> getAlivePlayers() {
        return alivePlayers;
    }

    public boolean isPlayerAlive(Player player) {
        return alivePlayers.contains(player);
    }

    public int getCurrentBorderSize() {
        return currentBorderSize;
    }
}

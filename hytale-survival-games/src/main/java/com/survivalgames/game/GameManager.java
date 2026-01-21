package com.survivalgames.game;

import com.hytale.api.entity.Player;
import com.hytale.api.scheduler.Scheduler;
import com.hytale.api.world.Location;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.models.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {

    private final SurvivalGamesPlugin plugin;
    private final Map<String, Arena> arenas;
    private final Map<String, Game> activeGames;
    private final Map<UUID, PlayerData> playerData;
    private final Map<UUID, String> playerArenas;

    public GameManager(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
        this.arenas = new ConcurrentHashMap<>();
        this.activeGames = new ConcurrentHashMap<>();
        this.playerData = new ConcurrentHashMap<>();
        this.playerArenas = new ConcurrentHashMap<>();
    }

    public Arena createArena(String name) {
        Arena arena = new Arena(name);
        arenas.put(name, arena);
        return arena;
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public Collection<Arena> getAllArenas() {
        return arenas.values();
    }

    public void deleteArena(String name) {
        // Stop game if running
        if (activeGames.containsKey(name)) {
            activeGames.get(name).stopGame();
            activeGames.remove(name);
        }
        arenas.remove(name);
    }

    public Game getOrCreateGame(Arena arena) {
        return activeGames.computeIfAbsent(arena.getName(), k -> new Game(plugin, arena));
    }

    public Game getGame(String arenaName) {
        return activeGames.get(arenaName);
    }

    public void stopAllGames() {
        for (Game game : activeGames.values()) {
            game.stopGame();
        }
        activeGames.clear();
    }

    public PlayerData getPlayerData(Player player) {
        return playerData.computeIfAbsent(player.getUniqueId(), k -> new PlayerData(player));
    }

    public boolean isPlayerInGame(Player player) {
        return playerArenas.containsKey(player.getUniqueId());
    }

    public String getPlayerArena(Player player) {
        return playerArenas.get(player.getUniqueId());
    }

    public void setPlayerArena(Player player, String arenaName) {
        if (arenaName == null) {
            playerArenas.remove(player.getUniqueId());
        } else {
            playerArenas.put(player.getUniqueId(), arenaName);
        }
    }

    public boolean joinGame(Player player, Arena arena) {
        if (isPlayerInGame(player)) {
            player.sendMessage("§cYou are already in a game!");
            return false;
        }

        if (!arena.isSetup()) {
            player.sendMessage("§cThis arena is not properly set up!");
            return false;
        }

        Game game = getOrCreateGame(arena);
        if (game.addPlayer(player)) {
            setPlayerArena(player, arena.getName());
            return true;
        }

        return false;
    }

    public boolean leaveGame(Player player) {
        String arenaName = getPlayerArena(player);
        if (arenaName == null) {
            player.sendMessage("§cYou are not in a game!");
            return false;
        }

        Game game = getGame(arenaName);
        if (game != null) {
            game.removePlayer(player);
            setPlayerArena(player, null);
            return true;
        }

        return false;
    }

    public void handlePlayerQuit(Player player) {
        if (isPlayerInGame(player)) {
            leaveGame(player);
        }
    }
}

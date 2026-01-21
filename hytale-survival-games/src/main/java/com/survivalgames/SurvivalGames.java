package com.survivalgames;

import com.google.gson.*;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hytale Survival Games Plugin
 *
 * NOTE: This is a minimal version that works with the current Hytale Early Access server.
 * The command system (Command/CommandSender classes) is not available in this version,
 * so commands will need to be added once we discover how commands actually work in your server.
 */
public class SurvivalGames extends JavaPlugin {

    private static SurvivalGames instance;
    private final Map<UUID, String> playerKits = new ConcurrentHashMap<>();
    private final Map<UUID, GamePlayer> gamePlayers = new ConcurrentHashMap<>();
    private GameConfig config;

    public SurvivalGames(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    public void setup() {
        log("Setting up Survival Games plugin...");

        // Load configuration
        loadConfig();

        log("Survival Games setup complete!");
    }

    @Override
    public void start() {
        log("Starting Survival Games plugin...");

        // Register event listeners
        registerEvents();

        log("Survival Games plugin started successfully!");
        log("Loaded " + config.kits.size() + " kits");
        log("Commands are not available in this Hytale version");
        log("Waiting for command system to be discovered...");
    }

    @Override
    public void shutdown() {
        log("Shutting down Survival Games plugin...");

        // Save any pending data
        saveConfig();

        log("Survival Games plugin shut down successfully!");
    }

    private void log(String message) {
        System.out.println("[SurvivalGames] " + message);
    }

    private void logError(String message) {
        System.err.println("[SurvivalGames] ERROR: " + message);
    }

    private void loadConfig() {
        Path configPath = getDataDirectory().resolve("config.json");

        try {
            if (!Files.exists(getDataDirectory())) {
                Files.createDirectories(getDataDirectory());
            }

            if (!Files.exists(configPath)) {
                // Create default config
                config = GameConfig.createDefault();
                saveConfig();
                log("Created default configuration");
            } else {
                // Load existing config
                String json = Files.readString(configPath);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                config = gson.fromJson(json, GameConfig.class);
                log("Loaded configuration from " + configPath);
            }
        } catch (IOException e) {
            logError("Failed to load config: " + e.getMessage());
            config = GameConfig.createDefault();
        }
    }

    private void saveConfig() {
        if (config == null) return;

        Path configPath = getDataDirectory().resolve("config.json");
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(config);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            logError("Failed to save config: " + e.getMessage());
        }
    }

    private void registerEvents() {
        // Player connect event
        getEventRegistry().register(PlayerConnectEvent.class, event -> {
            PlayerRef player = event.getPlayerRef();
            log("Player connected: " + player.getUsername());

            // Initialize player data
            gamePlayers.computeIfAbsent(player.getUuid(), k -> new GamePlayer());

            // Welcome message
            player.sendMessage(Message.raw("§a§l================================="));
            player.sendMessage(Message.raw("§6§lWelcome to Survival Games!"));
            player.sendMessage(Message.raw("§7This is an early version."));
            player.sendMessage(Message.raw("§7Commands are not yet available."));
            player.sendMessage(Message.raw("§a§l================================="));
        });

        // Player disconnect event
        getEventRegistry().register(PlayerDisconnectEvent.class, event -> {
            PlayerRef player = event.getPlayerRef();
            log("Player disconnected: " + player.getUsername());

            // Save player stats before they leave
            saveConfig();
        });
    }

    public static SurvivalGames getInstance() {
        return instance;
    }

    public GameConfig getConfig() {
        return config;
    }

    public Map<UUID, String> getPlayerKits() {
        return playerKits;
    }

    public Map<UUID, GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    // Configuration classes
    public static class GameConfig {
        public int minPlayers = 2;
        public int maxPlayers = 24;
        public int countdownSeconds = 30;
        public int gracePeriodSeconds = 30;
        public Map<String, KitConfig> kits = new HashMap<>();

        public static GameConfig createDefault() {
            GameConfig config = new GameConfig();

            // Create default kits
            KitConfig warrior = new KitConfig();
            warrior.name = "Warrior";
            warrior.description = "Balanced fighter with sword and armor";
            warrior.icon = "hytale:items/weapons/swords/one_handed/iron_one_handed_sword";
            warrior.items = Arrays.asList(
                "hytale:items/weapons/swords/one_handed/iron_one_handed_sword:1",
                "hytale:items/weapons/bows/shortbow/wooden_shortbow:1",
                "hytale:items/ammo/arrows/iron_arrow:16"
            );
            config.kits.put("warrior", warrior);

            KitConfig archer = new KitConfig();
            archer.name = "Archer";
            archer.description = "Master of ranged combat with extra arrows";
            archer.icon = "hytale:items/weapons/bows/shortbow/wooden_shortbow";
            archer.items = Arrays.asList(
                "hytale:items/weapons/swords/one_handed/wooden_one_handed_sword:1",
                "hytale:items/weapons/bows/shortbow/wooden_shortbow:1",
                "hytale:items/ammo/arrows/iron_arrow:32"
            );
            config.kits.put("archer", archer);

            KitConfig tank = new KitConfig();
            tank.name = "Tank";
            tank.description = "Heavy armor and high survivability";
            tank.icon = "hytale:items/armor/chestplates/iron_chestplate";
            tank.items = Arrays.asList(
                "hytale:items/weapons/swords/one_handed/iron_one_handed_sword:1",
                "hytale:items/armor/helmets/iron_helmet:1",
                "hytale:items/armor/chestplates/iron_chestplate:1"
            );
            config.kits.put("tank", tank);

            return config;
        }
    }

    public static class KitConfig {
        public String name;
        public String description;
        public String icon;
        public List<String> items = new ArrayList<>();
    }

    public static class GamePlayer {
        public int gamesPlayed = 0;
        public int wins = 0;
        public int kills = 0;
        public int deaths = 0;
    }
}

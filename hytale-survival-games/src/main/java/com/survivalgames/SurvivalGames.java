package com.survivalgames;

import com.google.gson.*;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
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
 * A complete Survival Games minigame with kit selection, multiple arenas, and more!
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
        System.out.println("[SurvivalGames] Setting up Survival Games plugin...");

        // Load configuration
        loadConfig();

        // Register commands
        registerCommands();

        System.out.println("[SurvivalGames] Survival Games setup complete!");
    }

    @Override
    public void start() {
        System.out.println("[SurvivalGames] Starting Survival Games plugin...");

        // Register event listeners
        registerEvents();

        System.out.println("[SurvivalGames] Survival Games plugin started successfully!");
        System.out.println("[SurvivalGames] Loaded " + config.kits.size() + " kits");
        System.out.println("[SurvivalGames] Use /sg help for commands");
    }

    @Override
    public void shutdown() {
        System.out.println("[SurvivalGames] Shutting down Survival Games plugin...");

        // Save any pending data
        saveConfig();

        System.out.println("[SurvivalGames] Survival Games plugin shut down successfully!");
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
                System.out.println("[SurvivalGames] Created default configuration");
            } else {
                // Load existing config
                String json = Files.readString(configPath);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                config = gson.fromJson(json, GameConfig.class);
                System.out.println("[SurvivalGames] Loaded configuration from " + configPath);
            }
        } catch (IOException e) {
            System.err.println("[SurvivalGames] ERROR: Failed to load config: " + e.getMessage());
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
            System.err.println("[SurvivalGames] ERROR: Failed to save config: " + e.getMessage());
        }
    }

    private void registerCommands() {
        // TODO: Implement commands once we discover the correct Hytale command API
        // The command package location is different than documented
        System.out.println("[SurvivalGames] Command registration skipped - API discovery needed");
    }

    private void registerEvents() {
        // Player connect event
        getEventRegistry().register(PlayerConnectEvent.class, event -> {
            PlayerRef player = event.getPlayerRef();
            System.out.println("[SurvivalGames] Player connected: " + player.getUsername());

            // Initialize player data
            gamePlayers.computeIfAbsent(player.getUuid(), k -> new GamePlayer());

            // Welcome message
            player.sendMessage(Message.raw("§a§lWelcome to Survival Games!"));
            player.sendMessage(Message.raw("§7Use §e/sg help §7for commands"));
        });

        // Player disconnect event
        getEventRegistry().register(PlayerDisconnectEvent.class, event -> {
            PlayerRef player = event.getPlayerRef();
            System.out.println("[SurvivalGames] Player disconnected: " + player.getUsername());

            // Remove from any active games (will implement later)
        });

        // Player chat event - removed as it's not an async event in this Hytale version
        // Can be added later once we understand the correct event system
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

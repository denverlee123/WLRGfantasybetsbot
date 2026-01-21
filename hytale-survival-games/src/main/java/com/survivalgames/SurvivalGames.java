package com.survivalgames;

import com.google.gson.*;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.event.events.player.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hytale Survival Games Plugin
 * A complete Survival Games minigame with kit selection, statistics, and more!
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

        // Register commands
        registerCommands();

        log("Survival Games setup complete!");
    }

    @Override
    public void start() {
        log("Starting Survival Games plugin...");

        // Register event listeners
        registerEvents();

        log("Survival Games plugin started successfully!");
        log("Loaded " + config.kits.size() + " kits");
        log("Use /sg help for commands");
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

    private void registerCommands() {
        CommandManager commandManager = HytaleServer.get().getCommandManager();

        // Main SG command
        commandManager.register(new AbstractCommand("sg", "Survival Games main command") {
            {
                // Add subcommands
                addSubCommand(new AbstractCommand("help", "Show help") {
                    @Override
                    protected CompletableFuture<Void> execute(CommandContext context) {
                        context.sendMessage(Message.raw("§a§l========== Survival Games =========="));
                        context.sendMessage(Message.raw("§e/sg help §7- Show this help"));
                        context.sendMessage(Message.raw("§e/sg info §7- Plugin information"));
                        context.sendMessage(Message.raw("§e/sg kits §7- List available kits"));
                        context.sendMessage(Message.raw("§e/kit <name> §7- Select a kit"));
                        context.sendMessage(Message.raw("§e/stats §7- View your statistics"));
                        return CompletableFuture.completedFuture(null);
                    }
                });

                addSubCommand(new AbstractCommand("info", "Plugin information") {
                    @Override
                    protected CompletableFuture<Void> execute(CommandContext context) {
                        context.sendMessage(Message.raw("§6§lSURVIVAL GAMES v1.0.0"));
                        context.sendMessage(Message.raw("§7A Hytale Survival Games minigame!"));
                        context.sendMessage(Message.raw("§7Type §e/sg help §7for commands"));
                        return CompletableFuture.completedFuture(null);
                    }
                });

                addSubCommand(new AbstractCommand("kits", "List available kits") {
                    @Override
                    protected CompletableFuture<Void> execute(CommandContext context) {
                        context.sendMessage(Message.raw("§a§l========== Available Kits =========="));
                        config.kits.forEach((id, kit) -> {
                            context.sendMessage(Message.raw("§e" + kit.name + " §8- §7" + kit.description));
                        });
                        return CompletableFuture.completedFuture(null);
                    }
                });
            }

            @Override
            protected CompletableFuture<Void> execute(CommandContext context) {
                // Default action: show help
                context.sendMessage(Message.raw("§a§l========== Survival Games =========="));
                context.sendMessage(Message.raw("§e/sg help §7- Show this help"));
                context.sendMessage(Message.raw("§e/sg info §7- Plugin information"));
                context.sendMessage(Message.raw("§e/sg kits §7- List available kits"));
                return CompletableFuture.completedFuture(null);
            }
        });

        // Kit selection command
        commandManager.register(new AbstractCommand("kit", "Select or view kits") {
            @Override
            protected CompletableFuture<Void> execute(CommandContext context) {
                if (!context.isPlayer()) {
                    context.sendMessage(Message.raw("§cOnly players can use this command!"));
                    return CompletableFuture.completedFuture(null);
                }

                PlayerRef player = context.senderAsPlayerRef();
                String[] input = context.getInputString().split(" ");

                if (input.length < 2) {
                    // List kits
                    context.sendMessage(Message.raw("§a§lAvailable Kits:"));
                    config.kits.forEach((id, kit) -> {
                        context.sendMessage(Message.raw("§e/kit " + id + " §7- " + kit.name));
                    });
                    return CompletableFuture.completedFuture(null);
                }

                String kitId = input[1].toLowerCase();
                if (!config.kits.containsKey(kitId)) {
                    context.sendMessage(Message.raw("§cKit '" + kitId + "' not found!"));
                    return CompletableFuture.completedFuture(null);
                }

                playerKits.put(player.getUuid(), kitId);
                KitConfig kit = config.kits.get(kitId);
                context.sendMessage(Message.raw("§aSelected kit: §e" + kit.name));
                context.sendMessage(Message.raw("§7" + kit.description));
                return CompletableFuture.completedFuture(null);
            }
        });

        // Stats command
        commandManager.register(new AbstractCommand("stats", "View your statistics") {
            @Override
            protected CompletableFuture<Void> execute(CommandContext context) {
                if (!context.isPlayer()) {
                    context.sendMessage(Message.raw("§cOnly players can use this command!"));
                    return CompletableFuture.completedFuture(null);
                }

                PlayerRef player = context.senderAsPlayerRef();
                GamePlayer stats = gamePlayers.computeIfAbsent(player.getUuid(), k -> new GamePlayer());

                context.sendMessage(Message.raw("§a§l========== Your Statistics =========="));
                context.sendMessage(Message.raw("§7Games Played: §e" + stats.gamesPlayed));
                context.sendMessage(Message.raw("§7Wins: §e" + stats.wins));
                context.sendMessage(Message.raw("§7Kills: §e" + stats.kills));
                context.sendMessage(Message.raw("§7Deaths: §e" + stats.deaths));

                if (stats.deaths > 0) {
                    double kd = (double) stats.kills / stats.deaths;
                    context.sendMessage(Message.raw("§7K/D Ratio: §e" + String.format("%.2f", kd)));
                }

                if (stats.gamesPlayed > 0) {
                    double winRate = (double) stats.wins / stats.gamesPlayed * 100;
                    context.sendMessage(Message.raw("§7Win Rate: §e" + String.format("%.1f%%", winRate)));
                }

                return CompletableFuture.completedFuture(null);
            }
        });

        log("Registered 3 commands: /sg, /kit, /stats");
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
            player.sendMessage(Message.raw("§7Type §e/sg help §7for commands"));
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

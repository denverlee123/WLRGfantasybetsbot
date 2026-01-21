package com.survivalgames;

import com.google.gson.*;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.*;
import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.CommandSender;

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
        getLogger().info("Setting up Survival Games plugin...");

        // Load configuration
        loadConfig();

        // Register commands
        registerCommands();

        getLogger().info("Survival Games setup complete!");
    }

    @Override
    public void start() {
        getLogger().info("Starting Survival Games plugin...");

        // Register event listeners
        registerEvents();

        getLogger().info("Survival Games plugin started successfully!");
        getLogger().info("Loaded " + config.kits.size() + " kits");
        getLogger().info("Use /sg help for commands");
    }

    @Override
    public void shutdown() {
        getLogger().info("Shutting down Survival Games plugin...");

        // Save any pending data
        saveConfig();

        getLogger().info("Survival Games plugin shut down successfully!");
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
                getLogger().info("Created default configuration");
            } else {
                // Load existing config
                String json = Files.readString(configPath);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                config = gson.fromJson(json, GameConfig.class);
                getLogger().info("Loaded configuration from " + configPath);
            }
        } catch (IOException e) {
            getLogger().severe("Failed to load config: " + e.getMessage());
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
            getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }

    private void registerCommands() {
        // Main SG command
        getCommandRegistry().registerCommand(new Command() {
            @Override
            public String getName() {
                return "sg";
            }

            @Override
            public void execute(CommandSender sender, String[] args) {
                if (args.length == 0) {
                    sendHelp(sender);
                    return;
                }

                String subCmd = args[0].toLowerCase();
                switch (subCmd) {
                    case "help":
                        sendHelp(sender);
                        break;
                    case "info":
                        sender.sendMessage(Message.raw("§6§lSURVIVAL GAMES v1.0.0"));
                        sender.sendMessage(Message.raw("§7A Hytale Survival Games minigame!"));
                        break;
                    case "kits":
                        listKits(sender);
                        break;
                    default:
                        sender.sendMessage(Message.raw("§cUnknown subcommand. Use /sg help"));
                }
            }

            private void sendHelp(CommandSender sender) {
                sender.sendMessage(Message.raw("§a§l========== Survival Games =========="));
                sender.sendMessage(Message.raw("§e/sg help §7- Show this help"));
                sender.sendMessage(Message.raw("§e/sg info §7- Plugin information"));
                sender.sendMessage(Message.raw("§e/sg kits §7- List available kits"));
                sender.sendMessage(Message.raw("§e/kit <name> §7- Select a kit"));
                sender.sendMessage(Message.raw("§e/stats §7- View your statistics"));
            }

            private void listKits(CommandSender sender) {
                sender.sendMessage(Message.raw("§a§l========== Available Kits =========="));
                config.kits.forEach((id, kit) -> {
                    sender.sendMessage(Message.raw("§e" + kit.name + " §8- §7" + kit.description));
                });
            }
        });

        // Kit selection command
        getCommandRegistry().registerCommand(new Command() {
            @Override
            public String getName() {
                return "kit";
            }

            @Override
            public void execute(CommandSender sender, String[] args) {
                if (!(sender instanceof PlayerRef)) {
                    sender.sendMessage(Message.raw("§cOnly players can use this command!"));
                    return;
                }

                PlayerRef player = (PlayerRef) sender;

                if (args.length == 0) {
                    // List kits
                    sender.sendMessage(Message.raw("§a§lAvailable Kits:"));
                    config.kits.forEach((id, kit) -> {
                        sender.sendMessage(Message.raw("§e/kit " + id + " §7- " + kit.name));
                    });
                    return;
                }

                String kitId = args[0].toLowerCase();
                if (!config.kits.containsKey(kitId)) {
                    sender.sendMessage(Message.raw("§cKit '" + kitId + "' not found!"));
                    return;
                }

                playerKits.put(player.getUuid(), kitId);
                KitConfig kit = config.kits.get(kitId);
                sender.sendMessage(Message.raw("§aSelected kit: §e" + kit.name));
                sender.sendMessage(Message.raw("§7" + kit.description));
            }
        });

        // Stats command
        getCommandRegistry().registerCommand(new Command() {
            @Override
            public String getName() {
                return "stats";
            }

            @Override
            public void execute(CommandSender sender, String[] args) {
                if (!(sender instanceof PlayerRef)) {
                    sender.sendMessage(Message.raw("§cOnly players can use this command!"));
                    return;
                }

                PlayerRef player = (PlayerRef) sender;
                GamePlayer stats = gamePlayers.computeIfAbsent(player.getUuid(), k -> new GamePlayer());

                sender.sendMessage(Message.raw("§a§l========== Your Statistics =========="));
                sender.sendMessage(Message.raw("§7Games Played: §e" + stats.gamesPlayed));
                sender.sendMessage(Message.raw("§7Wins: §e" + stats.wins));
                sender.sendMessage(Message.raw("§7Kills: §e" + stats.kills));
                sender.sendMessage(Message.raw("§7Deaths: §e" + stats.deaths));

                if (stats.deaths > 0) {
                    double kd = (double) stats.kills / stats.deaths;
                    sender.sendMessage(Message.raw("§7K/D Ratio: §e" + String.format("%.2f", kd)));
                }

                if (stats.gamesPlayed > 0) {
                    double winRate = (double) stats.wins / stats.gamesPlayed * 100;
                    sender.sendMessage(Message.raw("§7Win Rate: §e" + String.format("%.1f%%", winRate)));
                }
            }
        });
    }

    private void registerEvents() {
        // Player connect event
        getEventRegistry().register(PlayerConnectEvent.class, event -> {
            PlayerRef player = event.getPlayerRef();
            getLogger().info("Player connected: " + player.getUsername());

            // Initialize player data
            gamePlayers.computeIfAbsent(player.getUuid(), k -> new GamePlayer());

            // Welcome message
            player.sendMessage(Message.raw("§a§lWelcome to Survival Games!"));
            player.sendMessage(Message.raw("§7Use §e/sg help §7for commands"));
        });

        // Player disconnect event
        getEventRegistry().register(PlayerDisconnectEvent.class, event -> {
            PlayerRef player = event.getPlayerRef();
            getLogger().info("Player disconnected: " + player.getUsername());

            // Remove from any active games (will implement later)
        });

        // Player chat event (async)
        getEventRegistry().registerAsync(PlayerChatEvent.class, future -> {
            return future.thenApply(event -> {
                // Could add chat formatting, filtering, etc.
                return event;
            });
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

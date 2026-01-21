package com.survivalgames;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hytale.api.Plugin;
import com.hytale.api.event.EventRegistry;
import com.hytale.api.command.CommandRegistry;
import com.survivalgames.commands.*;
import com.survivalgames.config.ConfigManager;
import com.survivalgames.game.GameManager;
import com.survivalgames.listeners.*;

import java.io.File;
import java.util.logging.Logger;

public class SurvivalGamesPlugin extends Plugin {

    private static SurvivalGamesPlugin instance;
    private Logger logger;
    private Gson gson;
    private ConfigManager configManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        gson = new GsonBuilder().setPrettyPrinting().create();

        logger.info("Enabling Survival Games Plugin...");

        // Create plugin data folder if it doesn't exist
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        // Initialize configuration
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Initialize game manager
        gameManager = new GameManager(this);

        // Register commands
        registerCommands();

        // Register event listeners
        registerEvents();

        logger.info("Survival Games Plugin enabled successfully!");
        logger.info("Default kits loaded: " + configManager.getKitNames().size());
        logger.info("Use /sg help for commands");
    }

    @Override
    public void onDisable() {
        logger.info("Disabling Survival Games Plugin...");

        // Stop all active games
        if (gameManager != null) {
            gameManager.stopAllGames();
        }

        // Save configuration
        if (configManager != null) {
            configManager.saveConfig();
        }

        logger.info("Survival Games Plugin disabled successfully!");
    }

    private void registerCommands() {
        CommandRegistry registry = getCommandRegistry();

        // Player commands
        registry.register(new SGCommand(this));
        registry.register(new JoinCommand(this));
        registry.register(new LeaveCommand(this));
        registry.register(new KitCommand(this));
        registry.register(new StatsCommand(this));

        // Admin commands
        registry.register(new SetLobbyCommand(this));
        registry.register(new SetArenaCommand(this));
        registry.register(new CreateKitCommand(this));
        registry.register(new DeleteKitCommand(this));
        registry.register(new ForceStartCommand(this));
        registry.register(new ForceStopCommand(this));
    }

    private void registerEvents() {
        EventRegistry registry = getEventRegistry();

        // Game events
        registry.register(new PlayerJoinListener(this));
        registry.register(new PlayerQuitListener(this));
        registry.register(new PlayerDeathListener(this));
        registry.register(new PlayerDamageListener(this));
        registry.register(new PlayerMoveListener(this));
        registry.register(new PlayerInteractListener(this));
        registry.register(new BlockBreakListener(this));
        registry.register(new BlockPlaceListener(this));
    }

    public static SurvivalGamesPlugin getInstance() {
        return instance;
    }

    public Gson getGson() {
        return gson;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}

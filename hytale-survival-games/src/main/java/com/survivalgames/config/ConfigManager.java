package com.survivalgames.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.models.Kit;

import java.io.*;
import java.util.*;

public class ConfigManager {

    private final SurvivalGamesPlugin plugin;
    private JsonObject config;
    private Map<String, Kit> kits;

    public ConfigManager(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();
    }

    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.json");

        // If config doesn't exist, copy from resources
        if (!configFile.exists()) {
            try (InputStream in = plugin.getResource("config.json");
                 FileOutputStream out = new FileOutputStream(configFile)) {

                if (in != null) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                    plugin.getLogger().info("Created default config.json");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create default config: " + e.getMessage());
                return;
            }
        }

        // Load config
        try (FileReader reader = new FileReader(configFile)) {
            config = JsonParser.parseReader(reader).getAsJsonObject();
            loadKits();
            plugin.getLogger().info("Configuration loaded successfully");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load config: " + e.getMessage());
        }
    }

    private void loadKits() {
        kits.clear();
        if (config.has("kits")) {
            JsonObject kitsJson = config.getAsJsonObject("kits");
            for (String kitId : kitsJson.keySet()) {
                JsonObject kitJson = kitsJson.getAsJsonObject(kitId);
                Kit kit = new Kit(
                    kitId,
                    kitJson.get("name").getAsString(),
                    kitJson.get("description").getAsString(),
                    kitJson.get("icon").getAsString(),
                    parseItems(kitJson.get("items"))
                );
                kits.put(kitId, kit);
            }
        }
    }

    private List<String> parseItems(com.google.gson.JsonElement itemsElement) {
        List<String> items = new ArrayList<>();
        if (itemsElement != null && itemsElement.isJsonArray()) {
            itemsElement.getAsJsonArray().forEach(item -> items.add(item.getAsString()));
        }
        return items;
    }

    public void saveConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.json");
        try (FileWriter writer = new FileWriter(configFile)) {
            plugin.getGson().toJson(config, writer);
            plugin.getLogger().info("Configuration saved successfully");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }

    public int getMinPlayers() {
        return config.has("minPlayers") ? config.get("minPlayers").getAsInt() : 2;
    }

    public int getMaxPlayers() {
        return config.has("maxPlayers") ? config.get("maxPlayers").getAsInt() : 24;
    }

    public int getCountdownSeconds() {
        return config.has("countdownSeconds") ? config.get("countdownSeconds").getAsInt() : 30;
    }

    public int getGracePeriodSeconds() {
        return config.has("gracePeriodSeconds") ? config.get("gracePeriodSeconds").getAsInt() : 30;
    }

    public int getBorderShrinkStart() {
        return config.has("borderShrinkStart") ? config.get("borderShrinkStart").getAsInt() : 300;
    }

    public double getBorderShrinkSpeed() {
        return config.has("borderShrinkSpeed") ? config.get("borderShrinkSpeed").getAsDouble() : 1.0;
    }

    public int getChestRefillInterval() {
        return config.has("chestRefillInterval") ? config.get("chestRefillInterval").getAsInt() : 180;
    }

    public Kit getKit(String kitId) {
        return kits.get(kitId);
    }

    public Collection<Kit> getAllKits() {
        return kits.values();
    }

    public Set<String> getKitNames() {
        return kits.keySet();
    }

    public void addKit(Kit kit) {
        kits.put(kit.getId(), kit);
        saveKitsToConfig();
        saveConfig();
    }

    public boolean removeKit(String kitId) {
        if (kits.remove(kitId) != null) {
            saveKitsToConfig();
            saveConfig();
            return true;
        }
        return false;
    }

    private void saveKitsToConfig() {
        JsonObject kitsJson = new JsonObject();
        for (Kit kit : kits.values()) {
            JsonObject kitJson = new JsonObject();
            kitJson.addProperty("name", kit.getName());
            kitJson.addProperty("description", kit.getDescription());
            kitJson.addProperty("icon", kit.getIcon());

            com.google.gson.JsonArray itemsArray = new com.google.gson.JsonArray();
            for (String item : kit.getItems()) {
                itemsArray.add(item);
            }
            kitJson.add("items", itemsArray);

            kitsJson.add(kit.getId(), kitJson);
        }
        config.add("kits", kitsJson);
    }
}

package com.survivalgames.commands;

import com.hytale.api.command.AbstractPlayerCommand;
import com.hytale.api.entity.Player;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.models.Kit;
import com.survivalgames.models.PlayerData;

public class KitCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;

    public KitCommand(SurvivalGamesPlugin plugin) {
        super("kit");
        this.plugin = plugin;
        setDescription("Select a kit for the game");
        setUsage("/kit [kit_name]");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            showAvailableKits(player);
            return;
        }

        String kitId = args[0].toLowerCase();
        Kit kit = plugin.getConfigManager().getKit(kitId);

        if (kit == null) {
            player.sendMessage("§cKit '" + kitId + "' does not exist!");
            showAvailableKits(player);
            return;
        }

        // Check if player is in a game
        if (!plugin.getGameManager().isPlayerInGame(player)) {
            player.sendMessage("§cYou must be in a game to select a kit!");
            return;
        }

        // Set kit
        PlayerData data = plugin.getGameManager().getPlayerData(player);
        data.setSelectedKit(kit);

        player.sendMessage("§aYou selected the §e" + kit.getName() + " §akit!");
        player.sendMessage("§7" + kit.getDescription());
    }

    private void showAvailableKits(Player player) {
        player.sendMessage("§a§l========== Available Kits ==========");
        for (Kit kit : plugin.getConfigManager().getAllKits()) {
            player.sendMessage("§e" + kit.getId() + " §7- " + kit.getName());
            player.sendMessage("  §7" + kit.getDescription());
        }
        player.sendMessage("§7Use §e/kit <kit_name> §7to select a kit!");
    }
}

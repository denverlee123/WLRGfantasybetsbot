package com.survivalgames.commands;

import com.hytale.api.command.AbstractPlayerCommand;
import com.hytale.api.entity.Player;
import com.survivalgames.SurvivalGamesPlugin;

public class DeleteKitCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;

    public DeleteKitCommand(SurvivalGamesPlugin plugin) {
        super("deletekit");
        this.plugin = plugin;
        setDescription("Delete an existing kit");
        setUsage("/deletekit <kit_id>");
        setPermission("survivalgames.admin");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("survivalgames.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /deletekit <kit_id>");
            player.sendMessage("§7Available kits: §e" + String.join(", ", plugin.getConfigManager().getKitNames()));
            return;
        }

        String kitId = args[0].toLowerCase();

        if (plugin.getConfigManager().removeKit(kitId)) {
            player.sendMessage("§aDeleted kit: §e" + kitId);
        } else {
            player.sendMessage("§cKit '" + kitId + "' does not exist!");
        }
    }
}

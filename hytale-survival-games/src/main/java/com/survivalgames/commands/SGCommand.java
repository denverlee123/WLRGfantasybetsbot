package com.survivalgames.commands;

import com.hytale.api.command.Command;
import com.hytale.api.command.CommandSender;
import com.survivalgames.SurvivalGamesPlugin;

public class SGCommand extends Command {

    private final SurvivalGamesPlugin plugin;

    public SGCommand(SurvivalGamesPlugin plugin) {
        super("sg");
        this.plugin = plugin;
        setDescription("Main Survival Games command");
        setUsage("/sg [help|info|list]");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("info")) {
            sender.sendMessage("§a§l========================================");
            sender.sendMessage("§6§lSURVIVAL GAMES");
            sender.sendMessage("");
            sender.sendMessage("§7A Hytale Survival Games minigame!");
            sender.sendMessage("§7Version: §e1.0.0");
            sender.sendMessage("§7Author: §eWLRGfantasybetsbot");
            sender.sendMessage("§a§l========================================");
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("§a§lAvailable Arenas:");
            if (plugin.getGameManager().getAllArenas().isEmpty()) {
                sender.sendMessage("§7No arenas configured yet!");
            } else {
                plugin.getGameManager().getAllArenas().forEach(arena -> {
                    String status = arena.isSetup() ? "§a✓ Ready" : "§c✗ Not Setup";
                    sender.sendMessage("§7- §e" + arena.getName() + " " + status);
                });
            }
            return;
        }

        sendHelp(sender);
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§a§l========== Survival Games Help ==========");
        sender.sendMessage("§e/sg help §7- Show this help message");
        sender.sendMessage("§e/sg info §7- Show plugin information");
        sender.sendMessage("§e/sg list §7- List all arenas");
        sender.sendMessage("§e/join <arena> §7- Join a game");
        sender.sendMessage("§e/leave §7- Leave current game");
        sender.sendMessage("§e/kit [kit] §7- Select or view kits");
        sender.sendMessage("§e/stats [player] §7- View statistics");
        sender.sendMessage("");
        sender.sendMessage("§c§lAdmin Commands:");
        sender.sendMessage("§e/sg setlobby <arena> §7- Set lobby spawn");
        sender.sendMessage("§e/sg setarena <arena> §7- Manage arena");
        sender.sendMessage("§e/sg createkit §7- Create a new kit");
        sender.sendMessage("§e/sg deletekit <kit> §7- Delete a kit");
        sender.sendMessage("§e/sg forcestart <arena> §7- Force start game");
        sender.sendMessage("§e/sg forcestop <arena> §7- Force stop game");
    }
}

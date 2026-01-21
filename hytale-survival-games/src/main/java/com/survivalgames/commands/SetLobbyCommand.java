package com.survivalgames.commands;

import com.hytale.api.command.AbstractPlayerCommand;
import com.hytale.api.entity.Player;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.models.Arena;

public class SetLobbyCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;

    public SetLobbyCommand(SurvivalGamesPlugin plugin) {
        super("setlobby");
        this.plugin = plugin;
        setDescription("Set the lobby spawn for an arena");
        setUsage("/setlobby <arena>");
        setPermission("survivalgames.admin");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("survivalgames.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /setlobby <arena>");
            return;
        }

        String arenaName = args[0];
        Arena arena = plugin.getGameManager().getArena(arenaName);

        if (arena == null) {
            arena = plugin.getGameManager().createArena(arenaName);
            player.sendMessage("§aCreated new arena: §e" + arenaName);
        }

        arena.setLobbySpawn(player.getLocation());
        player.sendMessage("§aLobby spawn set for arena §e" + arenaName + "§a!");
    }
}

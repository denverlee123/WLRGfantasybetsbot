package com.survivalgames.commands;

import com.hytale.api.command.AbstractPlayerCommand;
import com.hytale.api.entity.Player;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.models.Arena;

public class JoinCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;

    public JoinCommand(SurvivalGamesPlugin plugin) {
        super("join");
        this.plugin = plugin;
        setDescription("Join a Survival Games arena");
        setUsage("/join <arena>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§cUsage: /join <arena>");
            player.sendMessage("§7Available arenas:");
            plugin.getGameManager().getAllArenas().forEach(arena -> {
                if (arena.isSetup()) {
                    player.sendMessage("§7- §e" + arena.getName());
                }
            });
            return;
        }

        String arenaName = args[0];
        Arena arena = plugin.getGameManager().getArena(arenaName);

        if (arena == null) {
            player.sendMessage("§cArena '" + arenaName + "' does not exist!");
            return;
        }

        if (plugin.getGameManager().joinGame(player, arena)) {
            player.sendMessage("§aYou joined the game in arena §e" + arenaName + "§a!");
        }
    }
}

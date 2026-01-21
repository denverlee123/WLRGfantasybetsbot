package com.survivalgames.commands;

import com.hytale.api.command.AbstractPlayerCommand;
import com.hytale.api.entity.Player;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.game.Game;
import com.survivalgames.models.Arena;

public class ForceStartCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;

    public ForceStartCommand(SurvivalGamesPlugin plugin) {
        super("forcestart");
        this.plugin = plugin;
        setDescription("Force start a game in an arena");
        setUsage("/forcestart <arena>");
        setPermission("survivalgames.admin");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("survivalgames.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /forcestart <arena>");
            return;
        }

        String arenaName = args[0];
        Arena arena = plugin.getGameManager().getArena(arenaName);

        if (arena == null) {
            player.sendMessage("§cArena '" + arenaName + "' does not exist!");
            return;
        }

        Game game = plugin.getGameManager().getGame(arenaName);

        if (game == null || game.getPlayers().isEmpty()) {
            player.sendMessage("§cNo players in arena '" + arenaName + "'!");
            return;
        }

        game.forceStart();
        player.sendMessage("§aForced game start in arena §e" + arenaName);
    }
}

package com.survivalgames.commands;

import com.hytale.api.command.AbstractPlayerCommand;
import com.hytale.api.entity.Player;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.game.Game;

public class ForceStopCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;

    public ForceStopCommand(SurvivalGamesPlugin plugin) {
        super("forcestop");
        this.plugin = plugin;
        setDescription("Force stop a game in an arena");
        setUsage("/forcestop <arena>");
        setPermission("survivalgames.admin");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("survivalgames.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /forcestop <arena>");
            return;
        }

        String arenaName = args[0];
        Game game = plugin.getGameManager().getGame(arenaName);

        if (game == null) {
            player.sendMessage("§cNo active game in arena '" + arenaName + "'!");
            return;
        }

        game.stopGame();
        player.sendMessage("§aForced game stop in arena §e" + arenaName);
    }
}

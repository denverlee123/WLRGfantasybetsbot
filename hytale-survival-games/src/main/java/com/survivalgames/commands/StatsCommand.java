package com.survivalgames.commands;

import com.hytale.api.command.AbstractPlayerCommand;
import com.hytale.api.entity.Player;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.models.PlayerData;

public class StatsCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;

    public StatsCommand(SurvivalGamesPlugin plugin) {
        super("stats");
        this.plugin = plugin;
        setDescription("View Survival Games statistics");
        setUsage("/stats [player]");
    }

    @Override
    public void execute(Player player, String[] args) {
        Player target = player;

        if (args.length > 0) {
            // TODO: Get player by name from server
            // target = plugin.getServer().getPlayer(args[0]);
            player.sendMessage("§cLooking up other players not yet implemented!");
            return;
        }

        PlayerData data = plugin.getGameManager().getPlayerData(target);

        player.sendMessage("§a§l========================================");
        player.sendMessage("§6§lStatistics for " + target.getName());
        player.sendMessage("");
        player.sendMessage("§7Games Played: §e" + data.getGamesPlayed());
        player.sendMessage("§7Wins: §e" + data.getWins());
        player.sendMessage("§7Kills: §e" + data.getKills());
        player.sendMessage("§7Deaths: §e" + data.getDeaths());

        if (data.getDeaths() > 0) {
            double kd = (double) data.getKills() / data.getDeaths();
            player.sendMessage("§7K/D Ratio: §e" + String.format("%.2f", kd));
        }

        if (data.getGamesPlayed() > 0) {
            double winRate = (double) data.getWins() / data.getGamesPlayed() * 100;
            player.sendMessage("§7Win Rate: §e" + String.format("%.1f", winRate) + "%");
        }

        player.sendMessage("§a§l========================================");
    }
}

package com.survivalgames.commands;

import com.hytale.api.command.AbstractPlayerCommand;
import com.hytale.api.entity.Player;
import com.survivalgames.SurvivalGamesPlugin;

public class LeaveCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;

    public LeaveCommand(SurvivalGamesPlugin plugin) {
        super("leave");
        this.plugin = plugin;
        setDescription("Leave the current Survival Games match");
        setUsage("/leave");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (plugin.getGameManager().leaveGame(player)) {
            player.sendMessage("§aYou left the game!");
        }
    }
}

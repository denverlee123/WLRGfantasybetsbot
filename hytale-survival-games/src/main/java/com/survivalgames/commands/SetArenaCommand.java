package com.survivalgames.commands;

import com.hytale.api.command.AbstractPlayerCommand;
import com.hytale.api.entity.Player;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.models.Arena;

public class SetArenaCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;

    public SetArenaCommand(SurvivalGamesPlugin plugin) {
        super("setarena");
        this.plugin = plugin;
        setDescription("Manage arena spawn points and boundaries");
        setUsage("/setarena <arena> <addspawn|clearspawns|corner1|corner2|delete>");
        setPermission("survivalgames.admin");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("survivalgames.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /setarena <arena> <addspawn|clearspawns|corner1|corner2|delete>");
            return;
        }

        String arenaName = args[0];
        String action = args[1].toLowerCase();

        Arena arena = plugin.getGameManager().getArena(arenaName);

        if (action.equals("delete")) {
            if (arena == null) {
                player.sendMessage("§cArena '" + arenaName + "' does not exist!");
                return;
            }
            plugin.getGameManager().deleteArena(arenaName);
            player.sendMessage("§aArena §e" + arenaName + " §adeleted!");
            return;
        }

        if (arena == null) {
            arena = plugin.getGameManager().createArena(arenaName);
            player.sendMessage("§aCreated new arena: §e" + arenaName);
        }

        switch (action) {
            case "addspawn":
                arena.addSpawnPoint(player.getLocation());
                player.sendMessage("§aAdded spawn point #" + arena.getSpawnPoints().size() + " to arena §e" + arenaName);
                break;

            case "clearspawns":
                arena.clearSpawnPoints();
                player.sendMessage("§aCleared all spawn points for arena §e" + arenaName);
                break;

            case "corner1":
                arena.setCorner1(player.getLocation());
                player.sendMessage("§aSet corner 1 for arena §e" + arenaName);
                break;

            case "corner2":
                arena.setCorner2(player.getLocation());
                player.sendMessage("§aSet corner 2 for arena §e" + arenaName);
                break;

            default:
                player.sendMessage("§cUnknown action! Use: addspawn, clearspawns, corner1, corner2, or delete");
                break;
        }

        // Show setup status
        if (arena.isSetup()) {
            player.sendMessage("§a§lArena is ready to use!");
        } else {
            player.sendMessage("§e§lArena setup incomplete:");
            if (arena.getLobbySpawn() == null) {
                player.sendMessage("§7- §cMissing lobby spawn (use /setlobby)");
            }
            if (arena.getSpawnPoints().isEmpty()) {
                player.sendMessage("§7- §cNo spawn points (use /setarena " + arenaName + " addspawn)");
            }
            if (arena.getCorner1() == null || arena.getCorner2() == null) {
                player.sendMessage("§7- §cMissing corners (use /setarena " + arenaName + " corner1/corner2)");
            }
        }
    }
}

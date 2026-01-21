package com.survivalgames.commands;

import com.hytale.api.command.AbstractPlayerCommand;
import com.hytale.api.entity.Player;
import com.hytale.api.inventory.Inventory;
import com.hytale.api.inventory.ItemStack;
import com.survivalgames.SurvivalGamesPlugin;
import com.survivalgames.models.Kit;

import java.util.ArrayList;
import java.util.List;

public class CreateKitCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;

    public CreateKitCommand(SurvivalGamesPlugin plugin) {
        super("createkit");
        this.plugin = plugin;
        setDescription("Create a new kit from your inventory");
        setUsage("/createkit <id> <name> <description> <icon>");
        setPermission("survivalgames.admin");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("survivalgames.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        if (args.length < 4) {
            player.sendMessage("§cUsage: /createkit <id> <name> <description> <icon>");
            player.sendMessage("§7Example: /createkit mage \"Mage\" \"Master of magic\" hytale:items/weapons/staff/wooden_staff");
            return;
        }

        String id = args[0].toLowerCase();
        String name = args[1];
        String description = args[2];
        String icon = args[3];

        // Check if kit already exists
        if (plugin.getConfigManager().getKit(id) != null) {
            player.sendMessage("§cA kit with ID '" + id + "' already exists!");
            return;
        }

        // Get items from player's inventory
        List<String> items = new ArrayList<>();
        Inventory inventory = player.getInventory();

        for (ItemStack item : inventory.getContents()) {
            if (item != null && !item.isEmpty()) {
                String itemId = item.getType().getId();
                int amount = item.getAmount();
                items.add(itemId + ":" + amount);
            }
        }

        if (items.isEmpty()) {
            player.sendMessage("§cYour inventory is empty! Add items to your inventory first.");
            return;
        }

        // Create and save the kit
        Kit kit = new Kit(id, name, description, icon, items);
        plugin.getConfigManager().addKit(kit);

        player.sendMessage("§aCreated new kit: §e" + name);
        player.sendMessage("§7Items: §e" + items.size());
        player.sendMessage("§7Players can now use §e/kit " + id);
    }
}

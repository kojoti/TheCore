/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 */
package me.esshd.api.main.kit.argument;

import java.util.Collections;
import java.util.List;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.kit.Kit;
import me.esshd.api.main.kit.KitManager;
import me.esshd.api.utils.cmds.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class KitGuiArgument
extends CommandArgument {
    private final BasePlugin plugin;

    public KitGuiArgument(BasePlugin plugin) {
        super("gui", "Opens the kit gui");
        this.plugin = plugin;
        this.aliases = new String[]{"menu"};
        this.permission = "command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return String.valueOf(String.valueOf('/')) + label + ' ' + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "Only players may open kit GUI's.");
            return true;
        }
        List<Kit> kits = this.plugin.getKitManager().getKits();
        if (kits.isEmpty()) {
            sender.sendMessage((Object)ChatColor.RED + "No kits have been defined.");
            return true;
        }
        Player player = (Player)sender;
        player.openInventory(this.plugin.getKitManager().getGui(player));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}


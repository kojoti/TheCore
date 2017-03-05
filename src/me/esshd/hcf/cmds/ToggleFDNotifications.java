/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 */
package me.esshd.hcf.cmds;

import java.util.Collections;
import java.util.List;
import me.esshd.hcf.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ToggleFDNotifications
implements CommandExecutor,
TabCompleter {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean newStatus;
        if (!sender.hasPermission("thecore.staff")) {
            sender.sendMessage((Object)ChatColor.RED + "No permission.");
            return true;
        }
        ConfigurationService.DIAMOND_ORE_ALERTS = newStatus = !ConfigurationService.DIAMOND_ORE_ALERTS;
        Bukkit.broadcastMessage((String)((Object)ChatColor.GOLD + sender.getName() + (Object)ChatColor.YELLOW + " has " + (newStatus ? new StringBuilder().append((Object)ChatColor.GREEN).append("enabled").toString() : new StringBuilder().append((Object)ChatColor.RED).append("disabled").toString()) + (Object)ChatColor.YELLOW + " found diamond ore notifications."));
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command commanda, String label, String[] args) {
        return Collections.emptyList();
    }
}


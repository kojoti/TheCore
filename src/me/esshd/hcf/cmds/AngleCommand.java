/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 */
package me.esshd.hcf.cmds;

import java.util.Collections;
import java.util.List;
import me.esshd.api.utils.JavaUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class AngleCommand
implements CommandExecutor,
TabCompleter {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("thecore.staff")) {
            sender.sendMessage((Object)ChatColor.RED + "No Permission.");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Location location = ((Player)sender).getLocation();
        sender.sendMessage((Object)ChatColor.GOLD + JavaUtils.format(Float.valueOf(location.getYaw())) + " yaw" + (Object)ChatColor.WHITE + ", " + (Object)ChatColor.GOLD + JavaUtils.format(Float.valueOf(location.getPitch())) + " pitch");
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}


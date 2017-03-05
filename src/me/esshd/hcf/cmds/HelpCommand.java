/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 */
package me.esshd.hcf.cmds;

import java.util.Arrays;
import java.util.List;
import me.esshd.hcf.LocaleService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class HelpCommand
implements CommandExecutor,
TabCompleter {
    public boolean onCommand(CommandSender p, Command command, String label, String[] args) {
        if (!(p instanceof Player)) {
            p.sendMessage((Object)ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        for (String s : LocaleService.helpLines) {
            p.sendMessage(s);
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = Arrays.asList("jimog was here");
        return list;
    }
}


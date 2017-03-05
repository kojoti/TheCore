/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.plugin.PluginDescriptionFile
 */
package me.esshd.hcfextra.command;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import me.esshd.hcfextra.Configuration;
import me.esshd.hcfextra.HCFExtra;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginDescriptionFile;

public class HCFExtraCommand
implements CommandExecutor,
TabCompleter {
    private static final List<String> COMPLETIONS_FIRST = ImmutableList.of("reload");
    private final HCFExtra plugin;

    public HCFExtraCommand(HCFExtra plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            this.plugin.getConfiguration().reload();
            sender.sendMessage((Object)ChatColor.RED + "Reloaded " + (Object)ChatColor.YELLOW + this.plugin.getDescription().getFullName() + (Object)ChatColor.RED + ".");
            return true;
        }
        sender.sendMessage((Object)ChatColor.RED + "Usage: /" + label + " <reload>");
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? COMPLETIONS_FIRST : Collections.emptyList();
    }
}


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

import java.util.Collections;
import java.util.List;
import me.esshd.hcf.DurationFormatter;
import me.esshd.hcf.HCF;
import me.esshd.hcf.timer.TimerManager;
import me.esshd.hcf.timer.type.GappleTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class GoppleCommand
implements CommandExecutor,
TabCompleter {
    private final HCF plugin;

    public GoppleCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player)sender;
        GappleTimer timer = this.plugin.getTimerManager().getGappleTimer();
        long remaining = timer.getRemaining(player);
        if (remaining <= 0) {
            sender.sendMessage((Object)ChatColor.RED + "Your " + timer.getDisplayName() + (Object)ChatColor.RED + " timer is currently not active.");
            return true;
        }
        sender.sendMessage((Object)ChatColor.YELLOW + "Your " + timer.getDisplayName() + (Object)ChatColor.YELLOW + " timer is active for another " + (Object)ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + (Object)ChatColor.YELLOW + '.');
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}


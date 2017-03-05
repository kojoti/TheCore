/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabExecutor
 *  org.bukkit.entity.Player
 */
package me.esshd.hcf.cmds;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import me.esshd.hcf.HCF;
import me.esshd.hcf.scoreboard.PlayerBoard;
import me.esshd.hcf.scoreboard.ScoreboardHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class ToggleSidebarCommand
implements CommandExecutor,
TabExecutor {
    private final HCF plugin;

    public ToggleSidebarCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        PlayerBoard playerBoard = this.plugin.getScoreboardHandler().getPlayerBoard(((Player)sender).getUniqueId());
        boolean newVisibile = !playerBoard.isSidebarVisible();
        playerBoard.setSidebarVisible(newVisibile);
        sender.sendMessage((Object)ChatColor.GOLD + "Your scoreboard is " + (newVisibile ? new StringBuilder().append((Object)ChatColor.GREEN).append("now").toString() : new StringBuilder().append((Object)ChatColor.RED).append("no longer").toString()) + (Object)ChatColor.GOLD + " visible.");
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}


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
import me.esshd.hcf.user.FactionUser;
import me.esshd.hcf.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class ToggleLightningCommand
implements CommandExecutor,
TabExecutor {
    private final HCF plugin;

    public ToggleLightningCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        FactionUser factionUser = this.plugin.getUserManager().getUser(((Player)sender).getUniqueId());
        boolean newShowLightning = !factionUser.isShowLightning();
        factionUser.setShowLightning(newShowLightning);
        sender.sendMessage((Object)ChatColor.YELLOW + "You will now " + (newShowLightning ? new StringBuilder().append((Object)ChatColor.GREEN).append("able").toString() : new StringBuilder().append((Object)ChatColor.RED).append("unable").toString()) + (Object)ChatColor.YELLOW + " to see lightning strikes on a players death.");
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}


/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.World
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 */
package me.esshd.api.main.cmds.modules.essential;

import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.ServerHandler;
import me.esshd.api.main.cmds.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class StopLagCommand
extends BaseCommand {
    private final BasePlugin plugin;

    public StopLagCommand(BasePlugin plugin) {
        super("stoplag", "Decrease the server lag.");
        this.plugin = plugin;
        this.setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean newMode = !this.plugin.getServerHandler().isDecreasedLagMode();
        this.plugin.getServerHandler().setDecreasedLagMode(newMode);
        String newModeString = Boolean.toString(newMode);
        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", newModeString);
        }
        Command.broadcastCommandMessage((CommandSender)sender, (String)((Object)ChatColor.YELLOW + "Server is " + (newMode ? new StringBuilder().append((Object)ChatColor.RED).append("not").toString() : new StringBuilder().append((Object)ChatColor.GREEN).append("now").toString()) + (Object)ChatColor.YELLOW + " allowing intensive activity."));
        return true;
    }
}


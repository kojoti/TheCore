/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 */
package me.esshd.api.main.cmds.modules.essential;

import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.ServerHandler;
import me.esshd.api.main.cmds.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ToggleDonorOnly
extends BaseCommand {
    BasePlugin plugin;

    public ToggleDonorOnly(BasePlugin plugin) {
        super("toggledonoronly", "Turns the server into Donor only mode.");
        this.setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.plugin.getServerHandler().setDonorOnly(!this.plugin.getServerHandler().isDonorOnly());
        Command.broadcastCommandMessage((CommandSender)sender, (String)((Object)ChatColor.YELLOW + "Server is " + (this.plugin.getServerHandler().isDonorOnly() ? new StringBuilder().append((Object)ChatColor.GREEN).append("now").toString() : new StringBuilder().append((Object)ChatColor.RED).append("not").toString()) + (Object)ChatColor.YELLOW + " in donor only mode."));
        return true;
    }
}


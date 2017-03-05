/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 */
package me.esshd.api.main.cmds.modules.essential;

import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.utils.server.TPS;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LagCommand
extends BaseCommand {
    public LagCommand() {
        super("lag", "Checks the lag of the server.");
        this.setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        double tps = TPS.getTPS();
        double lag = Math.round((1.0 - tps / 20.0) * 100.0);
        ChatColor colour = tps >= 18.0 ? ChatColor.GREEN : (tps >= 15.0 ? ChatColor.YELLOW : ChatColor.RED);
        sender.sendMessage((Object)colour + "TPS: " + (double)Math.round(tps * 10000.0) / 10000.0 + '.');
        sender.sendMessage((Object)colour + "Lag: " + (double)Math.round(lag * 10000.0) / 10000.0 + '%');
        return true;
    }
}


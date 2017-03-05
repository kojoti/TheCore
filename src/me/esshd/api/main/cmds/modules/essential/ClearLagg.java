/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package me.esshd.api.main.cmds.modules.essential;

import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.ServerHandler;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.main.task.ClearEntityHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ClearLagg
extends BaseCommand {
    public ClearLagg() {
        super("clearlagg", "Clears the lag on the server");
        this.setAliases(new String[]{"cl", "laggclear", "clearlag", "clag"});
        this.setUsage("/(command) [Delay]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(this.getUsage(label));
            return true;
        }
        if (args.length != 1) {
            return true;
        }
        if (!ClearLagg.isNumeric(args[0])) {
            sender.sendMessage((Object)ChatColor.RED + "Must be a number");
            return true;
        }
        BasePlugin.getPlugin().clearEntityHandler.cancel();
        ClearEntityHandler clearEntityHandler = new ClearEntityHandler();
        BasePlugin.getPlugin().clearEntityHandler = clearEntityHandler;
        BasePlugin.getPlugin().clearEntityHandler.runTaskTimer((Plugin)BasePlugin.getPlugin(), (long)Integer.parseInt(args[0]), (long)Integer.parseInt(args[0]));
        Command.broadcastCommandMessage((CommandSender)sender, (String)((Object)ChatColor.YELLOW + "Changed the Clear Lag From " + BasePlugin.getPlugin().getServerHandler().getClaggDelay() + " To " + Integer.parseInt(args[0])), (boolean)true);
        BasePlugin.getPlugin().getServerHandler().setClearlagdelay(Integer.parseInt(args[0]));
        return true;
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        }
        catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}


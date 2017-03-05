/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.esshd.api.main.cmds.modules.essential;

import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SetMaxPlayersCommand
extends BaseCommand {
    public SetMaxPlayersCommand() {
        super("setmaxplayers", "Sets the max player cap.");
        this.setAliases(new String[]{"setplayercap"});
        this.setUsage("/(command) <amount>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(this.getUsage(label));
            return true;
        }
        Integer amount = Integer.parseInt(args[0]);
        if (amount == null) {
            sender.sendMessage((Object)ChatColor.RED + "'" + args[0] + "' is not a number.");
            return true;
        }
        BasePlugin.getPlugin().getJavaPlugin().getConfig().set("maxOnline", (Object)amount);
        Command.broadcastCommandMessage((CommandSender)sender, (String)((Object)ChatColor.YELLOW + "Set the maximum players to " + amount + '.'));
        return true;
    }
}


/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.esshd.api.main.cmds.modules.essential;

import java.util.Collections;
import java.util.List;
import me.esshd.api.main.BaseConstants;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.main.task.Ping;
import me.esshd.api.utils.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand
extends BaseCommand {
    public PingCommand() {
        super("ping", "Checks the ping of a player.");
        this.setUsage("/(command) <player>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length > 0 && sender.hasPermission(String.valueOf(String.valueOf(command.getPermission())) + ".others")) {
            target = BukkitUtils.playerWithNameOrUUID(args[0]);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(this.getUsage(label));
                return true;
            }
            target = (Player)sender;
        }
        if (target == null || !BaseCommand.canSee(sender, target)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }
        sender.sendMessage(String.valueOf(String.valueOf(target.equals((Object)sender) ? new StringBuilder().append((Object)ChatColor.GRAY).append("Your ping is ").toString() : new StringBuilder().append((Object)ChatColor.GRAY).append("Ping of ").append(target.getName()).toString())) + (Object)ChatColor.YELLOW + ": " + (Object)ChatColor.RED + Ping.getPing(target));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 && sender.hasPermission(String.valueOf(String.valueOf(command.getPermission())) + ".others") ? null : Collections.emptyList();
    }
}


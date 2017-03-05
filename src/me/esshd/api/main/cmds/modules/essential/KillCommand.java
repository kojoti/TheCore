/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 */
package me.esshd.api.main.cmds.modules.essential;

import java.util.Collections;
import java.util.List;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class KillCommand
extends BaseCommand {
    public KillCommand() {
        super("kill", "Kills a player.");
        this.setAliases(new String[]{"slay"});
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
        if (target.isDead()) {
            sender.sendMessage((Object)ChatColor.RED + target.getName() + " is already dead.");
            return true;
        }
        EntityDamageEvent event = new EntityDamageEvent((Entity)target, EntityDamageEvent.DamageCause.SUICIDE, 10000);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) {
            sender.sendMessage((Object)ChatColor.RED + "You cannot kill " + target.getName() + '.');
            return true;
        }
        target.setLastDamageCause(event);
        target.setHealth(0.0);
        if (sender.equals((Object)target)) {
            sender.sendMessage((Object)ChatColor.GOLD + "You have been killed.");
            return true;
        }
        Command.broadcastCommandMessage((CommandSender)sender, (String)((Object)ChatColor.YELLOW + "Slain player " + target.getName() + '.'));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 && sender.hasPermission(String.valueOf(String.valueOf(command.getPermission())) + ".others") ? null : Collections.emptyList();
    }
}


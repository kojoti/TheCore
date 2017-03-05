/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.org.apache.commons.lang3.StringUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.esshd.api.main.cmds.modules.essential;

import me.esshd.api.main.cmds.BaseCommand;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RequestCommand
extends BaseCommand {
    private final String RECIEVE_MESSAGE;

    public RequestCommand() {
        super("request", "Gets the attention of staff.");
        this.setUsage("/(command) <Message>");
        this.setAliases(new String[]{"helpop"});
        this.RECIEVE_MESSAGE = "core.staff";
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (args.length == 0) {
            cs.sendMessage(this.getUsage(s));
            return true;
        }
        String message = StringUtils.join((Object[])args, (char)' ');
        cs.sendMessage(ChatColor.GREEN + "Your message has been sent to all online staff!");
        Bukkit.broadcast(ChatColor.GOLD + "[Request] " + ChatColor.RESET + cs.getName() + ChatColor.YELLOW + " requested \"" + ChatColor.ITALIC + message + ChatColor.YELLOW + "\"",""
        		+ this.RECIEVE_MESSAGE);
        return true;
    }
}


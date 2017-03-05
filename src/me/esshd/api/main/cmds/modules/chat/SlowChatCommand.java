/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.time.DurationFormatUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 */
package me.esshd.api.main.cmds.modules.chat;

import java.util.concurrent.TimeUnit;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.ServerHandler;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.utils.JavaUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SlowChatCommand
extends BaseCommand {
    private static final long DEFAULT_DELAY = TimeUnit.SECONDS.toMillis(5);
    private final BasePlugin plugin;

    public SlowChatCommand(BasePlugin plugin) {
        super("slowchat", "Slows the chat down for non-staff.");
        this.setAliases(new String[]{"slow"});
        this.setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Long newTicks;
        long oldTicks = this.plugin.getServerHandler().getRemainingChatSlowedMillis();
        if (oldTicks > 0) {
            newTicks = Long.parseLong("0");
        } else if (args.length < 1) {
            newTicks = DEFAULT_DELAY;
        } else {
            newTicks = JavaUtils.parse(args[0]);
            if (newTicks == -1) {
                sender.sendMessage((Object)ChatColor.RED + "Invalid duration, use the correct format: 10m1s");
                return true;
            }
        }
        this.plugin.getServerHandler().setChatSlowedMillis(newTicks);
        Bukkit.broadcastMessage((String)((Object)ChatColor.RED + "The chat " + (newTicks > 0 ? new StringBuilder("has slowed down for ").append(DurationFormatUtils.formatDurationWords((long)newTicks, (boolean)true, (boolean)true)).toString() : new StringBuilder().append((Object)ChatColor.RED).append("no longer slowed").toString()) + (Object)ChatColor.RED + '.'));
        return true;
    }
}


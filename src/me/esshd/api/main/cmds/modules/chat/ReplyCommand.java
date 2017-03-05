/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang.StringUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package me.esshd.api.main.cmds.modules.chat;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.main.event.PlayerMessageEvent;
import me.esshd.api.main.user.BaseUser;
import me.esshd.api.main.user.UserManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class ReplyCommand
extends BaseCommand {
    private static final long VANISH_REPLY_TIMEOUT = TimeUnit.SECONDS.toMillis(45);
    private final BasePlugin plugin;

    public ReplyCommand(BasePlugin plugin) {
        super("reply", "Replies to the last conversing player.");
        this.setAliases(new String[]{"r"});
        this.setUsage("/(command) <message>");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable for players.");
            return true;
        }
        Player player = (Player)sender;
        UUID uuid = player.getUniqueId();
        BaseUser baseUser = this.plugin.getUserManager().getUser(uuid);
        UUID lastReplied = baseUser.getLastRepliedTo();
        Player target = lastReplied == null ? null : Bukkit.getPlayer((UUID)lastReplied);
        Player player2 = target;
        if (args.length < 1) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            if (lastReplied != null && BaseCommand.canSee(sender, target)) {
                sender.sendMessage((Object)ChatColor.RED + "You are in a conversation with " + target.getName() + '.');
            }
            return true;
        }
        long millis = System.currentTimeMillis();
        if (target == null || !BaseCommand.canSee(sender, target) && millis - baseUser.getLastReceivedMessageMillis() > VANISH_REPLY_TIMEOUT) {
            sender.sendMessage((Object)ChatColor.GOLD + "There is no player to reply to.");
            return true;
        }
        String message = StringUtils.join((Object[])args, (char)' ');
        HashSet recipients = Sets.newHashSet((Object[])new Player[]{target});
        PlayerMessageEvent playerMessageEvent = new PlayerMessageEvent(player, recipients, message, false);
        Bukkit.getPluginManager().callEvent((Event)playerMessageEvent);
        if (!playerMessageEvent.isCancelled()) {
            playerMessageEvent.send();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}


/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package me.esshd.api.main.cmds.modules.chat;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import me.esshd.api.main.BaseConstants;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.main.event.PlayerMessageEvent;
import me.esshd.api.utils.BukkitUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class MessageCommand
extends BaseCommand {
    public MessageCommand(BasePlugin plugin) {
        super("message", "Sends a message to a recipient(s).");
        this.setAliases(new String[]{"msg", "m", "whisper", "w", "tell"});
        this.setUsage("/(command) <player> <message>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable for players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage((Object)ChatColor.RED + this.getUsage(label));
            return true;
        }
        Player player = (Player)sender;
        Player target = BukkitUtils.playerWithNameOrUUID(args[0]);
        if (target == null || !BaseCommand.canSee(sender, target)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }
        String message = StringUtils.join((Object[])args, (char)' ', (int)1, (int)args.length);
        Set<Player> recipients = Collections.singleton(target);
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


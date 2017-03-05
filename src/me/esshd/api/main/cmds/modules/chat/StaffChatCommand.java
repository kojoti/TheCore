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
package me.esshd.api.main.cmds.modules.chat;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.main.user.BaseUser;
import me.esshd.api.main.user.ServerParticipator;
import me.esshd.api.main.user.UserManager;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand
extends BaseCommand {
    private final BasePlugin plugin;

    public StaffChatCommand(BasePlugin plugin) {
        super("staffchat", "Enters staff chat mode.");
        this.setAliases(new String[]{"sc", "ac"});
        this.setUsage("/(command) [playerName]");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerParticipator target;
        ServerParticipator participator = this.plugin.getUserManager().getParticipator(sender);
        if (participator == null) {
            sender.sendMessage((Object)ChatColor.RED + "You are not allowed to do this.");
            return true;
        }
        if (args.length <= 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage((Object)ChatColor.RED + "Usage: /" + label + " <message|playerName>");
                return true;
            }
            target = participator;
        } else {
            Player targetPlayer = Bukkit.getPlayerExact((String)args[0]);
            if (targetPlayer == null || !BaseCommand.canSee(sender, targetPlayer) || !sender.hasPermission(String.valueOf(String.valueOf(command.getPermission())) + ".others")) {
                String message = StringUtils.join((Object[])args, (char)' ');
                String format = (Object)ChatColor.AQUA + String.format(Locale.ENGLISH, new StringBuilder("%1$s").append((Object)ChatColor.GRAY).append(": ").append((Object)ChatColor.AQUA).append("%2$s").toString(), sender.getName(), message);
                Bukkit.getConsoleSender().sendMessage(format);
                for (Player other : Bukkit.getOnlinePlayers()) {
                    BaseUser otherUser = this.plugin.getUserManager().getUser(other.getUniqueId());
                    if (!otherUser.isStaffChatVisible() || !other.hasPermission("base.command.staffchat")) continue;
                    other.sendMessage(format);
                }
                return true;
            }
            target = this.plugin.getUserManager().getUser(targetPlayer.getUniqueId());
        }
        boolean newStaffChat = !target.isInStaffChat() || args.length >= 2 && Boolean.parseBoolean(args[1]);
        target.setInStaffChat(newStaffChat);
        sender.sendMessage((Object)ChatColor.YELLOW + "Staff chat mode of " + target.getName() + " set to " + newStaffChat + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}


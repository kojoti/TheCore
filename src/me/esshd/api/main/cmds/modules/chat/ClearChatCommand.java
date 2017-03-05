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

import me.esshd.api.main.cmds.BaseCommand;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand
extends BaseCommand {
    private static final int CHAT_HEIGHT = 101;
    private static final String BYPASS_PERMISSION = "command.clearchat.bypass";
    private static final String[] CLEAR_MESSAGE = new String[101];

    public ClearChatCommand() {
        super("clearchat", "Clears the server chat for players.");
        this.setAliases(new String[]{"cc"});
        this.setUsage("/(command) <reason>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(this.getUsage());
            return true;
        }
        String reason = StringUtils.join((Object[])args, (char)' ');
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("command.clearchat.bypass")) {
                p.sendMessage(CLEAR_MESSAGE);
                p.sendMessage((Object)ChatColor.GREEN + sender.getName() + " has cleared chat for " + reason);
                continue;
            }
            p.sendMessage((Object)ChatColor.GREEN + sender.getName() + " has cleared chat for " + reason);
        }
        return true;
    }
}


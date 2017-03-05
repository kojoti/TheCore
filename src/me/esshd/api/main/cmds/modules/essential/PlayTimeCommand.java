/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.time.DurationFormatUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.esshd.api.main.cmds.modules.essential;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import me.esshd.api.main.BaseConstants;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.PlayTimeManager;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.utils.BukkitUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTimeCommand
extends BaseCommand {
    private final BasePlugin plugin;

    public PlayTimeCommand(BasePlugin plugin) {
        super("playtime", "Check the playtime of another player.");
        this.setAliases(new String[]{"pt"});
        this.setUsage("/(command) [player]");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer target;
        if (args.length >= 1) {
            target = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(this.getUsage(label));
                return true;
            }
            target = (OfflinePlayer)sender;
        }
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }
        sender.sendMessage((Object)ChatColor.YELLOW + target.getName() + " has been playing for " + (Object)ChatColor.GREEN + DurationFormatUtils.formatDurationWords((long)this.plugin.getPlayTimeManager().getTotalPlayTime(target.getUniqueId()), (boolean)true, (boolean)true) + (Object)ChatColor.YELLOW + " this map.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.emptyList();
    }
}


/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.esshd.api.main.kit.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.PlayTimeManager;
import me.esshd.api.main.kit.Kit;
import me.esshd.api.main.kit.KitManager;
import me.esshd.api.main.user.BaseUser;
import me.esshd.api.main.user.UserManager;
import me.esshd.api.utils.cmds.CommandArgument;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitListArgument
extends CommandArgument {
    private final BasePlugin plugin;

    public KitListArgument(BasePlugin plugin) {
        super("list", "Lists all current kits");
        this.plugin = plugin;
        this.permission = "command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return String.valueOf(String.valueOf('/')) + label + ' ' + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Kit> kits = this.plugin.getKitManager().getKits();
        if (kits.isEmpty()) {
            sender.sendMessage((Object)ChatColor.RED + "No kits have been defined.");
            return true;
        }
        ArrayList<String> kitNames = new ArrayList<String>();
        for (Kit kit : kits) {
            String permission = kit.getPermissionNode();
            if (permission != null && !sender.hasPermission(permission)) continue;
            BaseUser user = this.plugin.getUserManager().getUser(((Player)sender).getUniqueId());
            ChatColor color = user.getKitUses(kit) >= kit.getMaximumUses() || user.getRemainingKitCooldown(kit) >= (long)kit.getMaximumUses() || this.plugin.getPlayTimeManager().getTotalPlayTime(((Player)sender).getUniqueId()) <= kit.getMinPlaytimeMillis() ? ChatColor.RED : ChatColor.GREEN;
            kitNames.add((Object)color + kit.getDisplayName());
        }
        String kitList = StringUtils.join(kitNames, (String)((Object)ChatColor.GRAY + ", "));
        sender.sendMessage((Object)ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Kit List" + (Object)ChatColor.GREEN + "[" + kitNames.size() + '/' + kits.size() + "]");
        sender.sendMessage((Object)ChatColor.GRAY + "[" + (Object)ChatColor.RED + kitList + (Object)ChatColor.GRAY + ']');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}


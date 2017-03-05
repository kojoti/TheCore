/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  ru.tehkode.permissions.PermissionGroup
 *  ru.tehkode.permissions.bukkit.PermissionsEx
 */
package me.esshd.api.main.cmds.modules.essential;

import java.util.ArrayList;
import me.esshd.api.main.cmds.BaseCommand;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ListCommand
extends BaseCommand {
    private final String TO_BE_ON_LIST_PERMISSION;

    public ListCommand() {
        super("list", "Lists players online");
        this.setAliases(new String[]{"who"});
        this.setUsage("/(command)");
        this.TO_BE_ON_LIST_PERMISSION = "command.list.staff";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> list = new ArrayList<String>();
 
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(this.TO_BE_ON_LIST_PERMISSION) || !BaseCommand.canSee(sender, player)) continue;
            list.add(player.getName());
        }
        sender.sendMessage("\u00a77There are currently \u00a76\u00a7l" + Bukkit.getOnlinePlayers().size() + " \u00a77connected out of the maximum \u00a76\u00a7l" + Bukkit.getMaxPlayers() + "\u00a77.");
        sender.sendMessage("\u00a77There are currently \u00a76\u00a7l" + list.size() + " \u00a77staff connected: \u00a76" + list.toString().replace("[", "").replace("]", ""));
        return true;
    }

    public String getRealName(String group) {
        return PermissionsEx.getPermissionManager().getGroup(group).getPrefix().replace("[", "").replace("]", "").replace("_", "").replace(group, "");
    }
}


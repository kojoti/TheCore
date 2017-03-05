/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package me.esshd.api.main.cmds.modules.essential;

import java.util.Collections;
import java.util.List;
import me.esshd.api.main.cmds.BaseCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand
extends BaseCommand {
    public RenameCommand() {
        super("rename", "Rename your held item.");
        this.setUsage("/(command) <name>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String newName;
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "Only players can execute this command.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(this.getUsage(label));
            return true;
        }
        Player player = (Player)sender;
        ItemStack stack = player.getItemInHand();
        if (stack == null || stack.getType() == Material.AIR) {
            sender.sendMessage((Object)ChatColor.RED + "You are not holding anything.");
            return true;
        }
        ItemMeta meta = stack.getItemMeta();
        String oldName = meta.getDisplayName();
        if (oldName != null) {
            oldName = oldName.trim();
        }
        String string = newName = args[0].equalsIgnoreCase("none") || args[0].equalsIgnoreCase("null") ? null : ChatColor.translateAlternateColorCodes((char)'&', (String)StringUtils.join((Object[])args, (char)' ', (int)0, (int)args.length));
        if (oldName == null && newName == null) {
            sender.sendMessage((Object)ChatColor.RED + "Your held item already has no name.");
            return true;
        }
        if (oldName != null && oldName.equals(newName)) {
            sender.sendMessage((Object)ChatColor.RED + "Your held item is already named this.");
            return true;
        }
        meta.setDisplayName(newName);
        stack.setItemMeta(meta);
        if (newName == null) {
            sender.sendMessage((Object)ChatColor.YELLOW + "Removed name of held item from " + oldName + '.');
            return true;
        }
        sender.sendMessage((Object)ChatColor.YELLOW + "Renamed item in hand from " + (oldName == null ? "no name" : oldName) + (Object)ChatColor.YELLOW + " to " + newName + (Object)ChatColor.YELLOW + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}


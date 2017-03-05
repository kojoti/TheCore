/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package me.esshd.api.main.cmds.modules.inventory;

import java.util.HashMap;
import me.esshd.api.main.BaseConstants;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.utils.itemdb.ItemDb;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GiveCommand
extends BaseCommand {
    public GiveCommand() {
        super("give", "Gives an item to a player.");
        this.setUsage("/(command) <player> <item> [amount]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable for players.");
            return true;
        }
        Player p = (Player)sender;
        if (args.length < 2) {
            p.sendMessage((Object)ChatColor.RED + this.getUsage());
            return true;
        }
        if (Bukkit.getPlayer((String)args[0]) == null) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }
        Integer ammount = 0;
        Player t = Bukkit.getPlayer((String)args[0]);
        if (BasePlugin.getPlugin().getItemDb().getItem(args[1]) == null) {
            sender.sendMessage((Object)ChatColor.RED + "Item or ID not found.");
            return true;
        }
        if (args.length == 2) {
            if (!t.getInventory().addItem(new ItemStack[]{BasePlugin.getPlugin().getItemDb().getItem(args[1], BasePlugin.getPlugin().getItemDb().getItem(args[1]).getMaxStackSize())}).isEmpty()) {
                p.sendMessage((Object)ChatColor.RED + "The inventory of the player is full.");
                return true;
            }
            ammount = 64;
        }
        if (args.length == 3) {
            if (!t.getInventory().addItem(new ItemStack[]{BasePlugin.getPlugin().getItemDb().getItem(args[1], Integer.parseInt(args[2]))}).isEmpty()) {
                p.sendMessage((Object)ChatColor.RED + "The inventory of the player is full.");
                return true;
            }
            ammount = Integer.parseInt(args[2]);
        }
        Command.broadcastCommandMessage((CommandSender)sender, (String)((Object)ChatColor.GRAY + "Has given " + t.getName() + ammount.toString()));
        return true;
    }
}


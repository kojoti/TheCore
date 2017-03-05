/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package me.esshd.api.main.cmds.modules.inventory;

import me.esshd.api.main.cmds.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoreCommand
extends BaseCommand {
    public MoreCommand() {
        super("more", "Sets your item to its maximum amount.");
        this.setUsage("/(command)");
    }

    @Override
    public boolean isPlayerOnlyCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Integer amount;
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable for players.");
            return true;
        }
        Player player = (Player)sender;
        ItemStack stack = player.getItemInHand();
        if (stack == null || stack.getType() == Material.AIR) {
            sender.sendMessage((Object)ChatColor.RED + "You are not holding any item.");
            return true;
        }
        if (args.length > 0) {
            amount = Integer.parseInt(args[0]);
            if (amount == null) {
                sender.sendMessage((Object)ChatColor.RED + "'" + args[0] + "' is not a number.");
                return true;
            }
            if (amount <= 0) {
                sender.sendMessage((Object)ChatColor.RED + "Item amounts must be positive.");
                return true;
            }
        } else {
            int curAmount = stack.getAmount();
            if (curAmount >= (amount = Integer.valueOf(stack.getMaxStackSize()))) {
                sender.sendMessage((Object)ChatColor.RED + "You already have the maximum amount: " + amount + '.');
                return true;
            }
        }
        stack.setAmount(amount.intValue());
        return true;
    }
}


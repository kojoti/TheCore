/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 */
package me.esshd.api.main.kit.argument;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.kit.Kit;
import me.esshd.api.main.kit.KitManager;
import me.esshd.api.utils.cmds.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KitSetMaxUsesArgument
extends CommandArgument {
    private static final List<String> COMPLETIONS_THIRD = ImmutableList.of("UNLIMITED");
    private final BasePlugin plugin;

    public KitSetMaxUsesArgument(BasePlugin plugin) {
        super("setmaxuses", "Sets the maximum uses for a kit");
        this.plugin = plugin;
        this.aliases = new String[]{"setmaximumuses"};
        this.permission = "base.command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return String.valueOf(String.valueOf('/')) + label + ' ' + this.getName() + " <kitName> <amount|unlimited>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Integer amount;
        if (args.length < 3) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage((Object)ChatColor.RED + "There is not a kit named " + args[1] + '.');
            return true;
        }
        if (COMPLETIONS_THIRD.contains(args[2])) {
            amount = Integer.MAX_VALUE;
        } else {
            amount = Integer.parseInt(args[2]);
            if (amount == null) {
                sender.sendMessage((Object)ChatColor.RED + "'" + args[2] + "' is not a number.");
                return true;
            }
        }
        kit.setMaximumUses(amount.intValue());
        sender.sendMessage((Object)ChatColor.GRAY + "Set maximum uses of kit " + kit.getDisplayName() + " to " + (amount.intValue() == Integer.MAX_VALUE ? "unlimited" : amount) + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            List<Kit> kits = this.plugin.getKitManager().getKits();
            ArrayList<String> results = new ArrayList<String>(kits.size());
            for (Kit kit : kits) {
                results.add(kit.getName());
            }
            return results;
        }
        if (args.length == 3) {
            return COMPLETIONS_THIRD;
        }
        return Collections.emptyList();
    }
}


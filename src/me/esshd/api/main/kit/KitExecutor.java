/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.esshd.api.main.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.kit.Kit;
import me.esshd.api.main.kit.KitManager;
import me.esshd.api.main.kit.argument.KitApplyArgument;
import me.esshd.api.main.kit.argument.KitCreateArgument;
import me.esshd.api.main.kit.argument.KitDeleteArgument;
import me.esshd.api.main.kit.argument.KitDisableArgument;
import me.esshd.api.main.kit.argument.KitGuiArgument;
import me.esshd.api.main.kit.argument.KitListArgument;
import me.esshd.api.main.kit.argument.KitPreviewArgument;
import me.esshd.api.main.kit.argument.KitRenameArgument;
import me.esshd.api.main.kit.argument.KitSetDelayArgument;
import me.esshd.api.main.kit.argument.KitSetDescriptionArgument;
import me.esshd.api.main.kit.argument.KitSetImageArgument;
import me.esshd.api.main.kit.argument.KitSetIndexArgument;
import me.esshd.api.main.kit.argument.KitSetItemsArgument;
import me.esshd.api.main.kit.argument.KitSetMaxUsesArgument;
import me.esshd.api.main.kit.argument.KitSetminplaytimeArgument;
import me.esshd.api.utils.BukkitUtils;
import me.esshd.api.utils.cmds.ArgumentExecutor;
import me.esshd.api.utils.cmds.CommandArgument;
import me.esshd.api.utils.cmds.CommandWrapper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitExecutor
extends ArgumentExecutor {
    private final BasePlugin plugin;

    public KitExecutor(BasePlugin plugin) {
        super("kit");
        this.plugin = plugin;
        this.addArgument(new KitApplyArgument(plugin));
        this.addArgument(new KitCreateArgument(plugin));
        this.addArgument(new KitDeleteArgument(plugin));
        this.addArgument(new KitSetDescriptionArgument(plugin));
        this.addArgument(new KitDisableArgument(plugin));
        this.addArgument(new KitGuiArgument(plugin));
        this.addArgument(new KitListArgument(plugin));
        this.addArgument(new KitPreviewArgument(plugin));
        this.addArgument(new KitRenameArgument(plugin));
        this.addArgument(new KitSetDelayArgument(plugin));
        this.addArgument(new KitSetImageArgument(plugin));
        this.addArgument(new KitSetIndexArgument(plugin));
        this.addArgument(new KitSetItemsArgument(plugin));
        this.addArgument(new KitSetMaxUsesArgument(plugin));
        this.addArgument(new KitSetminplaytimeArgument(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            CommandWrapper.printUsage(sender, label, this.arguments);
            sender.sendMessage((Object)ChatColor.GREEN + "/" + label + " <kitName> " + (Object)ChatColor.GRAY + "- Applies a kit.");
            return true;
        }
        CommandArgument argument = this.getArgument(args[0]);
        String permission = argument == null ? null : argument.getPermission();
        String string = permission;
        if (argument == null || permission != null && !sender.hasPermission(permission)) {
            String kitPermission;
            Kit kit = this.plugin.getKitManager().getKit(args[0]);
            if (sender instanceof Player && kit != null && ((kitPermission = kit.getPermissionNode()) == null || sender.hasPermission(kitPermission))) {
                Player player = (Player)sender;
                kit.applyTo(player, false, true);
                return true;
            }
            sender.sendMessage((Object)ChatColor.RED + "Kit or command " + args[0] + " not found.");
            return true;
        }
        argument.onCommand(sender, command, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return super.onTabComplete(sender, command, label, args);
        }
        List<String> previous = super.onTabComplete(sender, command, label, args);
        ArrayList<String> kitNames = new ArrayList<String>();
        for (Kit kit : this.plugin.getKitManager().getKits()) {
            String permission = kit.getPermissionNode();
            if (permission != null && !sender.hasPermission(permission)) continue;
            kitNames.add(kit.getName());
        }
        if (previous == null || previous.isEmpty()) {
            previous = kitNames;
        } else {
            previous = new ArrayList<String>(previous);
            previous.addAll(0, kitNames);
        }
        return BukkitUtils.getCompletions(args, previous);
    }
}


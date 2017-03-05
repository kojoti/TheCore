/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package me.esshd.api.main.cmds.modules.warp;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.main.cmds.modules.warp.WarpListArgument;
import me.esshd.api.main.cmds.modules.warp.WarpRemoveArgument;
import me.esshd.api.main.cmds.modules.warp.WarpSetArgument;
import me.esshd.api.main.warp.Warp;
import me.esshd.api.main.warp.WarpManager;
import me.esshd.api.utils.BukkitUtils;
import me.esshd.api.utils.cmds.CommandArgument;
import me.esshd.api.utils.cmds.CommandWrapper;
import me.esshd.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WarpExecutor
extends BaseCommand
implements Listener {
    private final List<CommandArgument> arguments = new ArrayList<CommandArgument>(3);
    private final Map<UUID, BukkitRunnable> taskMap = new HashMap<UUID, BukkitRunnable>();
    private final BasePlugin plugin;

    public WarpExecutor(BasePlugin plugin) {
        super("warp", "Teleport to locations on the server.");
        this.setAliases(new String[]{"gw", "globalwarp"});
        this.setUsage("/(command)");
        this.plugin = plugin;
        this.arguments.add(new WarpListArgument(plugin));
        this.arguments.add(new WarpRemoveArgument(plugin));
        this.arguments.add(new WarpSetArgument(plugin));
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)HCF.getPlugin());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockZ() == e.getFrom().getBlockZ()) {
            return;
        }
        if (this.taskMap.containsKey(e.getPlayer().getUniqueId())) {
            this.taskMap.get(e.getPlayer().getUniqueId()).cancel();
            this.taskMap.remove(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage((Object)ChatColor.RED + "Warp canceled you moved.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            CommandWrapper.printUsage(sender, label, this.arguments);
            sender.sendMessage((Object)ChatColor.GREEN + "/" + label + " <warpName> " + (Object)ChatColor.GRAY + "- Teleport to a server warp.");
            return true;
        }
        CommandArgument argument = CommandWrapper.matchArgument(args[0], sender, this.arguments);
        if (argument == null) {
            this.handleWarp(sender, args);
            return true;
        }
        return argument.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List results2;
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            results2 = Lists.newArrayList((Iterable)Iterables.concat(CommandWrapper.getAccessibleArgumentNames(sender, this.arguments), this.plugin.getWarpManager().getWarpNames()));
        } else {
            CommandArgument argument = CommandWrapper.matchArgument(args[0], sender, this.arguments);
            if (argument == null) {
                return Collections.emptyList();
            }
            results2 = argument.onTabComplete(sender, command, label, args);
        }
        return results2 == null ? null : BukkitUtils.getCompletions(args, results2);
    }

    private boolean handleWarp(CommandSender sender, String[] args) {
        long warpDelayTicks;
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "Only players can teleport to warps.");
            return true;
        }
        final Warp warp = this.plugin.getWarpManager().getWarp(args[0]);
        if (warp == null) {
            sender.sendMessage((Object)ChatColor.RED + "Server warp '" + (Object)ChatColor.GRAY + args[0] + (Object)ChatColor.RED + "' not found.");
            return true;
        }
        if (!sender.hasPermission(warp.getPermission())) {
            sender.sendMessage((Object)ChatColor.RED + "Server warp '" + (Object)ChatColor.GRAY + args[0] + (Object)ChatColor.RED + "' not found.");
            return false;
        }
        final Player player = (Player)sender;
        long l = warpDelayTicks = player.hasPermission(String.valueOf(warp.getPermission()) + ".bypass") ? 0 : this.plugin.getWarpManager().getWarpDelayTicks();
        if (warpDelayTicks <= 0) {
            this.warpPlayer(player, warp);
            return false;
        }
        BukkitRunnable runnable = new BukkitRunnable(){

            public void run() {
                WarpExecutor.this.warpPlayer(player, warp);
            }
        };
        runnable.runTaskLater((Plugin)this.plugin.getJavaPlugin(), warpDelayTicks);
        this.taskMap.put(player.getUniqueId(), runnable);
        sender.sendMessage((Object)ChatColor.GRAY + "Warping to " + (Object)ChatColor.BLUE + warp.getName() + (Object)ChatColor.GRAY + " you will teleport in " + (Object)ChatColor.BLUE + this.plugin.getWarpManager().getWarpDelayWords() + (Object)ChatColor.GRAY + '.');
        return true;
    }

    private void warpPlayer(Player player, Warp warp) {
        BukkitRunnable runnable = this.taskMap.remove(player.getUniqueId());
        if (runnable != null) {
            runnable.cancel();
        }
        if (player.teleport(warp.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND)) {
            player.sendMessage((Object)ChatColor.GRAY + "Warped to " + warp.getName() + '.');
        }
    }

}


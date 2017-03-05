/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.plugin.Plugin
 */
package me.esshd.api.main.cmds.modules.teleport;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import me.esshd.api.main.BaseConstants;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.main.user.BaseUser;
import me.esshd.api.main.user.UserManager;
import me.esshd.api.utils.BukkitUtils;
import me.esshd.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class BackCommand
extends BaseCommand
implements Listener {
    private final BasePlugin plugin;

    public BackCommand(BasePlugin plugin) {
        super("back", "Go to a players last known location.");
        this.setUsage("/(command) [player]");
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)HCF.getPlugin());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BaseUser targetUser;
        Location previous;
        Player target;
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length > 0 && sender.hasPermission(String.valueOf(String.valueOf(command.getPermission())) + ".others")) {
            target = BukkitUtils.playerWithNameOrUUID(args[0]);
            if (target == null) {
                sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
                return true;
            }
        } else {
            target = (Player)sender;
        }
        if ((previous = (targetUser = this.plugin.getUserManager().getUser(target.getUniqueId())).getBackLocation()) == null) {
            sender.sendMessage((Object)ChatColor.RED + target.getName() + " doesn't have a back location.");
            return true;
        }
        ((Player)sender).teleport(previous);
        sender.sendMessage((Object)ChatColor.YELLOW + "Teleported to back location of " + target.getName() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.emptyList();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.plugin.getUserManager().getUser(player.getUniqueId()).setBackLocation(player.getLocation().clone());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            this.plugin.getUserManager().getUser(event.getPlayer().getUniqueId()).setBackLocation(event.getFrom().clone());
        }
    }
}


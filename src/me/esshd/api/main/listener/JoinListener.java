/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package me.esshd.api.main.listener;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.user.BaseUser;
import me.esshd.api.main.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener
implements Listener {
    private final BasePlugin plugin;

    public JoinListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        BaseUser baseUser = this.plugin.getUserManager().getUser(uuid);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("command.staffchat")) continue;
            if (!baseUser.getNotes().isEmpty()) {
                p.sendMessage((Object)ChatColor.YELLOW + player.getName() + (Object)ChatColor.BOLD + " has the following notes" + (Object)ChatColor.RED + '\u2193');
            }
            for (String notes : baseUser.getNotes()) {
                p.sendMessage(notes);
            }
        }
        Integer value = baseUser.getAddressHistories().size();
        baseUser.tryLoggingName(player);
        baseUser.tryLoggingAddress(player.getAddress().getAddress().getHostAddress());
    }
}


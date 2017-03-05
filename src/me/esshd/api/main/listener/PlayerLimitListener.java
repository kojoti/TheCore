/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 */
package me.esshd.api.main.listener;

import java.io.PrintStream;
import java.util.Collection;
import me.esshd.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLimitListener
implements Listener {
    private static final String BYPASS_FULL_JOIN = "base.serverfull.bypass";

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        System.out.println("Players Online: " + Bukkit.getServer().getOnlinePlayers().size());
        System.out.println("Max Players: " + HCF.getPlugin().getConfig().getInt("maxOnline"));
        System.out.println("Permission: " + event.getPlayer().hasPermission("base.serverfull.bypass"));
        if (Bukkit.getServer().getOnlinePlayers().size() >= HCF.getPlugin().getConfig().getInt("maxOnline") && !event.getPlayer().hasPermission("base.serverfull.bypass")) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, (Object)ChatColor.YELLOW + "The server is full. Purchase a rank at " + (Object)ChatColor.RED + "store.donatelink.com " + (Object)ChatColor.YELLOW + "to bypass this.");
        }
    }
}


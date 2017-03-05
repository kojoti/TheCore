/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.SignChangeEvent
 */
package me.esshd.api.main.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ColouredSignListener
implements Listener {
    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player != null && player.hasPermission("base.sign.colour")) {
            String[] lines = event.getLines();
            int i = 0;
            while (i < lines.length) {
                if (!player.hasPermission("base.sign.admin") && (event.getLine(i).contains(ChatColor.translateAlternateColorCodes((char)'&', (String)"Sell")) || event.getLine(i).contains("Buy") || event.getLine(i).contains("Kit"))) {
                    player.sendMessage((Object)ChatColor.RED + "You have used a sign that you're not allowed.");
                    event.setCancelled(true);
                }
                event.setLine(i, ChatColor.translateAlternateColorCodes((char)'&', (String)lines[i]));
                ++i;
            }
        }
    }
}


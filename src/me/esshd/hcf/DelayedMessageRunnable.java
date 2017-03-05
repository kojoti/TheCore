/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.scheduler.BukkitRunnable
 */
package me.esshd.hcf;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayedMessageRunnable
extends BukkitRunnable {
    private final Player player;
    private final String message;

    public DelayedMessageRunnable(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    public void run() {
        this.player.sendMessage(this.message);
    }
}


/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.scheduler.BukkitRunnable
 */
package me.esshd.api.main.task;

import java.util.Collections;
import java.util.List;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.ServerHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class AnnouncementHandler
extends BukkitRunnable {
    private final BasePlugin plugin;

    public AnnouncementHandler(BasePlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        List<String> announcements = this.plugin.getServerHandler().getAnnouncements();
        if (!announcements.isEmpty()) {
            String next = announcements.get(0);
            Bukkit.broadcastMessage((String)next);
            Collections.rotate(announcements, -1);
        }
    }
}


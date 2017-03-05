package me.esshd.api.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayedMessageRunnable extends BukkitRunnable {
    private final Player player;
    private final String message;

    public DelayedMessageRunnable(final JavaPlugin plugin, final Player player, final String message) {
        this.player = player;
        this.message = message;
        this.runTask((Plugin) plugin);
    }

    public void run() {
        this.player.sendMessage(this.message);
    }
}

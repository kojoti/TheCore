/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerPortalEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 */
package me.esshd.hcfold;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EndListener
implements Listener {
    private final Location endExitLocation = new Location(Bukkit.getWorld((String)"world"), 0.0, 70.0, 200.0);

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
                event.setTo(event.getTo().getWorld().getSpawnLocation().clone().add(0.5, 0.0, 0.5));
            } else if (event.getFrom().getWorld().getEnvironment() == World.Environment.THE_END) {
                event.setTo(this.endExitLocation);
            }
        }
    }
}


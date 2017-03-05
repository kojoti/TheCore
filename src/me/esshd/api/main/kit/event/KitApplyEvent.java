/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.player.PlayerEvent
 */
package me.esshd.api.main.kit.event;

import me.esshd.api.main.kit.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class KitApplyEvent
extends PlayerEvent
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Kit kit;
    private final boolean force;
    private boolean cancelled = false;

    public KitApplyEvent(Kit kit, Player player, boolean force) {
        super(player);
        this.kit = kit;
        this.force = force;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Kit getKit() {
        return this.kit;
    }

    public boolean isForce() {
        return this.force;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}


/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.player.PlayerEvent
 */
package me.esshd.hcf.pvpclass.event;

import me.esshd.hcf.pvpclass.PvpClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PvpClassEquipEvent
extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final PvpClass pvpClass;

    public PvpClassEquipEvent(Player player, PvpClass pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }

    public PvpClass getPvpClass() {
        return this.pvpClass;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}


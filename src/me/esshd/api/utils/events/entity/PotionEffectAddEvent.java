package me.esshd.api.utils.events.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;

public class PotionEffectAddEvent
        extends PotionEffectEvent
        implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final EffectCause cause;
    private boolean cancelled;

    public PotionEffectAddEvent(LivingEntity entity, PotionEffect effect, EffectCause cause) {
        super(entity, effect);
        this.cause = cause;
    }

    public EffectCause getCause() {
        return this.cause;
    }

    public static enum EffectCause {
        BEACON, PLUGIN, SPLASH_POTION, WITHER_SKELETON, WITHER_SKULL, UNKNOWN;

        private EffectCause() {
        }
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

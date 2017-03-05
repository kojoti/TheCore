/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package me.esshd.hcf.pvpclass;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class PvpClass {
    public static final long DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8);
    protected final Set<PotionEffect> passiveEffects = new HashSet<PotionEffect>();
    protected final String name;
    protected final long warmupDelay;

    public PvpClass(String name, long warmupDelay) {
        this.name = name;
        this.warmupDelay = warmupDelay;
    }

    public String getName() {
        return this.name;
    }

    public long getWarmupDelay() {
        return this.warmupDelay;
    }

    public boolean onEquip(Player player) {
        for (PotionEffect effect : this.passiveEffects) {
            player.addPotionEffect(effect, true);
        }
        player.sendMessage((Object)ChatColor.GOLD + this.name + (Object)ChatColor.YELLOW + " class " + (Object)ChatColor.YELLOW + "has been activated.");
        return true;
    }

    public void onUnequip(Player player) {
        block0 : for (PotionEffect effect : this.passiveEffects) {
            for (PotionEffect active : player.getActivePotionEffects()) {
                if ((long)active.getDuration() <= DEFAULT_MAX_DURATION || !active.getType().equals((Object)effect.getType()) || active.getAmplifier() != effect.getAmplifier()) continue;
                player.removePotionEffect(effect.getType());
                continue block0;
            }
        }
        player.sendMessage((Object)ChatColor.GOLD + this.name + " Class " + (Object)ChatColor.YELLOW + "has been deactivated.");
    }

    public abstract boolean isApplicableFor(Player var1);
}


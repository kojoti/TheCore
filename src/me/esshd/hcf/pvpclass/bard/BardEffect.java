/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.potion.PotionEffect
 */
package me.esshd.hcf.pvpclass.bard;

import org.bukkit.potion.PotionEffect;

public class BardEffect {
    public final int energyCost;
    public final PotionEffect clickable;
    public final PotionEffect heldable;

    public BardEffect(int energyCost, PotionEffect clickable, PotionEffect heldable) {
        this.energyCost = energyCost;
        this.clickable = clickable;
        this.heldable = heldable;
    }
}


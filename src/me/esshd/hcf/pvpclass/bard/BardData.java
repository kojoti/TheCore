/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.scheduler.BukkitTask
 */
package me.esshd.hcf.pvpclass.bard;

import com.google.common.base.Preconditions;
import org.bukkit.scheduler.BukkitTask;

public class BardData {
    public static final double ENERGY_PER_MILLISECOND = 0.95;
    public static final double MIN_ENERGY = 0.0;
    public static final double MAX_ENERGY = 100.0;
    public static final long MAX_ENERGY_MILLIS = 100000;
    private long buffCooldown;
    protected BukkitTask heldTask;
    private long energyStart;

    public void setBuffCooldown(long millis) {
        this.buffCooldown = System.currentTimeMillis() + millis;
    }

    public long getRemainingBuffDelay() {
        return this.buffCooldown - System.currentTimeMillis();
    }

    public void startEnergyTracking() {
        this.setEnergy(0.0);
    }

    public long getEnergyMillis() {
        if (this.energyStart == 0) {
            return 0;
        }
        return Math.min(100000, (long)(0.95 * (double)(System.currentTimeMillis() - this.energyStart)));
    }

    public double getEnergy() {
        return (double)Math.round((double)this.getEnergyMillis() / 100.0) / 10.0;
    }

    public void setEnergy(double energy) {
        Preconditions.checkArgument((boolean)(energy >= 0.0), (Object)"Energy cannot be less than 0.0");
        Preconditions.checkArgument((boolean)(energy <= 100.0), (Object)"Energy cannot be more than 100.0");
        this.energyStart = (long)((double)System.currentTimeMillis() - 1000.0 * energy);
    }

    public long getBuffCooldown() {
        return this.buffCooldown;
    }

    public BukkitTask getHeldTask() {
        return this.heldTask;
    }
}


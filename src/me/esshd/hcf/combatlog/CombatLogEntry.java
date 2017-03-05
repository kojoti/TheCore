/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.scheduler.BukkitTask
 */
package me.esshd.hcf.combatlog;

import me.esshd.hcf.combatlog.LoggerEntity;
import org.bukkit.scheduler.BukkitTask;

public class CombatLogEntry {
    public final LoggerEntity loggerEntity;
    public final BukkitTask task;

    public CombatLogEntry(LoggerEntity loggerEntity, BukkitTask task) {
        this.loggerEntity = loggerEntity;
        this.task = task;
    }
}


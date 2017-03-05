/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.esshd.hcf.scoreboard;

import java.util.List;
import me.esshd.hcf.scoreboard.SidebarEntry;
import org.bukkit.entity.Player;

public interface SidebarProvider {
    public String getTitle();

    public List<SidebarEntry> getLines(Player var1);
}


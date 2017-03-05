/*
 * Decompiled with CFR 0_119.
 */
package me.esshd.api.main.warp;

import java.util.Collection;
import me.esshd.api.main.warp.Warp;

public interface WarpManager {
    public Collection<String> getWarpNames();

    public Collection<Warp> getWarps();

    public Warp getWarp(String var1);

    public boolean containsWarp(Warp var1);

    public void createWarp(Warp var1);

    public void removeWarp(Warp var1);

    public String getWarpDelayWords();

    public long getWarpDelayMillis();

    public long getWarpDelayTicks();

    public void reloadWarpData();

    public void saveWarpData();
}


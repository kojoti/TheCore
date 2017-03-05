/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.time.DurationFormatUtils
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.esshd.api.main.warp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import me.esshd.api.main.warp.Warp;
import me.esshd.api.main.warp.WarpManager;
import me.esshd.api.utils.Config;
import me.esshd.api.utils.GenericUtils;
import me.esshd.hcf.HCF;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FlatFileWarpManager
implements WarpManager {
    private String warpDelayWords;
    private long warpDelayMillis;
    private long warpDelayTicks;
    private Map<String, Warp> warpNameMap;
    private final JavaPlugin plugin;
    private Config config;
    private List<Warp> warp = new ArrayList<Warp>();

    public FlatFileWarpManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.reloadWarpData();
    }

    @Override
    public Collection<String> getWarpNames() {
        return this.warpNameMap.keySet();
    }

    @Override
    public Collection<Warp> getWarps() {
        return this.warpNameMap.values();
    }

    @Override
    public Warp getWarp(String warpName) {
        return this.warpNameMap.get(warpName);
    }

    @Override
    public boolean containsWarp(Warp warp) {
        return this.warp.contains(warp);
    }

    @Override
    public void createWarp(Warp warp) {
        if (this.warp.add(warp)) {
            this.warpNameMap.put(warp.getName(), warp);
        }
    }

    @Override
    public void removeWarp(Warp warp) {
        if (this.warp.remove(warp)) {
            this.warpNameMap.remove(warp.getName());
        }
    }

    @Override
    public String getWarpDelayWords() {
        return this.warpDelayWords;
    }

    @Override
    public long getWarpDelayMillis() {
        return this.warpDelayMillis;
    }

    @Override
    public long getWarpDelayTicks() {
        return this.warpDelayTicks;
    }

    @Override
    public void reloadWarpData() {
        this.config = new Config(HCF.getPlugin(), "warps");
        Object object = this.config.get("warp");
        if (object instanceof List) {
            this.warp = GenericUtils.createList(object, Warp.class);
            this.warpNameMap = new CaseInsensitiveMap<String, Warp>();
            for (Warp warp : this.warp) {
                this.warpNameMap.put(warp.getName(), warp);
            }
        }
        this.warpDelayMillis = TimeUnit.SECONDS.toMillis(2);
        this.warpDelayTicks = this.warpDelayMillis / 50;
        this.warpDelayWords = DurationFormatUtils.formatDurationWords((long)this.warpDelayMillis, (boolean)true, (boolean)true);
    }

    @Override
    public void saveWarpData() {
        this.plugin.getConfig().set("warp-delay-millis", (Object)this.warpDelayMillis);
        this.plugin.saveConfig();
        this.config.set("warp", this.warp);
        this.config.save();
    }
}


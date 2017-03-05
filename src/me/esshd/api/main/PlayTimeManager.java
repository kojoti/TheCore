/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.gnu.trove.map.TObjectLongMap
 *  net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap
 *  net.minecraft.util.gnu.trove.procedure.TObjectLongProcedure
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.MemorySection
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package me.esshd.api.main;

import java.util.Set;
import java.util.UUID;

import me.esshd.api.utils.Config;
import me.esshd.hcf.HCF;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.util.gnu.trove.procedure.TObjectLongProcedure;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayTimeManager
implements Listener {
    private final TObjectLongMap<UUID> totalPlaytimeMap = new TObjectLongHashMap();
    private final TObjectLongMap<UUID> sessionTimestamps = new TObjectLongHashMap();
    private final Config config;

    public PlayTimeManager(HCF plugin) {
        this.config = new Config(plugin, "play-times");
        this.reloadPlaytimeData();
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        this.sessionTimestamps.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        this.totalPlaytimeMap.put(uuid, this.getTotalPlayTime(uuid));
        this.sessionTimestamps.remove((Object)uuid);
    }

    public void reloadPlaytimeData() {
        Object object = this.config.get("playing-times");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection)object;
            for (String id : section.getKeys(false)) {
                this.totalPlaytimeMap.put(UUID.fromString(id), this.config.getLong("playing-times." + id, 0));
            }
        }
        long millis = System.currentTimeMillis();
        for (Player target : Bukkit.getOnlinePlayers()) {
            this.sessionTimestamps.put(target.getUniqueId(), millis);
        }
    }

    public void savePlaytimeData() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.totalPlaytimeMap.put(p.getUniqueId(), this.getTotalPlayTime(p.getUniqueId()));
        }
        this.totalPlaytimeMap.forEachEntry((uuid, l) -> {
            this.config.set("playing-times." + uuid.toString(), (Object)l);
            return true;
        }
        );
        this.config.save();
    }

    public long getSessionPlayTime(UUID uuid) {
        long session = this.sessionTimestamps.get((Object)uuid);
        return session != this.sessionTimestamps.getNoEntryValue() ? System.currentTimeMillis() - session : 0;
    }

    public long getPreviousPlayTime(UUID uuid) {
        long stamp = this.totalPlaytimeMap.get((Object)uuid);
        return stamp == this.totalPlaytimeMap.getNoEntryValue() ? 0 : stamp;
    }

    public long getTotalPlayTime(UUID uuid) {
        return this.getSessionPlayTime(uuid) + this.getPreviousPlayTime(uuid);
    }
}


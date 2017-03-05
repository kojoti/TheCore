/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 */
package me.esshd.hcf.pvpclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import me.esshd.hcf.HCF;
import me.esshd.hcf.pvpclass.PvpClass;
import me.esshd.hcf.pvpclass.archer.ArcherClass;
import me.esshd.hcf.pvpclass.bard.BardClass;
import me.esshd.hcf.pvpclass.event.PvpClassEquipEvent;
import me.esshd.hcf.pvpclass.event.PvpClassUnequipEvent;
import me.esshd.hcf.pvpclass.type.MinerClass;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PvpClassManager
implements Listener {
    private final Map<UUID, PvpClass> equippedClassMap = new HashMap<UUID, PvpClass>();
    private final List<PvpClass> pvpClasses = new ArrayList<PvpClass>();

    public PvpClassManager(HCF plugin) {
        this.pvpClasses.add(new ArcherClass(plugin));
        this.pvpClasses.add(new BardClass(plugin));
        this.pvpClasses.add(new MinerClass(plugin));
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        for (PvpClass pvpClass : this.pvpClasses) {
            if (!(pvpClass instanceof Listener)) continue;
            plugin.getServer().getPluginManager().registerEvents((Listener)pvpClass, (Plugin)plugin);
        }
    }

    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.setEquippedClass(p, null);
        }
        this.pvpClasses.clear();
        this.equippedClassMap.clear();
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.setEquippedClass(event.getEntity(), null);
    }

    public Collection<PvpClass> getPvpClasses() {
        return this.pvpClasses;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PvpClass getEquippedClass(Player player) {
        Map<UUID, PvpClass> map = this.equippedClassMap;
        synchronized (map) {
            return this.equippedClassMap.get(player.getUniqueId());
        }
    }

    public boolean hasClassEquipped(Player player, PvpClass pvpClass) {
        if (this.getEquippedClass(player) == pvpClass) {
            return true;
        }
        return false;
    }

    public void setEquippedClass(Player player, @Nullable PvpClass pvpClass) {
        if (pvpClass == null) {
            PvpClass equipped = this.equippedClassMap.remove(player.getUniqueId());
            if (equipped != null) {
                equipped.onUnequip(player);
                Bukkit.getPluginManager().callEvent((Event)new PvpClassUnequipEvent(player, equipped));
            }
        } else if (pvpClass.onEquip(player) && pvpClass != this.getEquippedClass(player)) {
            this.equippedClassMap.put(player.getUniqueId(), pvpClass);
            Bukkit.getPluginManager().callEvent((Event)new PvpClassEquipEvent(player, pvpClass));
        }
    }
}


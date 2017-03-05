/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Tameable
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.scheduler.BukkitRunnable
 */
package me.esshd.api.main.task;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearEntityHandler
extends BukkitRunnable {
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            Chunk[] arrchunk = world.getLoadedChunks();
            int n = arrchunk.length;
            int n2 = 0;
            while (n2 < n) {
                Chunk chunk = arrchunk[n2];
                Entity[] arrentity = chunk.getEntities();
                int n3 = arrentity.length;
                int n4 = 0;
                while (n4 < n3) {
                    Item item;
                    ItemStack itemStack;
                    Material material;
                    Entity entity = arrentity[n4];
                    if (!(entity.getType() != EntityType.DROPPED_ITEM && entity.getType() != EntityType.SKELETON && entity.getType() != EntityType.ZOMBIE && entity.getType() != EntityType.ARROW && entity.getType() != EntityType.SPIDER && entity.getType() != EntityType.CREEPER && entity.getType() != EntityType.COW && entity.getType() != EntityType.ENDERMAN && entity.getType() != EntityType.SILVERFISH && entity.getType() != EntityType.MAGMA_CUBE && entity.getType() != EntityType.IRON_GOLEM && entity.getType() != EntityType.GHAST || entity instanceof Tameable && ((Tameable)entity).isTamed() || entity.getType() == EntityType.DROPPED_ITEM && entity instanceof Item && ((material = (itemStack = (item = (Item)entity).getItemStack()).getType()) == Material.SAND || material == Material.TNT || material == Material.DIAMOND || material == Material.EMERALD || material == Material.DIAMOND_ORE || material == Material.GOLD_ORE || material == Material.GOLD_INGOT || material == Material.IRON_INGOT || material == Material.IRON_ORE || material == Material.MOB_SPAWNER || material == Material.HOPPER || material == Material.BEACON))) {
                        entity.remove();
                    }
                    ++n4;
                }
                ++n2;
            }
        }
    }
}


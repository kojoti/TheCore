/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package me.esshd.hcf.pvpclass.type;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import me.esshd.api.utils.BukkitUtils;
import me.esshd.hcf.HCF;
import me.esshd.hcf.pvpclass.PvpClass;
import me.esshd.hcf.pvpclass.PvpClassManager;
import me.esshd.hcf.pvpclass.event.PvpClassEquipEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MinerClass
extends PvpClass
implements Listener {
    private static final int INVISIBILITY_HEIGHT_LEVEL = 30;
    private static final PotionEffect HEIGHT_INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
    private static final PotionEffect HASTE_EFFECT = new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 2);
    private static final PotionEffect SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1);
    private static final PotionEffect RES_EFFECT = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1);
    private final HCF plugin;

    public MinerClass(HCF plugin) {
        super("Miner", TimeUnit.SECONDS.toMillis(10));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
    }

    private void removeInvisibilitySafely(Player player) {
        for (PotionEffect active : player.getActivePotionEffects()) {
            if (!active.getType().equals((Object)PotionEffectType.INVISIBILITY) || (long)active.getDuration() <= DEFAULT_MAX_DURATION) continue;
            player.sendMessage((Object)ChatColor.RED + this.getName() + (Object)ChatColor.GRAY + " invisibility removed.");
            player.removePotionEffect(active.getType());
            break;
        }
    }

    private void removeHasteSafely(Player player) {
        for (PotionEffect active : player.getActivePotionEffects()) {
            if (!active.getType().equals((Object)PotionEffectType.DAMAGE_RESISTANCE) || (long)active.getDuration() <= DEFAULT_MAX_DURATION) continue;
            player.removePotionEffect(active.getType());
            break;
        }
    }

    private void removeResSafely(Player player) {
        for (PotionEffect active : player.getActivePotionEffects()) {
            if (!active.getType().equals((Object)PotionEffectType.FAST_DIGGING) || (long)active.getDuration() <= DEFAULT_MAX_DURATION) continue;
            player.removePotionEffect(active.getType());
            break;
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player && BukkitUtils.getFinalAttacker((EntityDamageEvent)event, false) != null) {
            Player player = (Player)entity;
            if (this.plugin.getPvpClassManager().hasClassEquipped(player, this)) {
                this.removeInvisibilitySafely(player);
            }
        }
    }

    @Override
    public void onUnequip(Player player) {
        super.onUnequip(player);
        this.removeInvisibilitySafely(player);
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onClassEquip(PvpClassEquipEvent event) {
        Player player = event.getPlayer();
        if (event.getPvpClass() == this && player.getLocation().getBlockY() <= 30) {
            player.addPotionEffect(HEIGHT_INVISIBILITY, true);
            player.sendMessage((Object)ChatColor.AQUA + this.getName() + (Object)ChatColor.GRAY + " invisibility added.");
        }
    }

    private void conformMinerInvisibility(Player player, Location from, Location to) {
        int toY;
        int fromY = from.getBlockY();
        if (fromY != (toY = to.getBlockY()) && this.plugin.getPvpClassManager().hasClassEquipped(player, this)) {
            boolean isInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);
            if (toY > 30) {
                if (fromY <= 30 && isInvisible) {
                    this.removeInvisibilitySafely(player);
                }
            } else if (!isInvisible) {
                player.addPotionEffect(HEIGHT_INVISIBILITY, true);
                player.sendMessage((Object)ChatColor.AQUA + this.getName() + (Object)ChatColor.GRAY + " invisibility added.");
            }
        }
    }

    @Override
    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.IRON_HELMET) {
            return false;
        }
        ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.IRON_CHESTPLATE) {
            return false;
        }
        ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.IRON_LEGGINGS) {
            return false;
        }
        ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.IRON_BOOTS;
    }
}


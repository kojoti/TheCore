/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package me.esshd.hcf.pvpclass.type;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.HCF;
import me.esshd.hcf.pvpclass.PvpClass;
import me.esshd.hcf.pvpclass.PvpClassManager;
import me.esshd.hcf.pvpclass.event.PvpClassEquipEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RogueClass
extends PvpClass
implements Listener {
    private final HCF plugin;

    public RogueClass(HCF plugin) {
        super("Rogue", TimeUnit.SECONDS.toMillis(5));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Player) {
            ItemStack stack;
            Player attacker = (Player)damager;
            if (this.plugin.getPvpClassManager().getEquippedClass(attacker) == this && (stack = attacker.getItemInHand()) != null && stack.getType() == Material.GOLD_SWORD && stack.getEnchantments().isEmpty()) {
                Player player = (Player)entity;
                player.sendMessage((Object)ChatColor.GOLD + "You have been " + (Object)ChatColor.RED + "backstabbed" + (Object)ChatColor.GOLD + " by " + (Object)ConfigurationService.ENEMY_COLOUR + attacker.getName() + (Object)ChatColor.GOLD + ".");
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                attacker.sendMessage((Object)ChatColor.GOLD + "You have backstabbed " + (Object)ConfigurationService.ENEMY_COLOUR + player.getName() + (Object)ChatColor.GOLD + '.');
                attacker.setItemInHand(new ItemStack(Material.AIR, 1));
                attacker.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                event.setDamage(6.25);
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onClassEquip(PvpClassEquipEvent event) {
        Player player = event.getPlayer();
    }

    @Override
    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.CHAINMAIL_HELMET) {
            return false;
        }
        ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.CHAINMAIL_CHESTPLATE) {
            return false;
        }
        ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.CHAINMAIL_LEGGINGS) {
            return false;
        }
        ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.CHAINMAIL_BOOTS;
    }
}


/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package me.esshd.hcf.pvpclass.type;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.esshd.hcf.HCF;
import me.esshd.hcf.pvpclass.PvpClass;
import me.esshd.hcf.pvpclass.PvpClassManager;
import me.esshd.hcf.pvpclass.bard.EffectRestorer;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AssassinClass
extends PvpClass
implements Listener {
    private final TObjectLongMap<UUID> cooldowns = new TObjectLongHashMap<UUID>();
    private final HCF plugin;

    public AssassinClass(HCF plugin) {
        super("Assassin", TimeUnit.SECONDS.toMillis(10));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem() && event.getItem().getType() == Material.GOLDEN_CARROT) {
            Player player = event.getPlayer();
            if (this.plugin.getPvpClassManager().getEquippedClass(player) == this) {
                long remaining;
                long timestamp = this.cooldowns.get(player.getUniqueId());
                long millis = System.currentTimeMillis();
                long l = remaining = timestamp == this.cooldowns.getNoEntryValue() ? 0 : timestamp - millis;
                if (remaining > 0) {
                    player.sendMessage((Object)ChatColor.RED + "Cooldown still for " + DurationFormatUtils.formatDurationWords((long)remaining, (boolean)true, (boolean)true) + ".");
                    return;
                }
                this.cooldowns.put(player.getUniqueId(), millis + 15000);
                this.plugin.getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.SPEED, 100, 4));
                this.plugin.getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0));
            }
        }
    }

    @Override
    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.IRON_HELMET || !helmet.getEnchantments().isEmpty()) {
            return false;
        }
        ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.IRON_CHESTPLATE || !chestplate.getEnchantments().isEmpty()) {
            return false;
        }
        ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.IRON_LEGGINGS || !leggings.getEnchantments().isEmpty()) {
            return false;
        }
        ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.IRON_BOOTS && boots.getEnchantments().isEmpty();
    }
}


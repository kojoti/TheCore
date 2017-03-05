package me.esshd.hcf.eventgame.crate.types;



import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import me.esshd.api.utils.ItemBuilder;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.eventgame.crate.EnderChestKey;
import me.esshd.hcf.listener.Crowbar;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

public class KeanuKey extends EnderChestKey
{
    public KeanuKey() {
        super("Keanu", 4);
        this.setupRarity(new ItemStack(Material.ENDER_PEARL, 8), 10);
        this.setupRarity(new ItemStack(Material.GOLDEN_APPLE, 1, (short)1), 4);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 3).enchant(Enchantment.LOOT_BONUS_BLOCKS, 1).build(), 6);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_AXE).enchant(Enchantment.DIG_SPEED, 3).enchant(Enchantment.LOOT_BONUS_BLOCKS, 1).build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SPADE).enchant(Enchantment.DIG_SPEED, 3).build(), 3);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).build(), 8);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 3).build(), 3);
        this.setupRarity(new ItemStack(Material.ENDER_PORTAL_FRAME, 1), 3);
        this.setupRarity(new ItemStack(Material.EXP_BOTTLE, 24), 7);
        this.setupRarity(new Crowbar().getItemIfPresent(), 1);
        this.setupRarity(new ItemStack(Material.BEACON, 1), 1);
        this.setupRarity(new ItemBuilder(Material.MOB_SPAWNER).displayName(ChatColor.GREEN + "Spawner").loreLine(ChatColor.WHITE + WordUtils.capitalizeFully(EntityType.PIG.name())).build(), 3);
        this.setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 3), 24);
        this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 4), 24);
        this.setupRarity(new ItemBuilder(Material.GOLD_CHESTPLATE).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Chestplate").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Leggings").build(), 1);
        this.setupRarity(new ItemStack(Material.IRON_BLOCK, 2), 24);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).build(), 2);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).build(), 2);
        this.setupRarity(new ItemBuilder(Material.SKULL_ITEM).data((short)1).build(), 3);
        this.setupRarity(new ItemBuilder(Material.GHAST_TEAR).build(), 3);
        this.setupRarity(new ItemStack(Material.QUARTZ, 3), 3);
        this.setupRarity(new ItemStack(Material.MAGMA_CREAM, 5), 3);
        this.setupRarity(new ItemBuilder(Material.MONSTER_EGG).data((short)92).build(), 3);
        this.setupRarity(new ItemBuilder(Material.POTION).data((short)16421).build(), 1);
    }
    
    @Override
    public ChatColor getColour() {
        return ChatColor.AQUA;
    }
    
    @Override
    public boolean getBroadcastItems() {
        return false;
    }
}

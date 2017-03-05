package me.esshd.hcf.eventgame.crate.types;

import me.esshd.api.utils.ItemBuilder;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.eventgame.crate.EnderChestKey;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class TropicalKey extends EnderChestKey
{
    public TropicalKey() {
        super("Tropical", 4);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).enchant(Enchantment.DURABILITY, 3).build(), 10);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).enchant(Enchantment.DURABILITY, 3).build(), 10);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).enchant(Enchantment.DURABILITY, 3).build(), 10);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).build(), 10);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.LOOT_BONUS_MOBS, 3).enchant(Enchantment.DAMAGE_ALL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).build(), 10);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BLOCK, 28).build(), 10);
        this.setupRarity(new ItemBuilder(Material.GOLD_BLOCK, 28).build(), 10);
        this.setupRarity(new ItemBuilder(Material.IRON_BLOCK, 28).build(), 10);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 5).enchant(Enchantment.LOOT_BONUS_BLOCKS, 3).enchant(Enchantment.DURABILITY, 3).build(), 10);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 5).enchant(Enchantment.SILK_TOUCH, 1).enchant(Enchantment.DURABILITY, 3).build(), 10);
    }
    
    @Override
    public ChatColor getColour() {
        return ChatColor.RED;
    }
}

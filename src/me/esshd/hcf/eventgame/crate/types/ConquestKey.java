package me.esshd.hcf.eventgame.crate.types;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import me.esshd.api.utils.ItemBuilder;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.eventgame.crate.EnderChestKey;

public class ConquestKey extends EnderChestKey
{
    public ConquestKey() {
        super("Conquest", 7);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.FIRE_ASPECT, 2).enchant(Enchantment.DAMAGE_ALL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).lore(new String[] { ChatColor.RED + "unrepairable" }).displayName(ChatColor.DARK_PURPLE + "Conquest Fire").build(), 3);
        this.setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 24), 15);
        this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 24), 15);
        this.setupRarity(new ItemStack(Material.IRON_BLOCK, 24), 15);
        this.setupRarity(new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Helmet").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_CHESTPLATE).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Chestplate").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Leggings").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Boots").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.LOOT_BONUS_MOBS, 5).displayName(ChatColor.DARK_PURPLE + "Conquest Looting").lore(new String[] { ChatColor.RED + "unrepairable" }).build(), 6);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 4).lore(new String[] { ChatColor.RED + "unrepairable" }).displayName(ChatColor.DARK_PURPLE + "Conquest Fortune").build(), 4);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.DIG_SPEED, 5).lore(new String[] { ChatColor.RED + "unrepairable" }).displayName(ChatColor.DARK_PURPLE + "Conquest Pickaxe").build(), 4);
        this.setupRarity(new ItemBuilder(Material.SKULL_ITEM, 2).data((short)1).build(), 6);
        this.setupRarity(new ItemStack(Material.BEACON), 2);
        this.setupRarity(new ItemStack(Material.NETHER_STAR), 1);
        this.setupRarity(new ItemBuilder(Material.GOLDEN_APPLE).data((short)1).build(), 3);
        this.setupRarity(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.ARROW_DAMAGE)).enchant(Enchantment.ARROW_FIRE, 1).enchant(Enchantment.ARROW_INFINITE, 1).enchant(Enchantment.DURABILITY, 3).lore(new String[] { ChatColor.RED + "unrepairable" }).displayName(ChatColor.RED + "KOTH Bow").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_PURPLE + "Conquest Helmet").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_PURPLE + "Conquest Chestplate").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_PURPLE + "Conquest Leggings").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_PURPLE + "Conquest Boots").build(), 1);
    }
    
    @Override
    public ChatColor getColour() {
        return ChatColor.DARK_PURPLE;
    }
    
    @Override
    public boolean getBroadcastItems() {
        return true;
    }
}

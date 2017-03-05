package me.esshd.hcf.eventgame.crate.types;



import me.esshd.api.utils.ItemBuilder;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.eventgame.crate.EnderChestKey;
import me.esshd.hcf.listener.Crowbar;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class KothKey extends EnderChestKey
{
    public KothKey() {
        super("Koth", 5); 
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.FIRE_ASPECT, 2).enchant(Enchantment.DAMAGE_ALL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).lore(new String[] { ChatColor.RED + "unrepairable" }).displayName(ChatColor.GREEN + "KOTH Fire").build(), 3);
        this.setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 16), 15);
        this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 16), 15);
        this.setupRarity(new ItemStack(Material.IRON_BLOCK, 16), 15);
        this.setupRarity(new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Helmet").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_CHESTPLATE).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Chestplate").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Leggings").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Boots").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.LOOT_BONUS_MOBS, 5).displayName(ChatColor.RED + "KOTH Looting").lore(new String[] { ChatColor.RED + "unrepairable" }).build(), 6);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 4).lore(new String[] { ChatColor.RED + "unrepairable" }).displayName(ChatColor.RED + "KOTH Fortune").build(), 4);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.DIG_SPEED, 5).lore(new String[] { ChatColor.RED + "unrepairable" }).displayName(ChatColor.RED + "KOTH Pickaxe").build(), 4);
        this.setupRarity(new ItemBuilder(Material.SKULL_ITEM, 2).data((short)1).build(), 6);
        this.setupRarity(new ItemStack(Material.BEACON), 1);
        this.setupRarity(new ItemStack(Material.NETHER_STAR), 1);
        this.setupRarity(new ItemBuilder(Material.GOLDEN_APPLE).data((short)1).build(), 3);
        this.setupRarity(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.ARROW_DAMAGE)).enchant(Enchantment.ARROW_FIRE, 1).enchant(Enchantment.ARROW_INFINITE, 1).enchant(Enchantment.DURABILITY, 3).lore(new String[] { ChatColor.RED + "unrepairable" }).displayName(ChatColor.RED + "KOTH Bow").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "KOTH Helmet").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "KOTH Chestplate").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "KOTH Leggings").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int) ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "KOTH Boots").build(), 1);
    }
    
    @Override
    public ChatColor getColour() {
        return ChatColor.BLUE;
    }
    
    @Override
    public boolean getBroadcastItems() {
        return true;
    }
}

/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.org.apache.commons.lang3.text.WordUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.potion.Potion
 *  org.bukkit.potion.PotionType
 */
package me.esshd.hcfold;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import me.esshd.api.utils.InventoryUtils;
import me.esshd.api.utils.ItemBuilder;
import me.esshd.api.utils.chat.Lang;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.HCF;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class MapKitCommand
implements CommandExecutor,
TabCompleter,
Listener {
    private final Set<Inventory> tracking = new HashSet<Inventory>();

    public MapKitCommand(HCF plugin) {
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (Map.Entry<Enchantment, Integer> entry : ConfigurationService.ENCHANTMENT_LIMITS.entrySet()) {
            items.add(new ItemBuilder(Material.ENCHANTED_BOOK).displayName((Object)ChatColor.YELLOW + Lang.fromEnchantment(entry.getKey()) + ": " + (Object)ChatColor.GREEN + entry.getValue()).build());
        }
        for (Entry<PotionType, Integer> entry : ConfigurationService.POTION_LIMITS.entrySet()) {
            items.add(new ItemBuilder(new Potion((PotionType)entry.getKey()).toItemStack(1)).displayName((Object)ChatColor.YELLOW + WordUtils.capitalizeFully((String)((PotionType)entry.getKey()).name().replace('_', ' ')) + ": " + (Object)ChatColor.GREEN + entry.getValue()).build());
        }
        Player player = (Player)sender;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)player, (int)InventoryUtils.getSafestInventorySize(items.size()), (String)("HCF Map " + ConfigurationService.MAP_NUMBER + " Kit"));
        this.tracking.add(inventory);
        for (ItemStack item : items) {
            inventory.addItem(new ItemStack[]{item});
        }
        player.openInventory(inventory);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (this.tracking.contains((Object)event.getInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        for (Inventory inventory : this.tracking) {
            HashSet<HumanEntity> viewers = new HashSet(inventory.getViewers());
            for (HumanEntity viewer : viewers) {
                viewer.closeInventory();
            }
        }
    }
}


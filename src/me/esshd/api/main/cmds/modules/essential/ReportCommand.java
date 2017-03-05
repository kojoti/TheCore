/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package me.esshd.api.main.cmds.modules.essential;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.esshd.api.main.BaseConstants;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.utils.ItemBuilder;
import me.esshd.api.utils.chat.ClickAction;
import me.esshd.api.utils.chat.Text;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ReportCommand
extends BaseCommand
implements Listener {
    private static final long REPORT_DELAY_MILLIS = TimeUnit.MINUTES.toMillis(3);
    private final Map<UUID, Long> reportMap;
    private final HashSet<String> whoReported = new HashSet();

    public ReportCommand() {
        super("report", "Reports a player");
        this.setUsage("/(command) <player>");
        this.reportMap = Maps.newHashMap();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "Only players may report others.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: /" + label + " <player> ");
            return true;
        }
        Player player = (Player)sender;
        Player target = Bukkit.getPlayer((String)args[0]);
        if (target == null || !player.canSee(target)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }
        if (sender.equals((Object)target)) {
            sender.sendMessage((Object)ChatColor.RED + "You may not report yourself.");
            return true;
        }
        UUID uuid = player.getUniqueId();
        long millis = System.currentTimeMillis();
        Long lastReport = this.reportMap.get(uuid);
        if (lastReport != null && lastReport - millis > 0) {
            sender.sendMessage((Object)ChatColor.RED + "You have already reported someone in the last " + DurationFormatUtils.formatDurationWords((long)REPORT_DELAY_MILLIS, (boolean)true, (boolean)true) + '.');
            return true;
        }
        this.reportMap.put(uuid, millis + REPORT_DELAY_MILLIS);
        this.whoReported.add(sender.getName());
        this.openInv((Player)sender, target);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.emptyList();
    }

    private void openInv(Player p, Player target) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, (int)18, (String)((Object)ChatColor.DARK_AQUA + "Report " + target.getName()));
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName((Object)ChatColor.YELLOW + "Kill Aura");
        item.setItemMeta(itemMeta);
        inv.addItem(new ItemStack[]{item});
        ItemStack item2 = new ItemStack(Material.FEATHER);
        ItemMeta itemMeta2 = item2.getItemMeta();
        itemMeta2.setDisplayName((Object)ChatColor.YELLOW + "Speed");
        item2.setItemMeta(itemMeta2);
        inv.addItem(new ItemStack[]{item2});
        ItemStack item3 = new ItemStack(Material.POTION);
        ItemMeta itemMeta3 = item3.getItemMeta();
        itemMeta3.setDisplayName((Object)ChatColor.YELLOW + "Auto Potion");
        item3.setItemMeta(itemMeta3);
        inv.addItem(new ItemStack[]{item3});
        ItemStack item4 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta itemMeta4 = item4.getItemMeta();
        itemMeta4.setDisplayName((Object)ChatColor.YELLOW + "Fly");
        item4.setItemMeta(itemMeta4);
        inv.addItem(new ItemStack[]{item4});
        ItemStack item5 = new ItemStack(Material.SAND);
        ItemMeta itemMeta5 = item5.getItemMeta();
        itemMeta5.setDisplayName((Object)ChatColor.YELLOW + "Phasing");
        item5.setItemMeta(itemMeta5);
        inv.addItem(new ItemStack[]{item5});
        ItemStack item6 = new ItemStack(Material.BRICK);
        ItemMeta itemMeta6 = item6.getItemMeta();
        itemMeta6.setDisplayName((Object)ChatColor.YELLOW + "Anti Knockback");
        item6.setItemMeta(itemMeta6);
        inv.addItem(new ItemStack[]{item6});
        ItemStack builder = new ItemBuilder(Material.DIAMOND_CHESTPLATE).displayName((Object)ChatColor.YELLOW + "Abusing a Glitch").build();
        inv.addItem(new ItemStack[]{builder});
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getName().contains("Report")) {
            Player p = (Player)e.getWhoClicked();
            e.setCancelled(true);
            p.closeInventory();
            for (Player on : Bukkit.getOnlinePlayers()) {
                if (!on.hasPermission("hcf.report.view")) continue;
                if (Bukkit.getPlayer((String)e.getInventory().getName().replace((Object)ChatColor.DARK_AQUA + "Report ", "")) != null) {
                    new Text((Object)ChatColor.GRAY + "[" + (Object)ChatColor.RED + "Report" + (Object)ChatColor.GRAY + "] " + (Object)ChatColor.RED + e.getInventory().getName().replace(new StringBuilder().append((Object)ChatColor.DARK_RED).append("Report ").toString(), "") + (Object)ChatColor.GRAY + " has been reported for: " + (Object)ChatColor.RED + e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.RED.toString(), "") + (Object)ChatColor.GRAY + (Object)ChatColor.ITALIC + " (Click here)").setHoverText((Object)ChatColor.GRAY + "The reporter: " + (Object)ChatColor.RED + p.getName() + (Object)ChatColor.GRAY + ". Click to teleport to the 'hacker' ").setClick(ClickAction.RUN_COMMAND, "/tp " + Bukkit.getPlayer((String)e.getInventory().getName().replace(new StringBuilder().append((Object)ChatColor.DARK_RED).append("Report ").toString(), "")).getName()).send((CommandSender)on);
                    continue;
                }
                new Text((Object)ChatColor.GRAY + "[" + (Object)ChatColor.RED + "Report" + (Object)ChatColor.GRAY + "] " + (Object)ChatColor.RED + e.getInventory().getName().replace(new StringBuilder().append((Object)ChatColor.DARK_RED).append("Report ").toString(), "") + (Object)ChatColor.GRAY + " has been reported for: " + (Object)ChatColor.RED + e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.RED.toString(), "") + (Object)ChatColor.GRAY + (Object)ChatColor.ITALIC + " (Click here)").setHoverText((Object)ChatColor.GRAY + "The reporter: " + (Object)ChatColor.RED + p.getName()).send((CommandSender)on);
            }
        }
    }

    @EventHandler
    public void ReportClose(InventoryCloseEvent e) {
        if (e.getInventory().getName().contains("Report")) {
            Player p = (Player)e.getPlayer();
            p.sendMessage((Object)ChatColor.GOLD + "We have recieved your report and staff will be on it right away.");
        }
    }
}


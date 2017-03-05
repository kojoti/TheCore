/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerKickEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package me.esshd.hcf.cmds;

import java.util.HashMap;
import java.util.Map;
import me.esshd.hcf.HCF;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EndPortalCommand
implements CommandExecutor,
Listener {
    private HCF mainPlugin;
    private final String ITEM_DISPLAYNAME = (Object)ChatColor.RED + "Endportal Maker";
    private Map<String, LocationPair> playerSelections;

    public EndPortalCommand(HCF plugin) {
        this.mainPlugin = plugin;
        this.playerSelections = new HashMap<String, LocationPair>();
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent e) {
        if (e.hasItem() && e.getClickedBlock() != null) {
            ItemStack itemStack = e.getItem();
            Block b = e.getClickedBlock();
            if (itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(this.ITEM_DISPLAYNAME)) {
                LocationPair locationPair = this.playerSelections.get(e.getPlayer().getName());
                if (locationPair == null) {
                    locationPair = new LocationPair(null, null);
                    this.playerSelections.put(e.getPlayer().getName(), locationPair);
                }
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (b.getType() != Material.ENDER_PORTAL_FRAME) {
                        e.getPlayer().sendMessage((Object)ChatColor.RED + "You must select an end portal frame.");
                        return;
                    }
                    locationPair.setFirstLoc(b.getLocation());
                    e.getPlayer().sendMessage((Object)ChatColor.GREEN + "Successfully set the first location.");
                } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (b.getType() != Material.ENDER_PORTAL_FRAME) {
                        e.getPlayer().sendMessage((Object)ChatColor.RED + "You must select an end portal frame.");
                        return;
                    }
                    if (locationPair.getFirstLoc() == null) {
                        e.getPlayer().sendMessage((Object)ChatColor.RED + "Please set the first location (by left clicking the end portal frame).");
                        return;
                    }
                    locationPair.setSecondLoc(b.getLocation());
                    e.getPlayer().sendMessage((Object)ChatColor.GREEN + "Successfully set the second location.");
                    Location firstLoc = locationPair.getFirstLoc();
                    Location secondLoc = locationPair.getSecondLoc();
                    if (firstLoc.distance(secondLoc) > 6.0) {
                        e.getPlayer().sendMessage((Object)ChatColor.RED + "You cannot create an end portal that big.");
                        return;
                    }
                    if (firstLoc.getBlockY() != secondLoc.getBlockY()) {
                        e.getPlayer().sendMessage((Object)ChatColor.RED + "Make sure that the portals have the same elevation.");
                        return;
                    }
                    int minX = Math.min(firstLoc.getBlockX(), secondLoc.getBlockX());
                    int minY = Math.min(firstLoc.getBlockY(), secondLoc.getBlockY());
                    int minZ = Math.min(firstLoc.getBlockZ(), secondLoc.getBlockZ());
                    int maxX = Math.max(firstLoc.getBlockX(), secondLoc.getBlockX());
                    int maxY = Math.max(firstLoc.getBlockY(), secondLoc.getBlockY());
                    int maxZ = Math.max(firstLoc.getBlockZ(), secondLoc.getBlockZ());
                    int x = minX;
                    while (x <= maxX) {
                        int y = minY;
                        while (y <= maxY) {
                            int z = minZ;
                            while (z <= maxZ) {
                                Block block = b.getWorld().getBlockAt(x, y, z);
                                if (block.isEmpty()) {
                                    block.setType(Material.ENDER_PORTAL);
                                }
                                ++z;
                            }
                            ++y;
                        }
                        ++x;
                    }
                    e.setCancelled(true);
                    new BukkitRunnable(){

                        public void run() {
                            e.getPlayer().setItemInHand(null);
                            e.getPlayer().updateInventory();
                        }
                    }.runTask((Plugin)this.mainPlugin);
                    e.getPlayer().sendMessage((Object)ChatColor.GREEN + "Created an end portal.");
                    this.playerSelections.remove(e.getPlayer().getName());
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack itemStack = e.getItemDrop().getItemStack();
        if (itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(this.ITEM_DISPLAYNAME)) {
            e.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.playerSelections.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        this.playerSelections.remove(e.getPlayer().getName());
    }

    public boolean onCommand(CommandSender s, Command c, String alias, String[] args) {
        if (!s.hasPermission("Core.staff")) {
            s.sendMessage((Object)ChatColor.RED + "No Permission.");
            return true;
        }
        if (!(s instanceof Player)) {
            s.sendMessage((Object)ChatColor.RED + "You must be a player to perform this command.");
            return true;
        }
        Player p = (Player)s;
        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage((Object)ChatColor.RED + "Please clear up your inventory, and then perform this command again.");
            return true;
        }
        ItemStack portalMaker = new ItemStack(Material.GOLD_SWORD);
        ItemMeta itemMeta = portalMaker.getItemMeta();
        itemMeta.setDisplayName(this.ITEM_DISPLAYNAME);
        portalMaker.setItemMeta(itemMeta);
        p.getInventory().addItem(new ItemStack[]{portalMaker});
        p.sendMessage((Object)ChatColor.GREEN + "Select the 2 points.");
        return true;
    }

    private class LocationPair {
        private Location firstLoc;
        private Location secondLoc;

        public LocationPair(Location firstLoc, Location secondLoc) {
            this.firstLoc = firstLoc;
            this.secondLoc = secondLoc;
        }

        public Location getFirstLoc() {
            return this.firstLoc;
        }

        public Location getSecondLoc() {
            return this.secondLoc;
        }

        public void setFirstLoc(Location firstLoc) {
            this.firstLoc = firstLoc;
        }

        public void setSecondLoc(Location secondLoc) {
            this.secondLoc = secondLoc;
        }
    }

}


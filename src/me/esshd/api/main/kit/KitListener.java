/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.time.DurationFormatUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Sign
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package me.esshd.api.main.kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.PlayTimeManager;
import me.esshd.api.main.kit.Kit;
import me.esshd.api.main.kit.KitManager;
import me.esshd.api.main.kit.event.KitApplyEvent;
import me.esshd.api.main.user.BaseUser;
import me.esshd.api.main.user.UserManager;
import me.esshd.api.utils.ParticleEffect;
import me.esshd.api.utils.SignHandler;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitListener
implements Listener {
    private final BasePlugin plugin;
    public static List<Inventory> previewInventory = new ArrayList<Inventory>();

    public KitListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (previewInventory.contains((Object)e.getInventory())) {
            previewInventory.remove((Object)e.getInventory());
            e.getInventory().clear();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (previewInventory.contains((Object)event.getInventory())) {
            event.setCancelled(true);
            return;
        }
        Inventory inventory = event.getInventory();
        if (inventory == null) {
            return;
        }
        String title = inventory.getTitle();
        HumanEntity humanEntity = event.getWhoClicked();
        if (title.contains("Kit Selector") && humanEntity instanceof Player) {
            event.setCancelled(true);
            if (!Objects.equals((Object)event.getView().getTopInventory(), (Object)event.getClickedInventory())) {
                return;
            }
            ItemStack stack = event.getCurrentItem();
            if (stack == null || !stack.hasItemMeta()) {
                return;
            }
            ItemMeta meta = stack.getItemMeta();
            if (!meta.hasDisplayName()) {
                return;
            }
            Player player = (Player)humanEntity;
            String name = ChatColor.stripColor((String)stack.getItemMeta().getDisplayName());
            Kit kit = this.plugin.getKitManager().getKit(name);
            if (kit == null) {
                return;
            }
            kit.applyTo(player, false, true);
        }
    }

    @EventHandler
    public void onKitSign(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            if (!(state instanceof Sign)) {
                return;
            }
            Sign sign = (Sign)state;
            String[] lines = sign.getLines();
            if (lines.length >= 2 && lines[1].contains("Kit")) {
                Kit kit = this.plugin.getKitManager().getKit(lines.length >= 3 ? lines[2] : null);
                if (kit == null) {
                    return;
                }
                event.setCancelled(true);
                Player player = event.getPlayer();
                String[] fakeLines = Arrays.copyOf(sign.getLines(), 4);
                boolean applied = kit.applyTo(player, false, false);
                if (applied) {
                    fakeLines[0] = (Object)ChatColor.GREEN + "Successfully";
                    fakeLines[1] = (Object)ChatColor.GREEN + "equipped kit";
                    fakeLines[2] = kit.getDisplayName();
                    fakeLines[3] = "";
                } else {
                    fakeLines[0] = (Object)ChatColor.RED + "Failed to";
                    fakeLines[1] = (Object)ChatColor.RED + "equip kit";
                    fakeLines[2] = kit.getDisplayName();
                    fakeLines[3] = (Object)ChatColor.RED + "Check chat";
                }
                if (this.plugin.getSignHandler().showLines(player, sign, fakeLines, 100, false) && applied) {
                    ParticleEffect.SPLASH.display(player, sign.getLocation().clone().add(0.5, 0.5, 0.5), 0.01f, 10);
                }
            }
        }
    }

    @EventHandler
    public void onKitApply(KitApplyEvent event) {
        int maxUses;
        if (event.isForce()) {
            return;
        }
        Player player = event.getPlayer();
        Kit kit = event.getKit();
        if (!player.isOp() && !kit.isEnabled()) {
            event.setCancelled(true);
            player.sendMessage((Object)ChatColor.RED + "The " + kit.getDisplayName() + " kit is currently disabled.");
            return;
        }
        String kitPermission = kit.getPermissionNode();
        if (kitPermission != null && !player.hasPermission(kitPermission)) {
            event.setCancelled(true);
            player.sendMessage((Object)ChatColor.RED + "You do not have permission to use this kit.");
            return;
        }
        UUID uuid = player.getUniqueId();
        long minPlaytimeMillis = kit.getMinPlaytimeMillis();
        if (minPlaytimeMillis > 0 && this.plugin.getPlayTimeManager().getTotalPlayTime(uuid) < minPlaytimeMillis) {
            player.sendMessage((Object)ChatColor.RED + "You need at least " + kit.getMinPlaytimeWords() + " minimum playtime to use kit " + kit.getDisplayName() + '.');
            event.setCancelled(true);
            return;
        }
        BaseUser baseUser = this.plugin.getUserManager().getUser(uuid);
        long remaining = baseUser.getRemainingKitCooldown(kit);
        if (remaining > 0) {
            player.sendMessage((Object)ChatColor.RED + "You cannot use the " + kit.getDisplayName() + " kit for " + DurationFormatUtils.formatDurationWords((long)remaining, (boolean)true, (boolean)true) + '.');
            event.setCancelled(true);
            return;
        }
        int curUses = baseUser.getKitUses(kit);
        if (curUses >= (maxUses = kit.getMaximumUses()) && maxUses != Integer.MAX_VALUE) {
            player.sendMessage((Object)ChatColor.RED + "You have already used this kit " + curUses + '/' + maxUses + " times.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onKitApplyMonitor(KitApplyEvent event) {
        if (!event.isForce()) {
            Kit kit = event.getKit();
            BaseUser baseUser = this.plugin.getUserManager().getUser(event.getPlayer().getUniqueId());
            baseUser.incrementKitUses(kit);
            baseUser.updateKitCooldown(kit);
        }
    }
}


/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.util.org.apache.commons.lang3.time.FastDateFormat
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Sign
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.block.SignChangeEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package me.esshd.hcfold;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import me.esshd.hcf.DateTimeFormats;
import net.minecraft.util.org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventSignListener
implements Listener {
    private static final String EVENT_SIGN_ITEM_NAME = (Object)ChatColor.GOLD + "Event Sign";

    public static ItemStack getEventSign(String playerName, String kothName) {
        ItemStack stack = new ItemStack(Material.SIGN, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(EVENT_SIGN_ITEM_NAME);
        meta.setLore((List)Lists.newArrayList((Object[])new String[]{(Object)ChatColor.GOLD + playerName, (Object)ChatColor.DARK_PURPLE + "captured by", (Object)ChatColor.GOLD + kothName, DateTimeFormats.DAY_MTH_HR_MIN_SECS.format(System.currentTimeMillis())}));
        stack.setItemMeta(meta);
        return stack;
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onSignChange(SignChangeEvent event) {
        if (this.isEventSign(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (this.isEventSign(block)) {
            BlockState state = block.getState();
            Sign sign = (Sign)state;
            ItemStack stack = new ItemStack(Material.SIGN, 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(EVENT_SIGN_ITEM_NAME);
            meta.setLore(Arrays.asList(sign.getLines()));
            stack.setItemMeta(meta);
            Player player = event.getPlayer();
            World world = player.getWorld();
            Location blockLocation = block.getLocation();
            if (player.getGameMode() != GameMode.CREATIVE && world.isGameRule("doTileDrops")) {
                world.dropItemNaturally(blockLocation, stack);
            }
            event.setCancelled(true);
            block.setType(Material.AIR);
            state.update();
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemMeta meta;
        ItemStack stack = event.getItemInHand();
        BlockState state = event.getBlock().getState();
        if (state instanceof Sign && stack.hasItemMeta() && (meta = stack.getItemMeta()).hasDisplayName() && meta.getDisplayName().equals(EVENT_SIGN_ITEM_NAME)) {
            Sign sign = (Sign)state;
            List<String> lore = meta.getLore();
            int count = 0;
            for (String loreLine : lore) {
                sign.setLine(count++, loreLine);
                if (count == 4) break;
            }
            sign.update();
        }
    }

    private boolean isEventSign(Block block) {
        BlockState state = block.getState();
        if (state instanceof Sign) {
            String[] lines = ((Sign)state).getLines();
            if (lines.length > 0 && lines[1] != null && lines[1].equals((Object)ChatColor.DARK_PURPLE + "captured by")) {
                return true;
            }
            return false;
        }
        return false;
    }
}


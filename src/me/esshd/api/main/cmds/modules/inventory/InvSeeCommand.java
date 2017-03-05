/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 */
package me.esshd.api.main.cmds.modules.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import me.esshd.api.main.BaseConstants;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.StaffPriority;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.utils.BukkitUtils;
import me.esshd.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class InvSeeCommand
extends BaseCommand
implements Listener {
    private final InventoryType[] types = new InventoryType[]{InventoryType.BREWING, InventoryType.CHEST, InventoryType.DISPENSER, InventoryType.ENCHANTING, InventoryType.FURNACE, InventoryType.HOPPER, InventoryType.PLAYER, InventoryType.WORKBENCH};
    private final Map<InventoryType, Inventory> inventories = new EnumMap<InventoryType, Inventory>(InventoryType.class);

    public InvSeeCommand(BasePlugin plugin) {
        super("invsee", "View the inventory of a player.");
        this.setAliases(new String[]{"inventorysee", "inventory", "inv"});
        this.setUsage("/(command) <player>");
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)HCF.getPlugin());
    }

    @Override
    public boolean isPlayerOnlyCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 1) {
                sender.sendMessage(this.getUsage(label));
                return true;
            }
            Player target = BukkitUtils.playerWithNameOrUUID(args[0]);
            if (target == null) {
                sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
                return true;
            }
            sender.sendMessage((Object)ChatColor.YELLOW + "This players inventory contains: ");
            ItemStack[] arritemStack = target.getInventory().getContents();
            int n = arritemStack.length;
            int n2 = 0;
            while (n2 < n) {
                ItemStack items = arritemStack[n2];
                if (items != null) {
                    sender.sendMessage((Object)ChatColor.AQUA + items.getType().toString().replace("_", "").toLowerCase() + ": " + items.getAmount());
                }
                ++n2;
            }
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(this.getUsage(label));
            return true;
        }
        Player player = (Player)sender;
        Inventory inventory = null;
        InventoryType[] types = this.types;
        int length = types.length;
        int i = 0;
        while (i < length) {
            InventoryType type = types[i];
            if (type.name().equalsIgnoreCase(args[0])) {
                Inventory inventoryRevert = Bukkit.createInventory((InventoryHolder)player, (InventoryType)type);
                inventory = this.inventories.putIfAbsent(type, inventoryRevert);
                if (inventory != null) break;
                inventory = inventoryRevert;
                break;
            }
            ++i;
        }
        if (inventory == null) {
            Player target2 = BukkitUtils.playerWithNameOrUUID(args[0]);
            if (sender.equals((Object)target2)) {
                sender.sendMessage((Object)ChatColor.RED + "You cannot check the inventory of yourself.");
                return true;
            }
            if (target2 == null || !BaseCommand.canSee(sender, target2)) {
                sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
                return true;
            }
            StaffPriority selfPriority = StaffPriority.of(player);
            if (StaffPriority.of(target2).isMoreThan(selfPriority)) {
                sender.sendMessage((Object)ChatColor.RED + "You do not have access to check the inventory of that player.");
                return true;
            }
            inventory = target2.getInventory();
        }
        player.openInventory(inventory);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        InventoryType[] values = InventoryType.values();
        ArrayList<String> results = new ArrayList<String>(values.length);
        Player senderPlayer = (Player)sender;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (senderPlayer != null && !senderPlayer.canSee(target)) continue;
            results.add(target.getName());
        }
        return BukkitUtils.getCompletions(args, results);
    }
}


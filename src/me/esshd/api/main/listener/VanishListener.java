package me.esshd.api.main.listener;

import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.StaffPriority;
import me.esshd.api.main.event.PlayerVanishEvent;
import me.esshd.api.main.user.BaseUser;
import me.esshd.api.utils.BukkitUtils;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockAction;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class VanishListener
        implements Listener {
    private static final String CHEST_INTERACT_PERMISSION = "vanish.chestinteract";
    private static final String INVENTORY_INTERACT_PERMISSION = "vanish.inventorysee";
    private static final String FAKE_CHEST_PREFIX = "[F] ";
    private static final String BLOCK_INTERACT_PERMISSION = "vanish.build";
    private final Map<UUID, Location> fakeChestLocationMap;
    private final Set<Player> onlineVanishedPlayers;
    private final BasePlugin plugin;

    public VanishListener(BasePlugin plugin) {
        this.fakeChestLocationMap = new HashMap<UUID, Location>();
        this.onlineVanishedPlayers = new HashSet<Player>();
        this.plugin = plugin;
    }

    public static void handleFakeChest(Player player, Chest chest, boolean open) {
        Inventory chestInventory = chest.getInventory();
        if ((chestInventory instanceof DoubleChestInventory)) {
            chest = (Chest) ((DoubleChestInventory) chestInventory).getHolder().getLeftSide();
        }
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockAction(chest.getX(), chest.getY(), chest.getZ(), Blocks.CHEST, 1, open ? 1 : 0));
        player.playSound(chest.getLocation(), open ? Sound.CHEST_OPEN : Sound.CHEST_CLOSE, 1.0F, 1.0F);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!this.onlineVanishedPlayers.isEmpty()) {
            StaffPriority selfPriority = StaffPriority.of(player);
            if (selfPriority != StaffPriority.ADMIN) {
                for (Player target : this.onlineVanishedPlayers) {
                    if ((this.plugin.getUserManager().getUser(target.getUniqueId()).isVanished()) && (StaffPriority.of(target).isMoreThan(selfPriority))) {
                        player.hidePlayer(target);
                    }
                }
            }
        }
        BaseUser baseUser = this.plugin.getUserManager().getUser(player.getUniqueId());
        if (baseUser.isVanished()) {
            this.onlineVanishedPlayers.add(player);
            player.sendMessage(ChatColor.YELLOW + "You have joined vanished.");
            List<String> vanished = new ArrayList();
            for (Player on : Bukkit.getOnlinePlayers()) {
                if ((on.hasPermission("vanish.chestinteract")) && (!player.equals(on)) && (this.plugin.getUserManager().getUser(on.getUniqueId()).isVanished()) && (player.canSee(on))) {
                    vanished.add(on.getName());
                }
            }
            player.sendMessage(ChatColor.BLUE + "There are currently " + ChatColor.AQUA + vanished.size() + ChatColor.BLUE + " staff online and vanished.");
            baseUser.updateVanishedState(player, true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (this.plugin.getUserManager().getUser(event.getPlayer().getUniqueId()).isVanished()) {
            this.onlineVanishedPlayers.remove(event.getPlayer());
            event.setQuitMessage(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerVanish(PlayerVanishEvent event) {
        if (event.isVanished()) {
            this.onlineVanishedPlayers.add(event.getPlayer());
        } else {
            this.onlineVanishedPlayers.remove(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if ((entity instanceof Player)) {
            Player player = event.getPlayer();
            if ((!player.isSneaking()) && (player.hasPermission("vanish.inventorysee")) && (this.plugin.getUserManager().getUser(player.getUniqueId()).isVanished())) {
                player.openInventory(((Player) entity).getInventory());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getReason() == EntityTargetEvent.TargetReason.CUSTOM) {
            return;
        }
        Entity target = event.getTarget();
        Entity entity = event.getEntity();
        if ((((entity instanceof ExperienceOrb)) || ((entity instanceof LivingEntity))) && ((target instanceof Player)) && (this.plugin.getUserManager().getUser(target.getUniqueId()).isVanished())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if (this.plugin.getUserManager().getUser(e.getPlayer().getUniqueId()).isVanished()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (this.plugin.getUserManager().getUser(event.getPlayer().getUniqueId()).isVanished()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.plugin.getUserManager().getUser(event.getEntity().getUniqueId()).isVanished()) {
            event.setDeathMessage(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        if ((cause == EntityDamageEvent.DamageCause.VOID) || (cause == EntityDamageEvent.DamageCause.SUICIDE)) {
            return;
        }
        Entity entity = event.getEntity();
        if ((entity instanceof Player)) {
            Player attacked = (Player) entity;
            BaseUser attackedUser = this.plugin.getUserManager().getUser(attacked.getUniqueId());
            Player attacker = BukkitUtils.getFinalAttacker(event, true);
            if (attackedUser.isVanished()) {
                if ((attacker != null) && (StaffPriority.of(attacked) != StaffPriority.NONE)) {
                    attacker.sendMessage(ChatColor.RED + "That player is vanished.");
                }
                event.setCancelled(true);
                return;
            }
            if ((attacker != null) && (this.plugin.getUserManager().getUser(attacker.getUniqueId()).isVanished())) {
                attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst vanished.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        BaseUser baseUser = this.plugin.getUserManager().getUser(player.getUniqueId());
        if ((baseUser.isVanished()) && (!player.hasPermission("vanish.build"))) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot build whilst vanished.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        BaseUser baseUser = this.plugin.getUserManager().getUser(player.getUniqueId());
        if ((baseUser.isVanished()) && (!player.hasPermission("vanish.build"))) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot build whilst vanished.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        BaseUser baseUser = this.plugin.getUserManager().getUser(player.getUniqueId());
        if ((baseUser.isVanished()) && (!player.hasPermission("vanish.build"))) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot build whilst vanished.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                if (this.plugin.getUserManager().getUser(uuid).isVanished()) {
                    event.setCancelled(true);
                }
                break;
            case LEFT_CLICK_BLOCK:
                Block block = event.getClickedBlock();
                BlockState state = block.getState();
                if (((state instanceof Chest)) && (this.plugin.getUserManager().getUser(uuid).isVanished())) {
                    Chest chest = (Chest) state;
                    Location chestLocation = chest.getLocation();
                    InventoryType type = chest.getInventory().getType();
                    if ((type == InventoryType.CHEST) && (this.fakeChestLocationMap.putIfAbsent(uuid, chestLocation) == null)) {
                        ItemStack[] contents = chest.getInventory().getContents();
                        Inventory fakeInventory = Bukkit.createInventory(null, contents.length, "[F] " + type.getDefaultTitle());
                        fakeInventory.setContents(contents);
                        event.setCancelled(true);
                        player.openInventory(fakeInventory);
                        handleFakeChest(player, chest, true);
                        this.fakeChestLocationMap.put(uuid, chestLocation);
                    }
                }
                break;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Location chestLocation;
        if ((chestLocation = (Location) this.fakeChestLocationMap.remove(player.getUniqueId())) != null) {
            BlockState blockState = chestLocation.getBlock().getState();
            if ((blockState instanceof Chest)) {
                handleFakeChest(player, (Chest) blockState, false);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if ((humanEntity instanceof Player)) {
            Player player = (Player) humanEntity;
            if (this.fakeChestLocationMap.containsKey(player.getUniqueId())) {
                ItemStack stack = event.getCurrentItem();
                if ((stack != null) && (stack.getType() != Material.AIR) && (!player.hasPermission("vanish.chestinteract"))) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot interact with fake chest inventories.");
                }
            }
        }
    }
}

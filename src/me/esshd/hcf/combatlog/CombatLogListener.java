package me.esshd.hcf.combatlog;

import me.esshd.api.utils.InventoryUtils;
import me.esshd.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;

public class CombatLogListener implements Listener {
    private static final int NEARBY_SPAWN_RADIUS = 64;
    private static final Set<UUID> SAFE_DISCONNECTS;
    private static final Map<UUID, CombatLogEntry> LOGGERS;

    static {
        SAFE_DISCONNECTS = new HashSet<UUID>();
        LOGGERS = new HashMap<>();
    }

    private final HCF plugin;

    public CombatLogListener(final HCF plugiton) {
        plugin = plugiton;
    }

    public static void safelyDisconnect(final Player player, final String reason) {
        if (CombatLogListener.SAFE_DISCONNECTS.add(player.getUniqueId())) {
            player.kickPlayer(reason);
        }
    }

    public static void removeCombatLoggers() {
        final Iterator<CombatLogEntry> iterator = CombatLogListener.LOGGERS.values().iterator();
        while (iterator.hasNext()) {
            final CombatLogEntry entry = iterator.next();
            entry.task.cancel();
            entry.loggerEntity.getBukkitEntity().remove();
            iterator.remove();
        }
        CombatLogListener.SAFE_DISCONNECTS.clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuitSafe(final PlayerQuitEvent event) {
        CombatLogListener.SAFE_DISCONNECTS.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLoggerInteract(final EntityInteractEvent event) {
        final Collection<CombatLogEntry> entries = CombatLogListener.LOGGERS.values();
        for (final CombatLogEntry entry : entries) {
            if (entry.loggerEntity.getBukkitEntity().equals(event.getEntity())) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLoggerDeath(final LoggerDeathEvent event) {
        final CombatLogEntry entry = CombatLogListener.LOGGERS.remove(event.getLoggerEntity().getPlayerUUID());
        if (entry != null) {
            entry.task.cancel();
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerSpawnLocation(final ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            for (CombatLogEntry entry : LOGGERS.values()) {
                if (entry.loggerEntity.getId() == entity.getEntityId()) {
                    entity.remove();
                    CombatLogListener.LOGGERS.remove(entry.loggerEntity.getPlayerUUID());
                    entry.task.cancel();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerSpawnLocation(final ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            for (CombatLogEntry entry : LOGGERS.values()) {
                if (entry.loggerEntity.getId() == entity.getEntityId()) {
                    entity.remove();
                    CombatLogListener.LOGGERS.remove(entry.loggerEntity.getPlayerUUID());
                    entry.task.cancel();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerSpawnLocation(final PlayerSpawnLocationEvent event) {
        final CombatLogEntry combatLogEntry = CombatLogListener.LOGGERS.remove(event.getPlayer().getUniqueId());
        if (combatLogEntry != null) {
            final CraftLivingEntity loggerEntity = combatLogEntry.loggerEntity.getBukkitEntity();
            final Player player = event.getPlayer();
            event.setSpawnLocation(loggerEntity.getLocation());
            player.setFallDistance(loggerEntity.getFallDistance());
            player.setHealth(Math.min(((CraftPlayer) player).getMaxHealth(), loggerEntity.getHealth()));
            player.setTicksLived(loggerEntity.getTicksLived());
            player.setRemainingAir(loggerEntity.getRemainingAir());
            loggerEntity.remove();
            combatLogEntry.task.cancel();
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final PlayerInventory inventory = player.getInventory();
        if (player.getGameMode() != GameMode.CREATIVE && !player.isDead() && !CombatLogListener.SAFE_DISCONNECTS.contains(uuid)) {
            if (InventoryUtils.isEmpty(inventory) || this.plugin.getTimerManager().getInvincibilityTimer().getRemaining(uuid) > 0L) {
                return;
            }
            if (this.plugin.getTimerManager().teleportTimer.getNearbyEnemies(player, NEARBY_SPAWN_RADIUS) <= 0) {
                return;
            }
            final Location location = player.getLocation();
            if (this.plugin.getFactionManager().getFactionAt(location).isSafezone()) {
                return;
            }
            if (CombatLogListener.LOGGERS.containsKey(player.getUniqueId())) {
                return;
            }
            final World world = location.getWorld();
            final LoggerEntity loggerEntity = new LoggerEntity(world, location, player);
            final LoggerSpawnEvent calledEvent = new LoggerSpawnEvent(loggerEntity);
            Bukkit.getPluginManager().callEvent(calledEvent);
            CombatLogListener.LOGGERS.put(uuid, new CombatLogEntry(loggerEntity, new LoggerRemovable(uuid, loggerEntity).runTaskLater(this.plugin, 600L)));
            final CraftEntity craftEntity = loggerEntity.getBukkitEntity();
            if (craftEntity != null) {
                final CraftLivingEntity craftLivingEntity = (CraftLivingEntity) craftEntity;
                final EntityEquipment entityEquipment = craftLivingEntity.getEquipment();
                entityEquipment.setItemInHand(inventory.getItemInHand());
                entityEquipment.setArmorContents(inventory.getArmorContents());
            }
        }
    }


    private static class LoggerRemovable extends BukkitRunnable {
        private final UUID uuid;
        private final LoggerEntity loggerEntity;

        public LoggerRemovable(final UUID uuid, final LoggerEntity loggerEntity) {
            this.uuid = uuid;
            this.loggerEntity = loggerEntity;
        }

        public void run() {
            if (CombatLogListener.LOGGERS.remove(this.uuid) != null) {
                this.loggerEntity.dead = true;
            }
        }
    }
}

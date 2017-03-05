package me.esshd.hcf.timer.type;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import me.esshd.api.utils.BukkitUtils;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.DurationFormatter;
import me.esshd.hcf.HCF;
import me.esshd.hcf.faction.claim.Claim;
import me.esshd.hcf.faction.event.FactionClaimChangedEvent;
import me.esshd.hcf.faction.event.PlayerClaimEnterEvent;
import me.esshd.hcf.faction.event.cause.ClaimChangeCause;
import me.esshd.hcf.faction.type.ClaimableFaction;
import me.esshd.hcf.faction.type.Faction;
import me.esshd.hcf.faction.type.PlayerFaction;
import me.esshd.hcf.faction.type.RoadFaction;
import me.esshd.hcf.timer.PlayerTimer;
import me.esshd.hcf.timer.TimerCooldown;
import me.esshd.hcf.timer.event.TimerClearEvent;
import me.esshd.hcf.visualise.VisualType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Timer used to apply PVP Protection to {@link Player}s.
 */
public class InvincibilityTimer extends PlayerTimer implements Listener {

    // TODO: Future proof
    private static final String PVP_COMMAND = "/pvp enable";

    // The PlayerPickupItemEvent spams if cancelled, needs a delay between messages to look clean.
    private static final long ITEM_PICKUP_DELAY = TimeUnit.SECONDS.toMillis(30L);
    private static final long ITEM_PICKUP_MESSAGE_DELAY = 1250L;
    private static final String ITEM_PICKUP_MESSAGE_META_KEY = "pickupMessageDelay";

    private final Map<UUID, Long> itemUUIDPickupDelays = new HashMap<>();
    private final HCF plugin;

    public InvincibilityTimer(HCF plugin) {
        super("PVP Timer", TimeUnit.MINUTES.toMillis(30L));
        this.plugin = plugin;
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.GREEN.toString() + ChatColor.BOLD;
    }

    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player != null) {
            player.sendMessage(ChatColor.YELLOW + "Your " + getDisplayName() + ChatColor.YELLOW + " has ran out. PvP is now enabled.");
            plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CLAIM_BORDER, null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStop(TimerClearEvent event) {
        if (event.getTimer() == this) {
            Optional<UUID> optionalUserUUID = event.getUserUUID();
            if (optionalUserUUID.isPresent()) {
                this.onExpire(optionalUserUUID.get());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onClaimChange(FactionClaimChangedEvent event) {
        if (event.getCause() != ClaimChangeCause.CLAIM) {
            return;
        }

        Collection<Claim> claims = event.getAffectedClaims();
        for (Claim claim : claims) {
            Collection<Player> players = claim.getPlayers();
            if (players.isEmpty()) {
                continue;
            }

            Location location = new Location(claim.getWorld(), claim.getMinimumX() - 1, 0, claim.getMinimumZ() - 1);
            location = BukkitUtils.getHighestLocation(location, location);
            for (Player player : players) {
                if (getRemaining(player) > 0L && player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
                    player.sendMessage(ChatColor.YELLOW + "Faction Land has been claimed at your position. As you still have your " + getDisplayName() + ChatColor.YELLOW + ", you were teleported away.");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (this.setCooldown(player, player.getUniqueId(), defaultCooldown, true)) {
            this.setPaused(player.getUniqueId(), true);
            player.sendMessage(ChatColor.YELLOW + "You will now have " + getDisplayName() + ChatColor.YELLOW + " for 30 minutes.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        World world = player.getWorld();
        Location location = player.getLocation();
        Collection<ItemStack> drops = event.getDrops();
        if (!drops.isEmpty()) {
            Iterator<ItemStack> iterator = drops.iterator();

            // Drop the items manually so we can add meta to prevent
            // PVP Protected players from collecting them.
            long stamp = System.currentTimeMillis() + +ITEM_PICKUP_DELAY;
            while (iterator.hasNext()) {
                itemUUIDPickupDelays.put(world.dropItemNaturally(location, iterator.next()).getUniqueId(), stamp);
                iterator.remove();
            }
        }

        clearCooldown(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player == null)
            return;
        long remaining = getRemaining(player);
        if (remaining > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.YELLOW + "You cannot ignite blocks with a " + getDisplayName() + ".");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        long remaining = getRemaining(player);
        if (remaining > 0L) {
            UUID itemUUID = event.getItem().getUniqueId();
            Long delay = itemUUIDPickupDelays.get(itemUUID);
            if (delay == null)
                return;

            // The item has been spawned for over the required pickup time for
            // PVP Protected players, let them pick it up.
            long millis = System.currentTimeMillis();
            if ((delay - millis) > 0L) {
                event.setCancelled(true);

                // Don't let the pickup event spam the player.
                List<MetadataValue> value = player.getMetadata(ITEM_PICKUP_MESSAGE_META_KEY);
                if (value != null && !value.isEmpty() && value.get(0).asLong() - millis <= 0L) {
                    player.setMetadata(ITEM_PICKUP_MESSAGE_META_KEY, new FixedMetadataValue(plugin, millis + ITEM_PICKUP_MESSAGE_DELAY));
                    player.sendMessage(ChatColor.RED + "You cannot pick this item up with a PvP Timer.");
                }
            } else
                itemUUIDPickupDelays.remove(itemUUID);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TimerCooldown runnable = cooldowns.get(player.getUniqueId());
        if (runnable != null && runnable.getRemaining() > 0L) {
            runnable.setPaused(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            if (this.canApply() && this.setCooldown(player, player.getUniqueId(), this.defaultCooldown, true)) {
                this.setPaused(player.getUniqueId(), true);
                player.sendMessage(ChatColor.YELLOW + "You now have a " + getDisplayName() + ChatColor.YELLOW + ".");
            }
        } else {
            // If a player has their timer paused and they are not in a safezone, un-pause for them.
            // We do this because disconnection pauses PVP Protection.
            if (this.isPaused(player) && getRemaining(player) > 0L && !plugin.getFactionManager().getFactionAt(event.getSpawnLocation()).isSafezone()) {
                this.setPaused(player.getUniqueId(), false);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerClaimEnterMonitor(PlayerClaimEnterEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
            clearCooldown(player);
            return;
        }

        boolean flag = getRemaining(player.getUniqueId()) > 0L;
        if (flag) {
            Faction toFaction = event.getToFaction();
            Faction fromFaction = event.getFromFaction();

            if (fromFaction.isSafezone() && !toFaction.isSafezone()) {
                player.sendMessage(ChatColor.YELLOW + "Your " + getDisplayName() + ChatColor.YELLOW + " is no longer paused.");
                this.setPaused(player.getUniqueId(), false);
            } else if (!fromFaction.isSafezone() && toFaction.isSafezone()) {
                player.sendMessage(ChatColor.YELLOW + "Your " + getDisplayName() + ChatColor.YELLOW + " is now paused.");
                this.setPaused(player.getUniqueId(), true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
        Player player = event.getPlayer();
        Faction toFaction = event.getToFaction();
        long remaining; // lazy load
        if (toFaction instanceof ClaimableFaction && (remaining = getRemaining(player)) > 0L) {
            if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
                // Allow player to enter own claim, but just remove PVP Protection when teleporting.
                PlayerFaction playerFaction; // lazy-load

                if (toFaction instanceof PlayerFaction && (playerFaction = plugin.getFactionManager().getPlayerFaction(player)) != null && playerFaction == toFaction) {
                    player.sendMessage(ChatColor.YELLOW + "You have entered your own claim, therefore your " + ChatColor.GREEN + getDisplayName() + ChatColor.YELLOW + " was removed.");
                    clearCooldown(player);
                    return;
                }
            }

            if (!toFaction.isSafezone() && !(toFaction instanceof RoadFaction)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot enter " + toFaction.getDisplayName(player) + ChatColor.RED + " whilst your " + getDisplayName() + ChatColor.RED + " is active." + " Use '" + ChatColor.GOLD + PVP_COMMAND + ChatColor.RED
                        + "' to remove this timer.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player attacker = BukkitUtils.getFinalAttacker(event, true);
            if (attacker == null)
                return;

            long remaining;
            Player player = (Player) entity;
            if ((remaining = getRemaining(player)) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " has their " + getDisplayName() + ChatColor.YELLOW + " for another " + ChatColor.WHITE
                        + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + '.');

                return;
            }

            if ((remaining = getRemaining(attacker)) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst your " + getDisplayName() + ChatColor.RED + " is active. Use '" + ChatColor.GOLD + PVP_COMMAND + ChatColor.RED + "' to remove your " + getDisplayName() + ".");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        if (potion.getShooter() instanceof Player && BukkitUtils.isDebuff(potion)) {
            for (LivingEntity livingEntity : event.getAffectedEntities()) {
                if (livingEntity instanceof Player) {
                    if (getRemaining((Player) livingEntity) > 0L) {
                        event.setIntensity(livingEntity, 0);
                    }
                }
            }
        }
    }

    @Override
    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite, @Nullable Predicate<Long> callback) {
        return this.canApply() && super.setCooldown(player, playerUUID, duration, overwrite, callback);
    }

    //boolean to tell wether server is in eotw or kitmap
    private boolean canApply() {
        return !plugin.getEotwHandler().isEndOfTheWorld() && !ConfigurationService.KIT_MAP && plugin.getSotwTimer().getSotwRunnable() == null;
    }
}

package me.esshd.hcf.listener;

import com.google.common.base.Optional;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.HCF;
import me.esshd.hcf.eventgame.faction.KothFaction;
import me.esshd.hcf.faction.event.*;
import me.esshd.hcf.faction.struct.RegenStatus;
import me.esshd.hcf.faction.struct.Relation;
import me.esshd.hcf.faction.type.Faction;
import me.esshd.hcf.faction.type.PlayerFaction;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FactionListener implements Listener {

    private static final long FACTION_JOIN_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);
    private static final String FACTION_JOIN_WAIT_WORDS = DurationFormatUtils.formatDurationWords(FACTION_JOIN_WAIT_MILLIS, true, true);

    private static final String LAND_CHANGED_META_KEY = "landChangedMessage";
    private static final long LAND_CHANGE_MSG_THRESHOLD = 225L;

    private final HCF plugin;

    public FactionListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRenameMonitor(FactionRenameEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof KothFaction) {
            ((KothFaction) faction).getCaptureZone().setName(event.getNewName());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionCreate(FactionCreateEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            CommandSender sender = event.getSender();
            for (Player player : Bukkit.getOnlinePlayers()) {
                //String msg = (player == null ? faction.getName() : faction.getDisplayName(player)) + ChatColor.YELLOW + " has been " + ChatColor.GREEN.toString() + 
                //"created" + ChatColor.YELLOW + " by " + ChatColor.WHITE + (sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName()) + ChatColor.YELLOW + '.';
                String msg = (player == null ? ChatColor.RED + faction.getName() : faction.getDisplayName(player)) + ChatColor.YELLOW + " has been " + ChatColor.GREEN.toString() +
                        "created" + ChatColor.YELLOW + " by " + ChatColor.GREEN + sender.getName() + ChatColor.YELLOW + ".";


                player.sendMessage(msg);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(FactionRemoveEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            CommandSender sender = event.getSender();
            for (Player player : Bukkit.getOnlinePlayers()) {
                //String msg = (player == null ? faction.getName() : faction.getDisplayName(player)) + ChatColor.YELLOW + " has been " + ChatColor.RED.toString()
                //+ "disbanded" + ChatColor.YELLOW + " by " + ChatColor.WHITE + (sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName()) + ChatColor.YELLOW + '.';
                String msg = (player == null ? faction.getName() : faction.getDisplayName(player)) + ChatColor.YELLOW + " has been " + ChatColor.RED.toString()
                        + "disbanded" + ChatColor.YELLOW + " by " + ChatColor.WHITE + sender.getName() + ChatColor.YELLOW + '.';
                player.sendMessage(msg);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRename(FactionRenameEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Relation relation = faction.getRelation(player);
                String msg = relation.toChatColour() + event.getOriginalName() + ChatColor.YELLOW + " was " + ChatColor.YELLOW.toString() + "renamed" + ChatColor.YELLOW
                        + " to " + relation.toChatColour() + event.getNewName() + ChatColor.YELLOW + '.';
                player.sendMessage(msg);
            }
        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onFactionRename1(final FactionRenameEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            ((PlayerFaction) faction).broadcast(ChatColor.YELLOW + "Your faction has been renamed to" + ChatColor.GRAY + ": " + ChatColor.GREEN + event.getNewName());
        }
    }

    private long getLastLandChangedMeta(Player player) {
        List<MetadataValue> value = player.getMetadata(LAND_CHANGED_META_KEY);
        long millis = System.currentTimeMillis();
        long remaining = value == null || value.isEmpty() ? 0L : value.get(0).asLong() - millis;
        if (remaining <= 0L) { // update the metadata.
            player.setMetadata(LAND_CHANGED_META_KEY, new FixedMetadataValue(plugin, millis + LAND_CHANGE_MSG_THRESHOLD));
        }

        return remaining;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneEnter(CaptureZoneEnterEvent event) {
        Player player = event.getPlayer();
        if (getLastLandChangedMeta(player) > 0L)
            return; // delay before re-messaging.

        if (plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
            player.sendMessage(
                    ChatColor.YELLOW + "Entering Capture Zone " + event.getCaptureZone().getDisplayName() + ChatColor.YELLOW + " " + '(' + event.getFaction().getName() + ChatColor.YELLOW + ')');
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
        Faction toFaction = event.getToFaction();
        if (toFaction.isSafezone()) {
            Player player = event.getPlayer();
            player.setHealth(((CraftPlayer) player).getMaxHealth());
            player.setFoodLevel(20);
            player.setFireTicks(0);
            player.setSaturation(4.0F);
        }

        Player player = event.getPlayer();
        if (getLastLandChangedMeta(player) > 0L)
            return; // delay before re-messaging.

        Faction fromFaction = event.getFromFaction();
        player.sendMessage(ChatColor.YELLOW + "Leaving " + fromFaction.getDisplayName(player) + ChatColor.YELLOW + ", Entering " + toFaction.getDisplayName(player) + ChatColor.YELLOW + "");
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
        Optional<Player> optionalPlayer = event.getPlayer();
        if (optionalPlayer.isPresent()) {
            plugin.getUserManager().getUser(optionalPlayer.get().getUniqueId()).setLastFactionLeaveMillis(System.currentTimeMillis());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPreFactionJoin(PlayerJoinFactionEvent event) {
        Faction faction = event.getFaction();
        Optional<Player> optionalPlayer = event.getPlayer();
        if (faction instanceof PlayerFaction && optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            PlayerFaction playerFaction = (PlayerFaction) faction;

            if (!ConfigurationService.KIT_MAP && !plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.getRegenStatus() == RegenStatus.PAUSED) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions that are not regenerating DTR.");
                return;
            }

            long difference = (plugin.getUserManager().getUser(player.getUniqueId()).getLastFactionLeaveMillis() - System.currentTimeMillis()) + FACTION_JOIN_WAIT_MILLIS;
            if (difference > 0L && !player.hasPermission("hcf.faction.argument.staff.forcejoin")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions after just leaving within " + FACTION_JOIN_WAIT_WORDS + ". " + "You need to wait another "
                        + DurationFormatUtils.formatDurationWords(difference, true, true) + '.');
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(PlayerLeaveFactionEvent event) {
        if (event.isForce() || event.isKick()) {
            return;
        }

        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            Optional<Player> optional = event.getPlayer();
            if (optional.isPresent()) {
                Player player = optional.get();
                if (plugin.getFactionManager().getFactionAt(player.getLocation()) == faction) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Please exit your faction's territory before attempting to leave.");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction != null) {
            playerFaction.printDetails(player);
            playerFaction.broadcast(ChatColor.GOLD + "Member Online: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.',
                    player.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction != null) {
            playerFaction.broadcast(ChatColor.GOLD + "Member Offline: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.');
        }
    }
}

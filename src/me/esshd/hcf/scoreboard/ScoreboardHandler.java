package me.esshd.hcf.scoreboard;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import me.esshd.hcf.HCF;
import me.esshd.hcf.faction.event.FactionRelationCreateEvent;
import me.esshd.hcf.faction.event.FactionRelationRemoveEvent;
import me.esshd.hcf.faction.event.PlayerJoinedFactionEvent;
import me.esshd.hcf.faction.event.PlayerLeftFactionEvent;
import me.esshd.hcf.scoreboard.provider.TimerSidebarProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class ScoreboardHandler implements Listener {

    private static final long UPDATE_TICK_INTERVAL = 2L;

    private final Map<UUID, PlayerBoard> playerBoards = new HashMap<>();
    private final TimerSidebarProvider timerSidebarProvider;
    private final HCF plugin;

    public ScoreboardHandler(HCF plugin) {
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
        this.timerSidebarProvider = new TimerSidebarProvider(plugin);

        // Give all online players a scoreboard.
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            this.applyBoard(player).addUpdates(players);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (PlayerBoard board : this.playerBoards.values()) {
            board.addUpdate(player); // Update this player for every other online player.
        }
        this.applyBoard(player).addUpdates(Bukkit.getOnlinePlayers());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(PlayerJoinedFactionEvent event) {
        Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            Player player = optional.get();

            Collection<Player> players = event.getFaction().getOnlinePlayers();
            this.getPlayerBoard(event.getPlayerUUID()).addUpdates(players);
            for (Player target : players) {
                this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
        Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            Player player = optional.get();

            Collection<Player> players = event.getFaction().getOnlinePlayers();
            this.getPlayerBoard(event.getUniqueID()).addUpdates(players);
            for (Player target : players) {
                this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyCreate(FactionRelationCreateEvent event) {
        Iterable<Player> updates = Iterables.concat(event.getSenderFaction().getOnlinePlayers(), event.getTargetFaction().getOnlinePlayers());
        for (PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyRemove(FactionRelationRemoveEvent event) {
        Iterable<Player> updates = Iterables.concat(event.getSenderFaction().getOnlinePlayers(), event.getTargetFaction().getOnlinePlayers());
        for (PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }
    }

    public PlayerBoard getPlayerBoard(UUID uuid) {
        return this.playerBoards.get(uuid);
    }

    public PlayerBoard applyBoard(Player player) {
        PlayerBoard board = new PlayerBoard(plugin, player);
        PlayerBoard previous = this.playerBoards.put(player.getUniqueId(), board);
        if (previous != null && previous != board) {
            previous.remove();
        }

        board.setSidebarVisible(true);
        board.setDefaultSidebar(this.timerSidebarProvider, UPDATE_TICK_INTERVAL);
        return board;
    }

    public void clearBoards() {
        Iterator<PlayerBoard> iterator = this.playerBoards.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().remove();
            iterator.remove();
        }
    }
}

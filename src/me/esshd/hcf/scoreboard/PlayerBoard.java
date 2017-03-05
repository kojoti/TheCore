/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.scoreboard.DisplaySlot
 *  org.bukkit.scoreboard.Objective
 *  org.bukkit.scoreboard.Scoreboard
 *  org.bukkit.scoreboard.ScoreboardManager
 *  org.bukkit.scoreboard.Team
 */
package me.esshd.hcf.scoreboard;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.HCF;
import me.esshd.hcf.aextra.Addons;
import me.esshd.hcf.faction.FactionManager;
import me.esshd.hcf.faction.type.PlayerFaction;
import me.esshd.hcf.pvpclass.archer.ArcherClass;
import me.esshd.hcf.scoreboard.BufferedObjective;
import me.esshd.hcf.scoreboard.SidebarEntry;
import me.esshd.hcf.scoreboard.SidebarProvider;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class PlayerBoard {
    private boolean sidebarVisible = false;
    private SidebarProvider defaultProvider;
    private SidebarProvider temporaryProvider;
    private BukkitRunnable runnable;
    private final AtomicBoolean removed = new AtomicBoolean(false);
    private final BufferedObjective bufferedObjective;
    private final Scoreboard scoreboard;
    private final Player player;
    private final HCF plugin;

    public PlayerBoard(HCF plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(this.scoreboard);
        player.setScoreboard(this.scoreboard);
    }

    public synchronized Team getAllyTeam() {
        Team team = this.scoreboard.getTeam("enemies");
        if (team == null) {
            team = this.scoreboard.registerNewTeam("archers");
            team.setPrefix(ConfigurationService.ALLY_COLOUR.toString());
        }
        return team;
    }

    public synchronized Team getArcherTeam() {
        Team team = this.scoreboard.getTeam("archers");
        if (team == null) {
            team = this.scoreboard.registerNewTeam("archers");
            team.setPrefix(ChatColor.DARK_RED.toString());
        }
        return team;
    }

    public synchronized Team getNeutralTeam() {
        Team team = this.scoreboard.getTeam("neutrals");
        if (team == null) {
            team = this.scoreboard.registerNewTeam("neutrals");
            team.setPrefix(ConfigurationService.ENEMY_COLOUR.toString());
        }
        return team;
    }

    public synchronized Team getMemberTeam() {
        Team team = this.scoreboard.getTeam("members");
        if (team == null) {
            team = this.scoreboard.registerNewTeam("members");
            team.setPrefix(ConfigurationService.TEAMMATE_COLOUR.toString());
            team.setCanSeeFriendlyInvisibles(true);
        }
        return team;
    }

    public void remove() {
        if (!this.removed.getAndSet(true) && this.scoreboard != null) {
            for (Team team : this.scoreboard.getTeams()) {
                team.unregister();
            }
            for (Objective objective : this.scoreboard.getObjectives()) {
                objective.unregister();
            }
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public boolean isSidebarVisible() {
        return this.sidebarVisible;
    }

    public void setSidebarVisible(boolean visible) {
        this.sidebarVisible = visible;
        this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }

    public void setDefaultSidebar(final SidebarProvider provider, long updateInterval) {
        if (provider != this.defaultProvider) {
            this.defaultProvider = provider;
            if (this.runnable != null) {
                this.runnable.cancel();
            }
            if (provider == null) {
                this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
                return;
            }
            this.runnable = new BukkitRunnable(){

                public void run() {
                    if (PlayerBoard.this.removed.get()) {
                        this.cancel();
                        return;
                    }
                    if (provider == PlayerBoard.this.defaultProvider) {
                        PlayerBoard.this.updateObjective();
                    }
                }
            };
            this.runnable.runTaskTimerAsynchronously((Plugin)this.plugin, updateInterval, updateInterval);
        }
    }

    public void setTemporarySidebar(final SidebarProvider provider, long expiration) {
        if (this.removed.get()) {
            throw new IllegalStateException("Cannot update whilst board is removed");
        }
        this.temporaryProvider = provider;
        this.updateObjective();
        new BukkitRunnable(){

            public void run() {
                if (PlayerBoard.this.removed.get()) {
                    this.cancel();
                    return;
                }
                if (PlayerBoard.this.temporaryProvider == provider) {
                    PlayerBoard.access$4(PlayerBoard.this, null);
                    PlayerBoard.this.updateObjective();
                }
            }
        }.runTaskLaterAsynchronously((Plugin)this.plugin, expiration);
    }

    private void updateObjective() {
        SidebarProvider provider;
        if (this.removed.get()) {
            throw new IllegalStateException("Cannot update whilst board is removed");
        }
        SidebarProvider sidebarProvider = provider = this.temporaryProvider != null ? this.temporaryProvider : this.defaultProvider;
        if (provider == null) {
            this.bufferedObjective.setVisible(false);
        } else {
            this.bufferedObjective.setTitle(provider.getTitle());
            this.bufferedObjective.setAllLines(provider.getLines(this.player));
            this.bufferedObjective.flip();
        }
    }

    public void addUpdate(Player target) {
        this.addUpdates(Collections.singleton(target));
    }

    public void addUpdates(Iterable<? extends Player> updates) {
        if (this.removed.get()) {
            throw new IllegalStateException("Cannot update whilst board is removed");
        }
        this.player.setScoreboard(this.scoreboard);
        PlayerFaction playerFaction = null;
        boolean firstExecute = false;
        for (Player update : updates) {
            PlayerFaction targetFaction;
            if (this.player.equals((Object)update)) {
                this.getMemberTeam().addPlayer((OfflinePlayer)update);
                continue;
            }
            if (!firstExecute) {
                playerFaction = this.plugin.getFactionManager().getPlayerFaction(this.player);
                firstExecute = true;
            }
            if (playerFaction == null || (targetFaction = this.plugin.getFactionManager().getPlayerFaction(update)) == null) {
                this.getNeutralTeam().addPlayer((OfflinePlayer)update);
                continue;
            }
            if (playerFaction == targetFaction) {
                this.getMemberTeam().addPlayer((OfflinePlayer)update);
                continue;
            }
            if (ArcherClass.TAGGED.containsKey(update.getUniqueId())) {
                this.getArcherTeam().addPlayer((OfflinePlayer)update);
                continue;
            }
            if (playerFaction.getAllied().contains(targetFaction.getUniqueID())) {
                this.getAllyTeam().addPlayer((OfflinePlayer)update);
                continue;
            }
            this.getNeutralTeam().addPlayer((OfflinePlayer)update);
        }
        //if (playerFaction != null) {
        //    Addons.broadcastFocus(playerFaction);
        //}
    }

    public void addUpdates(Player[] updates) {
        if (this.removed.get()) {
            throw new IllegalStateException("Cannot update whilst board is removed");
        }
        this.player.setScoreboard(this.scoreboard);
        PlayerFaction playerFaction = null;
        boolean firstExecute = false;
        Player[] arrplayer = updates;
        int n = arrplayer.length;
        int n2 = 0;
        while (n2 < n) {
            Player update = arrplayer[n2];
            if (this.player.equals((Object)update)) {
                this.getMemberTeam().addPlayer((OfflinePlayer)update);
            } else {
                PlayerFaction targetFaction;
                if (!firstExecute) {
                    playerFaction = this.plugin.getFactionManager().getPlayerFaction(this.player);
                    firstExecute = true;
                }
                if (playerFaction == null || (targetFaction = this.plugin.getFactionManager().getPlayerFaction(update)) == null) {
                    this.getNeutralTeam().addPlayer((OfflinePlayer)update);
                } else if (playerFaction == targetFaction) {
                    this.getMemberTeam().addPlayer((OfflinePlayer)update);
                } else if (ArcherClass.TAGGED.containsKey(update.getUniqueId())) {
                    this.getArcherTeam().addPlayer((OfflinePlayer)update);
                } else if (playerFaction.getAllied().contains(targetFaction.getUniqueID())) {
                    this.getAllyTeam().addPlayer((OfflinePlayer)update);
                } else {
                    this.getNeutralTeam().addPlayer((OfflinePlayer)update);
                }
            }
            ++n2;
        }
        //if (playerFaction != null) {
        //    Addons.broadcastFocus(playerFaction);
        //}
    }

    static /* synthetic */ void access$4(PlayerBoard playerBoard, SidebarProvider sidebarProvider) {
        playerBoard.temporaryProvider = sidebarProvider;
    }

}


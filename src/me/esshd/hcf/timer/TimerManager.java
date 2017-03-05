package me.esshd.hcf.timer;

import lombok.Data;
import lombok.Getter;
import me.esshd.api.utils.Config;
import me.esshd.hcf.HCF;
import me.esshd.hcf.eventgame.EventTimer;
import me.esshd.hcf.timer.type.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashSet;
import java.util.Set;

public class TimerManager implements Listener {


    public final CombatTimer combatTimer;

    private final LogoutTimer logoutTimer;
    private final EnderPearlTimer enderPearlTimer;


    public final EventTimer eventTimer;

    @Getter
    public final GappleTimer gappleTimer;

    @Getter
    public final ArcherTimer archerTimer;

    @Getter
    public
    final InvincibilityTimer invincibilityTimer;

    @Getter
    private final PvpClassWarmupTimer pvpClassWarmupTimer;

    @Getter
    private final StuckTimer stuckTimer;

    @Getter
    public final TeleportTimer teleportTimer;

    @Getter
    private final Set<Timer> timers = new LinkedHashSet<>();

    private final JavaPlugin plugin;

    private Config config;

    public TimerManager(HCF plugin) {
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
        this.registerTimer(this.enderPearlTimer = new EnderPearlTimer(plugin));
        this.registerTimer(this.logoutTimer = new LogoutTimer());
        this.registerTimer(this.gappleTimer = new GappleTimer(plugin));
        this.registerTimer(this.stuckTimer = new StuckTimer());
        this.registerTimer(this.invincibilityTimer = new InvincibilityTimer(plugin));
        this.registerTimer(this.combatTimer = new CombatTimer(plugin));
        this.registerTimer(this.teleportTimer = new TeleportTimer(plugin));
        this.registerTimer(this.eventTimer = new EventTimer(plugin));
        this.registerTimer(this.pvpClassWarmupTimer = new PvpClassWarmupTimer(plugin));
        this.registerTimer(this.archerTimer = new ArcherTimer(plugin));
        this.reloadTimerData();
    }

    public void registerTimer(Timer timer) {
        this.timers.add(timer);
        if (timer instanceof Listener) {
            this.plugin.getServer().getPluginManager().registerEvents((Listener) timer, this.plugin);
        }
    }

    public void unregisterTimer(Timer timer) {
        this.timers.remove(timer);
    }

    /**
     * Reloads the {@link Timer} data from storage.
     */
    public void reloadTimerData() {
        this.config = new Config(HCF.getPlugin(), "timers");
        for (Timer timer : this.timers) {
            timer.load(this.config);
        }
    }

    /**
     * Saves the {@link Timer} data to storage.
     */
    public void saveTimerData() {
        for (Timer timer : this.timers) {
            timer.onDisable(this.config);
        }

        this.config.save();
    }

    public CombatTimer getCombatTimer() {
        return combatTimer;
    }

    public LogoutTimer getLogoutTimer() {
        return logoutTimer;
    }

    public EnderPearlTimer getEnderPearlTimer() {
        return enderPearlTimer;
    }

    public EventTimer getEventTimer() {
        return eventTimer;
    }

    public GappleTimer getGappleTimer() {
        return gappleTimer;
    }

    public ArcherTimer getArcherTimer() {
        return archerTimer;
    }

    public InvincibilityTimer getInvincibilityTimer() {
        return invincibilityTimer;
    }

    public PvpClassWarmupTimer getPvpClassWarmupTimer() {
        return pvpClassWarmupTimer;
    }

    public StuckTimer getStuckTimer() {
        return stuckTimer;
    }

    public TeleportTimer getTeleportTimer() {
        return teleportTimer;
    }

    public Set<Timer> getTimers() {
        return timers;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Config getConfig() {
        return config;
    }
}

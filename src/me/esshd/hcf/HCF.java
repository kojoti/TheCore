package me.esshd.hcf;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.utils.server.TPS;
import me.esshd.hcf.aextra.Addons;
import me.esshd.hcf.cmds.*;
import me.esshd.hcf.combatlog.CombatLogListener;
import me.esshd.hcf.combatlog.CustomEntityRegistration;
import me.esshd.hcf.eco.*;
import me.esshd.hcf.eventgame.CaptureZone;
import me.esshd.hcf.eventgame.EventExecutor;
import me.esshd.hcf.eventgame.EventScheduler;
import me.esshd.hcf.eventgame.conquest.ConquestExecutor;
import me.esshd.hcf.eventgame.crate.KeyEnderListener;
import me.esshd.hcf.eventgame.crate.KeyListener;
import me.esshd.hcf.eventgame.crate.KeyManager;
import me.esshd.hcf.eventgame.crate.LootExecutor;
import me.esshd.hcf.eventgame.eotw.EotwCommand;
import me.esshd.hcf.eventgame.eotw.EotwHandler;
import me.esshd.hcf.eventgame.eotw.EotwListener;
import me.esshd.hcf.eventgame.faction.CapturableFaction;
import me.esshd.hcf.eventgame.faction.ConquestFaction;
import me.esshd.hcf.eventgame.faction.KothFaction;
import me.esshd.hcf.eventgame.koth.KothExecutor;
import me.esshd.hcf.faction.FactionExecutor;
import me.esshd.hcf.faction.FactionManager;
import me.esshd.hcf.faction.FactionMember;
import me.esshd.hcf.faction.FlatFileFactionManager;
import me.esshd.hcf.faction.claim.Claim;
import me.esshd.hcf.faction.claim.ClaimHandler;
import me.esshd.hcf.faction.claim.ClaimWandListener;
import me.esshd.hcf.faction.claim.Subclaim;
import me.esshd.hcf.faction.type.*;
import me.esshd.hcf.listener.*;
import me.esshd.hcf.listener.fixes.*;
import me.esshd.hcf.pvpclass.PvpClassManager;
import me.esshd.hcf.pvpclass.bard.EffectRestorer;
import me.esshd.hcf.scoreboard.ScoreboardHandler;
import me.esshd.hcf.sotw.SotwCommand;
import me.esshd.hcf.sotw.SotwListener;
import me.esshd.hcf.sotw.SotwTimer;
import me.esshd.hcf.timer.TimerExecutor;
import me.esshd.hcf.timer.TimerManager;
import me.esshd.hcf.user.FactionUser;
import me.esshd.hcf.user.UserManager;
import me.esshd.hcf.visualise.ProtocolLibHook;
import me.esshd.hcf.visualise.VisualiseHandler;
import me.esshd.hcf.visualise.WallBorderListener;
import me.esshd.hcfold.EndListener;
import me.esshd.hcfold.EventSignListener;
import me.esshd.hcfold.MapKitCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HCF extends JavaPlugin {

    private static HCF instance;

    private HCF pl;
    private Random random = new Random();
    private ClaimHandler claimHandler;
    private CombatLogListener combatLogListener;
    private EconomyManager economyManager;
    private EffectRestorer effectRestorer;
    private EotwHandler eotwHandler;
    private EventScheduler eventScheduler;
    private FactionManager factionManager;
    private KeyManager keyManager;
    private PvpClassManager pvpClassManager;
    private ScoreboardHandler scoreboardHandler;
    private SotwTimer sotwTimer;
    private TimerManager timerManager;
    private UserManager userManager;
    private VisualiseHandler visualiseHandler;
    private WorldEditPlugin worldEdit;
    private FoundDiamondsListener foundDiamondsListener;

    public ArrayList<UUID> bypassing = new ArrayList<UUID>();

    @Override
    public void onEnable() {
        instance = this;
        BasePlugin.getPlugin().init(this);
        ProtocolLibHook.hook(this);

        new EffectRestorer(this);
        
        Plugin wep = getServer().getPluginManager().getPlugin("WorldEdit");
        this.worldEdit = wep instanceof WorldEditPlugin && wep.isEnabled() ? (WorldEditPlugin) wep : null;
        CustomEntityRegistration.registerCustomEntities();
        getCommand("hcf").setExecutor(new HcfCommand());
        ConfigurationService.init(this);
        this.effectRestorer = new EffectRestorer(this);
        this.registerConfiguration();
        this.registerCommands();
        this.registerManagers();
        this.registerListeners();

        new BukkitRunnable() {
            @Override
            public void run() {
                getServer().broadcast(ChatColor.GREEN.toString() + ChatColor.BOLD + "Saving!" + "\n" + ChatColor.GREEN + "Saved all faction and player data to the database." + "\n" + ChatColor.GRAY + "Current TPS: " + ChatColor.GREEN + TPS.getTPS() + ChatColor.GRAY, "hcf.seesaves");
                saveData();
            }
        }.runTaskTimerAsynchronously(instance, TimeUnit.MINUTES.toMillis(10L), TimeUnit.MINUTES.toMillis(10L));
        //AntiLeak.antiLeak();
        LocaleService.init(this);
    }

    private void saveData() {
        this.economyManager.saveEconomyData();
        this.factionManager.saveFactionData();
        this.keyManager.saveKeyData();
        this.timerManager.saveTimerData();
        this.userManager.saveUserData();
    }

    //   @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    // public void onPlayerQuit(PlayerQuitEvent event) {
    //   this.userManager.saveUserData();
    //}
    @Override
    public void onDisable() {
        CustomEntityRegistration.unregisterCustomEntities();
        CombatLogListener.removeCombatLoggers();
        this.pvpClassManager.onDisable();
        this.scoreboardHandler.clearBoards();
        this.factionManager.saveFactionData();
        this.economyManager.saveEconomyData();
        this.factionManager.saveFactionData();
        this.keyManager.saveKeyData();
        this.timerManager.saveTimerData();
        this.userManager.saveUserData();
        this.saveData();

        HCF.instance = null; // always initialise last
    }

    private void registerConfiguration() {
        ConfigurationSerialization.registerClass(CaptureZone.class);
        ConfigurationSerialization.registerClass(Claim.class);
        ConfigurationSerialization.registerClass(Subclaim.class);
        ConfigurationSerialization.registerClass(FactionUser.class);
        ConfigurationSerialization.registerClass(ClaimableFaction.class);
        ConfigurationSerialization.registerClass(ConquestFaction.class);
        ConfigurationSerialization.registerClass(CapturableFaction.class);
        ConfigurationSerialization.registerClass(KothFaction.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.class);
        ConfigurationSerialization.registerClass(Faction.class);
        ConfigurationSerialization.registerClass(FactionMember.class);
        ConfigurationSerialization.registerClass(PlayerFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.class);
        ConfigurationSerialization.registerClass(SpawnFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
    }

    private void registerListeners() {
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new AutoSmeltOreListener(), this);
        manager.registerEvents(new BlockHitFixListener(), this);
        manager.registerEvents(new BlockJumpGlitchFixListener(), this);
        manager.registerEvents(new BoatGlitchFixListener(), this);
        manager.registerEvents(new BookDeenchantListener(), this);
        manager.registerEvents(new BorderListener(), this);
        manager.registerEvents(new BottledExpListener(), this);
        manager.registerEvents(new ChatListener(this), this);
        manager.registerEvents(new ClaimWandListener(this), this);
        manager.registerEvents(new CombatLogListener(this), (Plugin) this);
        manager.registerEvents(new CoreListener(this), this);
        // manager.registerEvents(new CreativeClickListener(), this);
        manager.registerEvents(new CrowbarListener(this), this);
        manager.registerEvents(new HungerFixListener(), this);
        manager.registerEvents(new DeathListener(this), this);
        manager.registerEvents(new DeathMessageListener(this), this);
        manager.registerEvents(new DeathSignListener(this), this);
        manager.registerEvents(new EnchantLimitListener(), this);
        manager.registerEvents(new EnderChestRemovalListener(), this);
        manager.registerEvents(new EntityLimitListener(), this);
        manager.registerEvents(new EndListener(), this);
        manager.registerEvents(new EotwListener(this), this);
        manager.registerEvents(new EndPortalCommand(HCF.getPlugin()), this);
        manager.registerEvents(new EventSignListener(), this);
        manager.registerEvents(new ExpMultiplierListener(), this);
        manager.registerEvents(new FactionListener(this), this);
        manager.registerEvents(this.setFoundDiamondsListener(new FoundDiamondsListener(this)), this);
        manager.registerEvents(new InfinityArrowFixListener(), this);
        manager.registerEvents(new KeyListener(this), this);
        manager.registerEvents(new KeyEnderListener(this), this);
        manager.registerEvents(new KitMapListener(this), this);
        manager.registerEvents(new PearlGlitchListener(this), this);
        manager.registerEvents(new PortalListener(this), this);
        manager.registerEvents(new PotionLimitListener(), this);
        manager.registerEvents(new ProtectionListener(this), this);
        manager.registerEvents(new SignSubclaimListener(this), this);
        manager.registerEvents(new ShopSignListener(this), this);
        manager.registerEvents(new SkullListener(), this);
        manager.registerEvents(new SotwListener(this), this);
        manager.registerEvents(new BeaconStrengthFixListener(), this);
        manager.registerEvents(new KeyDupeFixListener(), this);
        manager.registerEvents(new VoidGlitchFixListener(), this);
        manager.registerEvents(new UnRepairableListener(), this);
        manager.registerEvents(new WallBorderListener(this), this);
        manager.registerEvents(new WorldListener(this), this);
    }

    private void registerCommands() {
 	
        this.getCommand("angle").setExecutor(new AngleCommand());
        this.getCommand("endportal").setExecutor(new EndPortalCommand(HCF.getPlugin()));
        this.getCommand("help").setExecutor(new HelpCommand());
        this.getCommand("conquest").setExecutor(new ConquestExecutor(this));
        this.getCommand("economy").setExecutor(new EconomyCommand(this));
        this.getCommand("eotw").setExecutor(new EotwCommand(this));
        this.getCommand("event").setExecutor(new EventExecutor(this));
        this.getCommand("faction").setExecutor(new FactionExecutor(this));
        this.getCommand("gopple").setExecutor(new GoppleCommand(this));
        this.getCommand("koth").setExecutor(new KothExecutor(this));
        this.getCommand("location").setExecutor(new LocationCommand(this));
        this.getCommand("logout").setExecutor(new LogoutCommand(this));
        this.getCommand("loot").setExecutor(new LootExecutor(this));
        this.getCommand("mapkit").setExecutor(new MapKitCommand(this));
        this.getCommand("pay").setExecutor(new PayCommand(this));
        this.getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
        this.getCommand("regen").setExecutor(new RegenCommand(this));
        this.getCommand("servertime").setExecutor(new ServerTimeCommand());
        this.getCommand("sotw").setExecutor(new SotwCommand(this));
        this.getCommand("spawncannon").setExecutor(new SpawnCannonCommand(this));
        this.getCommand("timer").setExecutor(new TimerExecutor(this));
        this.getCommand("togglefd").setExecutor(new ToggleFDNotifications());
        this.getCommand("togglecapzoneentry").setExecutor(new ToggleCapzoneEntryCommand(this));
        this.getCommand("togglelightning").setExecutor(new ToggleLightningCommand(this));
        this.getCommand("togglesidebar").setExecutor(new ToggleSidebarCommand(this));
        Addons.initialize(this);
    }

    private void registerManagers() {
        this.claimHandler = new ClaimHandler(this);
        this.economyManager = new FlatFileEconomyManager(this);
        this.eotwHandler = new EotwHandler(this);
        this.eventScheduler = new EventScheduler(this);
        this.factionManager = new FlatFileFactionManager(this);
        this.keyManager = new KeyManager(this);
        this.pvpClassManager = new PvpClassManager(this);
        this.sotwTimer = new SotwTimer();
        this.timerManager = new TimerManager(this); // needs to be registered before ScoreboardHandler
        this.scoreboardHandler = new ScoreboardHandler(this);
        this.userManager = new UserManager(this);
        this.visualiseHandler = new VisualiseHandler();
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public WorldEditPlugin getWorldEdit() {
        return this.worldEdit;
    }

    public SotwTimer getSotwTimer() {
        return this.sotwTimer;
    }

    public ClaimHandler getClaimHandler() {
        return this.claimHandler;
    }

    public FactionManager getFactionManager() {
        return this.factionManager;
    }

    public KeyManager getKeyManager() {
        return this.keyManager;
    }

    public Random getRandom() {
        return random;
    }

    public static HCF getPlugin() {
        return HCF.instance;
    }

    public CombatLogListener getCombatLogListener() {
        return combatLogListener;
    }

    public EotwHandler getEotwHandler() {
        return eotwHandler;
    }

    public PvpClassManager getPvpClassManager() {
        return this.pvpClassManager;
    }

    public EffectRestorer getEffectRestorer() {
        return effectRestorer;
    }

    public EventScheduler getEventScheduler() {
        return eventScheduler;
    }

    public ScoreboardHandler getScoreboardHandler() {
        return this.scoreboardHandler;
    }

    public TimerManager getTimerManager() {
        return this.timerManager;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public VisualiseHandler getVisualiseHandler() {
        return this.visualiseHandler;
    }

	public Object getDeathbanManager() {
		return this.getDeathbanManager();
	}

	public HCF getPl() {
		return pl;
	}

	public void setPl(HCF pl) {
		this.pl = pl;
	}

	public FoundDiamondsListener getFoundDiamondsListener() {
		return foundDiamondsListener;
	}

	public FoundDiamondsListener setFoundDiamondsListener(FoundDiamondsListener foundDiamondsListener) {
		this.foundDiamondsListener = foundDiamondsListener;
		return foundDiamondsListener;
	}
}
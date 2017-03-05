/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.math.RandomUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.block.Sign
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.configuration.serialization.ConfigurationSerialization
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package me.esshd.api.main;

import java.io.IOException;
import java.util.Random;
import me.esshd.api.main.PlayTimeManager;
import me.esshd.api.main.ServerHandler;
import me.esshd.api.main.cmds.BaseCommandModule;
import me.esshd.api.main.cmds.CommandManager;
import me.esshd.api.main.cmds.SimpleCommandManager;
import me.esshd.api.main.cmds.modules.ChatModule;
import me.esshd.api.main.cmds.modules.EssentialModule;
import me.esshd.api.main.cmds.modules.InventoryModule;
import me.esshd.api.main.cmds.modules.TeleportModule;
import me.esshd.api.main.cmds.modules.essential.ReportCommand;
import me.esshd.api.main.kit.FlatFileKitManager;
import me.esshd.api.main.kit.Kit;
import me.esshd.api.main.kit.KitExecutor;
import me.esshd.api.main.kit.KitListener;
import me.esshd.api.main.kit.KitManager;
import me.esshd.api.main.listener.ChatListener;
import me.esshd.api.main.listener.ColouredSignListener;
import me.esshd.api.main.listener.DecreasedLagListener;
import me.esshd.api.main.listener.JoinListener;
import me.esshd.api.main.listener.MoveByBlockEvent;
import me.esshd.api.main.listener.NameVerifyListener;
import me.esshd.api.main.listener.VanishListener;
import me.esshd.api.main.task.AnnouncementHandler;
import me.esshd.api.main.task.ClearEntityHandler;
import me.esshd.api.main.user.BaseUser;
import me.esshd.api.main.user.ConsoleUser;
import me.esshd.api.main.user.NameHistory;
import me.esshd.api.main.user.ServerParticipator;
import me.esshd.api.main.user.UserManager;
import me.esshd.api.main.warp.FlatFileWarpManager;
import me.esshd.api.main.warp.Warp;
import me.esshd.api.main.warp.WarpManager;
import me.esshd.api.utils.PersistableLocation;
import me.esshd.api.utils.SignHandler;
import me.esshd.api.utils.chat.Lang;
import me.esshd.api.utils.cuboid.Cuboid;
import me.esshd.api.utils.cuboid.NamedCuboid;
import me.esshd.api.utils.itemdb.ItemDb;
import me.esshd.api.utils.itemdb.SimpleItemDb;
import me.esshd.api.utils.server.TPS;
import me.esshd.hcf.HCF;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class BasePlugin {
    private Random random = new Random();
    private ItemDb itemDb;
    private SignHandler signHandler;
    private static BasePlugin plugin = new BasePlugin();
    private JavaPlugin javaPlugin;
    private WarpManager warpManager;
    private RandomUtils randomUtils;
    public BukkitRunnable clearEntityHandler;
    public BukkitRunnable announcementTask;
    private CommandManager commandManager;
    private KitManager kitManager;
    private PlayTimeManager playTimeManager;
    private ServerHandler serverHandler;
    private UserManager userManager;
    private KitExecutor kitExecutor;

    private BasePlugin() {
    }

    public void init(JavaPlugin plugin) {
        this.javaPlugin = plugin;
        ConfigurationSerialization.registerClass(Warp.class);
        ConfigurationSerialization.registerClass(ServerParticipator.class);
        ConfigurationSerialization.registerClass(BaseUser.class);
        ConfigurationSerialization.registerClass(ConsoleUser.class);
        ConfigurationSerialization.registerClass(NameHistory.class);
        ConfigurationSerialization.registerClass(PersistableLocation.class);
        ConfigurationSerialization.registerClass(Cuboid.class);
        ConfigurationSerialization.registerClass(NamedCuboid.class);
        ConfigurationSerialization.registerClass(Kit.class);
        this.registerManagers();
        this.registerCommands();
        this.registerListeners();
        this.reloadSchedulers();
        try {
            Lang.initialize("en_US");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, (Runnable)new TPS(), 100, 1);
    }

    public void disable() {
        this.randomUtils = new RandomUtils();
        this.kitManager = new FlatFileKitManager(HCF.getPlugin());
        this.serverHandler = new ServerHandler(this);
        this.signHandler = new SignHandler(this.javaPlugin);
        this.userManager = new UserManager(this.javaPlugin);
        this.itemDb = new SimpleItemDb(this.javaPlugin);
        this.warpManager = new FlatFileWarpManager(this.javaPlugin);
        this.signHandler.cancelTasks(null);
        this.javaPlugin = null;
        plugin = null;
        try {
            Lang.initialize("en_US");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void registerManagers() {
        this.randomUtils = new RandomUtils();
        this.kitManager = new FlatFileKitManager(HCF.getPlugin());
        this.serverHandler = new ServerHandler(this);
        this.signHandler = new SignHandler(this.javaPlugin);
        this.userManager = new UserManager(this.javaPlugin);
        this.itemDb = new SimpleItemDb(this.javaPlugin);
        this.warpManager = new FlatFileWarpManager(this.javaPlugin);
    }

    private void registerCommands() {
        this.commandManager = new SimpleCommandManager(HCF.getPlugin());
        this.commandManager.registerAll(new ChatModule(this));
        this.commandManager.registerAll(new EssentialModule(this));
        this.commandManager.registerAll(new InventoryModule(this));
        this.commandManager.registerAll(new TeleportModule(this));
        this.kitExecutor = new KitExecutor(this);
        HCF.getPlugin().getCommand("kit").setExecutor((CommandExecutor)this.kitExecutor);
    }

    public KitExecutor getKitExecutor() {
        return this.kitExecutor;
    }

    private void registerListeners() {
        PluginManager manager = HCF.getPlugin().getServer().getPluginManager();
        manager.registerEvents((Listener)new ChatListener(this), (Plugin)this.javaPlugin);
        manager.registerEvents((Listener)new ColouredSignListener(), (Plugin)this.javaPlugin);
        manager.registerEvents((Listener)new DecreasedLagListener(this), (Plugin)this.javaPlugin);
        manager.registerEvents((Listener)new JoinListener(this), (Plugin)this.javaPlugin);
        manager.registerEvents((Listener)new ReportCommand(), (Plugin)this.javaPlugin);
        manager.registerEvents((Listener)new KitListener(this), (Plugin)this.javaPlugin);
        manager.registerEvents((Listener)new MoveByBlockEvent(), (Plugin)this.javaPlugin);
        manager.registerEvents((Listener)new NameVerifyListener(this), (Plugin)this.javaPlugin);
        this.playTimeManager = new PlayTimeManager(HCF.getPlugin());
        manager.registerEvents((Listener)this.playTimeManager, (Plugin)this.javaPlugin);
        manager.registerEvents((Listener)new VanishListener(this), (Plugin)this.javaPlugin);
    }

    public void reloadSchedulers() {
        if (this.clearEntityHandler != null) {
            this.clearEntityHandler.cancel();
        }
        if (this.announcementTask != null) {
            this.announcementTask.cancel();
        }
        long announcementDelay = (long)this.serverHandler.getAnnouncementDelay() * 20;
        long claggdelay = (long)this.serverHandler.getClaggDelay() * 20;
        AnnouncementHandler announcementTask = new AnnouncementHandler(this);
        this.announcementTask = announcementTask;
        ClearEntityHandler clearEntityHandler = new ClearEntityHandler();
        this.clearEntityHandler = clearEntityHandler;
        clearEntityHandler.runTaskTimer((Plugin)HCF.getPlugin(), claggdelay, claggdelay);
        announcementTask.runTaskTimer((Plugin)HCF.getPlugin(), announcementDelay, announcementDelay);
    }

    public WarpManager getWarpManager() {
        return this.warpManager;
    }

    public RandomUtils getRandomUtils() {
        return this.randomUtils;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public KitManager getKitManager() {
        return this.kitManager;
    }

    public PlayTimeManager getPlayTimeManager() {
        return this.playTimeManager;
    }

    public ServerHandler getServerHandler() {
        return this.serverHandler;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public Random getRandom() {
        return this.random;
    }

    public ItemDb getItemDb() {
        return this.itemDb;
    }

    public SignHandler getSignHandler() {
        return this.signHandler;
    }

    public static BasePlugin getPlugin() {
        return plugin;
    }

    public JavaPlugin getJavaPlugin() {
        return this.javaPlugin;
    }
}


/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.esshd.api.main;

import java.util.ArrayList;
import java.util.List;
import me.esshd.api.main.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerHandler {
    private final List<String> announcements = new ArrayList<String>();
    private final List<String> serverRules = new ArrayList<String>();
    private final BasePlugin plugin;
    private int clearlagdelay;
    public boolean useProtocolLib;
    private int announcementDelay;
    private long chatSlowedMillis;
    private long chatDisabledMillis;
    private int chatSlowedDelay;
    private String broadcastFormat;
    private FileConfiguration config;
    private boolean decreasedLagMode;
    private int worldBorder;
    private boolean end;
    private Location endExit;
    private boolean donorOnly;
    private int netherBorder;
    private int endBorder;

    public ServerHandler(BasePlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getJavaPlugin().getConfig();
        this.reloadServerData();
    }

    public void setServerBorder(World.Environment environment, Integer integer) {
        if (environment.equals((Object)World.Environment.NORMAL)) {
            this.worldBorder = integer;
        } else if (environment.equals((Object)World.Environment.NETHER)) {
            this.netherBorder = integer;
        } else if (environment.equals((Object)World.Environment.THE_END)) {
            this.endBorder = integer;
        }
    }

    public void setDonorOnly(boolean value) {
        this.donorOnly = value;
    }

    public boolean isDonorOnly() {
        return this.donorOnly;
    }

    public void setEndExit(Location location) {
        this.endExit = location;
    }

    public Location getEndExit() {
        return this.endExit;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public boolean isEnd() {
        return this.end;
    }

    public Integer getWorldBorder() {
        return this.worldBorder;
    }

    public Integer getNetherBorder() {
        return this.netherBorder;
    }

    public Integer getEndBorder() {
        return this.endBorder;
    }

    public int getAnnouncementDelay() {
        return this.announcementDelay;
    }

    public void setClearlagdelay(Integer integer) {
        this.clearlagdelay = integer;
    }

    public int getClaggDelay() {
        return this.clearlagdelay;
    }

    public void setAnnouncementDelay(int delay) {
        this.announcementDelay = delay;
    }

    public List<String> getAnnouncements() {
        return this.announcements;
    }

    public boolean isChatSlowed() {
        if (this.getRemainingChatSlowedMillis() > 0) {
            return true;
        }
        return false;
    }

    public long getChatSlowedMillis() {
        return this.chatSlowedMillis;
    }

    public void setChatSlowedMillis(long ticks) {
        this.chatSlowedMillis = System.currentTimeMillis() + ticks;
    }

    public long getRemainingChatSlowedMillis() {
        return this.chatSlowedMillis - System.currentTimeMillis();
    }

    public boolean isChatDisabled() {
        if (this.getRemainingChatDisabledMillis() > 0) {
            return true;
        }
        return false;
    }

    public long getChatDisabledMillis() {
        return this.chatDisabledMillis;
    }

    public void setChatDisabledMillis(long ticks) {
        long millis = System.currentTimeMillis();
        this.chatDisabledMillis = millis + ticks;
    }

    public long getRemainingChatDisabledMillis() {
        return this.chatDisabledMillis - System.currentTimeMillis();
    }

    public int getChatSlowedDelay() {
        return this.chatSlowedDelay;
    }

    public void setChatSlowedDelay(int delay) {
        this.chatSlowedDelay = delay;
    }

    public String getBroadcastFormat() {
        return this.broadcastFormat;
    }

    public void setBroadcastFormat(String broadcastFormat) {
        this.broadcastFormat = broadcastFormat;
    }

    public boolean isDecreasedLagMode() {
        return this.decreasedLagMode;
    }

    public void setDecreasedLagMode(boolean decreasedLagMode) {
        this.decreasedLagMode = decreasedLagMode;
    }

    public void reloadServerData() {
        Location location;
        this.plugin.getJavaPlugin().reloadConfig();
        this.config = this.plugin.getJavaPlugin().getConfig();
        this.endExit = location = new Location(Bukkit.getWorld((String)"world"), this.config.getDouble("end.exitLocation.x"), this.config.getDouble("end.exitLocation.y"), this.config.getDouble("end.exitLocation.z"));
        this.donorOnly = this.config.getBoolean("donor-only-enter");
        this.end = this.config.getBoolean("end-open");
        this.worldBorder = this.config.getInt("border.worldBorder", 3000);
        this.netherBorder = this.config.getInt("border.netherBorder", 1000);
        this.endBorder = this.config.getInt("border.endBorder", 1500);
        this.serverRules.clear();
        this.clearlagdelay = this.config.getInt("clearlag.delay", 200);
        this.announcementDelay = this.config.getInt("announcements.delay", 15);
        this.announcements.clear();
        for (String each : this.config.getStringList("announcements.list")) {
            this.announcements.add(ChatColor.translateAlternateColorCodes((char)'&', (String)each));
        }
        this.chatDisabledMillis = this.config.getLong("chat.disabled.millis", 0);
        this.chatSlowedMillis = this.config.getLong("chat.slowed.millis", 0);
        this.chatSlowedDelay = this.config.getInt("chat.slowed.delay", 15);
        this.useProtocolLib = this.config.getBoolean("use-protocol-lib", true);
        this.decreasedLagMode = this.config.getBoolean("decreased-lag-mode");
        this.broadcastFormat = ChatColor.translateAlternateColorCodes((char)'&', (String)this.config.getString("broadcast.format", (Object)ChatColor.GRAY + "[" + (Object)ChatColor.LIGHT_PURPLE + "HCF" + (Object)ChatColor.GRAY + "]" + (Object)ChatColor.GRAY + " &7%1$s"));
    }

    public void saveServerData() {
        this.config.set("clearlag.delay", (Object)this.clearlagdelay);
        this.config.set("server-rules", this.serverRules);
        this.config.set("use-protocol-lib", (Object)this.useProtocolLib);
        this.config.set("chat.disabled.millis", (Object)this.chatDisabledMillis);
        this.config.set("chat.slowed.millis", (Object)this.chatSlowedMillis);
        this.config.set("chat.slowed-delay", (Object)this.chatSlowedDelay);
        this.config.set("announcements.delay", (Object)this.announcementDelay);
        this.config.set("announcements.list", this.announcements);
        this.config.set("decreased-lag-mode", (Object)this.decreasedLagMode);
        this.config.set("end.exitLocation.x", (Object)this.endExit.getX());
        this.config.set("end.exitLocation.y", (Object)this.endExit.getY());
        this.config.set("end.exitLocation.z", (Object)this.endExit.getZ());
        this.config.set("donor-only-enter", (Object)this.donorOnly);
        this.config.set("end-open", (Object)this.end);
        this.config.set("border.worldBorder", (Object)this.worldBorder);
        this.config.set("border.netherBorder", (Object)this.netherBorder);
        this.config.set("border.endBorder", (Object)this.endBorder);
        this.plugin.getJavaPlugin().saveConfig();
    }
}


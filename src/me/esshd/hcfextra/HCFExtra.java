/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.esshd.hcfextra;

import me.esshd.hcfextra.Configuration;
import me.esshd.hcfextra.command.HCFExtraCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class HCFExtra
extends JavaPlugin {
    private static HCFExtra plugin;
    private Configuration configuration;

    public void onEnable() {
        plugin = this;
        this.configuration = new Configuration(this);
        this.configuration.reload();
        this.getCommand("hcfextra").setExecutor((CommandExecutor)new HCFExtraCommand(this));
    }

    public void onDisable() {
        plugin = null;
    }

    public static HCFExtra getPlugin() {
        return plugin;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }
}


/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package me.esshd.api.main.cmds;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.main.cmds.BaseCommandModule;
import me.esshd.api.main.cmds.CommandManager;
import me.esshd.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SimpleCommandManager
implements CommandManager {
    private static final String PERMISSION_MESSAGE = (Object)ChatColor.RED + "Insufficient permissions to perform this command.";
    private final Map<String, BaseCommand> commandMap = new HashMap<String, BaseCommand>();

    public SimpleCommandManager(final HCF plugin) {
        final ConsoleCommandSender console = plugin.getServer().getConsoleSender();
        new BukkitRunnable(){

            public void run() {
                Collection<BaseCommand> commands = SimpleCommandManager.this.commandMap.values();
                for (BaseCommand command : commands) {
                    String commandName = command.getName();
                    PluginCommand pluginCommand = plugin.getCommand(commandName);
                    if (pluginCommand == null) {
                        Bukkit.broadcastMessage((String)commandName);
                        console.sendMessage(String.valueOf('[') + plugin.getName() + "] " + (Object)ChatColor.YELLOW + "Failed to register command '" + commandName + "'.");
                        console.sendMessage(String.valueOf('[') + plugin.getName() + "] " + (Object)ChatColor.YELLOW + "Reason: Undefined in plugin.yml.");
                        continue;
                    }
                    pluginCommand.setAliases(Arrays.asList(command.getAliases()));
                    pluginCommand.setDescription(command.getDescription());
                    pluginCommand.setExecutor((CommandExecutor)command);
                    pluginCommand.setTabCompleter((TabCompleter)command);
                    pluginCommand.setUsage(command.getUsage());
                    pluginCommand.setPermission("command." + command.getName());
                    pluginCommand.setPermissionMessage(PERMISSION_MESSAGE);
                }
            }
        }.runTask((Plugin)plugin);
    }

    @Override
    public boolean containsCommand(BaseCommand command) {
        return this.commandMap.containsValue(command);
    }

    @Override
    public void registerAll(BaseCommandModule module) {
        if (module.isEnabled()) {
            Set<BaseCommand> commands = module.getCommands();
            for (BaseCommand command : commands) {
                this.commandMap.put(command.getName(), command);
            }
        }
    }

    @Override
    public void registerCommand(BaseCommand command) {
        this.commandMap.put(command.getName(), command);
    }

    @Override
    public void registerCommands(BaseCommand[] commands) {
        BaseCommand[] arrayOfBaseCommand = commands;
        int j = arrayOfBaseCommand.length;
        int i = 0;
        while (i < j) {
            BaseCommand command = arrayOfBaseCommand[i];
            this.commandMap.put(command.getName(), command);
            ++i;
        }
    }

    @Override
    public void unregisterCommand(BaseCommand command) {
        this.commandMap.values().remove(command);
    }

    @Override
    public BaseCommand getCommand(String id) {
        return this.commandMap.get(id);
    }

}


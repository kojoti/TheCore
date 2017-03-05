/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.SimplePluginManager
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package me.esshd.api.main.cmds;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.bukkit.command.CommandMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class ReflectionCommandManager
implements CommandManager {
    private static final String PERMISSION_MESSAGE = (Object)ChatColor.RED + "You do not have permission for this command.";
    private final Map<String, BaseCommand> commandMap = new HashMap<String, BaseCommand>();

    public ReflectionCommandManager(final HCF plugin) {
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        final Server server = Bukkit.getServer();
        server.getScheduler().runTaskLater((Plugin)plugin, new Runnable(){

            @Override
            public void run() {
                Optional optionalCommandMap = ReflectionCommandManager.this.getCommandMap(server);
                if (!optionalCommandMap.isPresent()) {
                    Bukkit.broadcastMessage((String)(String.valueOf('[') + plugin.getDescription().getFullName() + "] Command map not found"));
                    console.sendMessage(String.valueOf('[') + plugin.getDescription().getFullName() + "] Command map not found");
                    return;
                }
                CommandMap bukkitCommandMap = (CommandMap)optionalCommandMap.get();
                for (BaseCommand command : ReflectionCommandManager.this.commandMap.values()) {
                    String commandName = command.getName();
                    Optional optional = ReflectionCommandManager.this.getPluginCommand(commandName, (Plugin)plugin);
                    if (optional.isPresent()) {
                        PluginCommand pluginCommand = (PluginCommand)optional.get();
                        pluginCommand.setAliases(Arrays.asList(command.getAliases()));
                        pluginCommand.setDescription(command.getDescription());
                        pluginCommand.setExecutor((CommandExecutor)command);
                        pluginCommand.setTabCompleter((TabCompleter)command);
                        pluginCommand.setUsage(command.getUsage());
                        pluginCommand.setPermission(command.getPermission());
                        pluginCommand.setPermissionMessage(PERMISSION_MESSAGE);
                        bukkitCommandMap.register(plugin.getDescription().getName(), (Command)pluginCommand);
                        continue;
                    }
                    Bukkit.broadcastMessage((String)(String.valueOf('[') + plugin.getName() + "] " + (Object)ChatColor.YELLOW + "Failed to register command '" + commandName + "'."));
                    console.sendMessage(String.valueOf('[') + plugin.getName() + "] " + (Object)ChatColor.YELLOW + "Failed to register command '" + commandName + "'.");
                }
            }
        }, 1);
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

    private Optional<PluginCommand> getPluginCommand(String name, Plugin plugin) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            return Optional.of(constructor.newInstance(new Object[]{name, plugin}));
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException constructor) {
            return Optional.empty();
        }
    }

    private Optional<CommandMap> getCommandMap(Server server) {
        PluginManager pluginManager = server.getPluginManager();
        if (pluginManager instanceof SimplePluginManager) {
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                Optional<Object> optional = Optional.of(field.get((Object)pluginManager));
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex3) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

}


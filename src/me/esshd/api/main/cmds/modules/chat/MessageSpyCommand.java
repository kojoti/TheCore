/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang.StringUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.esshd.api.main.cmds.modules.chat;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.main.user.ServerParticipator;
import me.esshd.api.main.user.UserManager;
import me.esshd.api.utils.BukkitUtils;
import me.esshd.api.utils.JavaUtils;
import me.esshd.api.utils.cmds.CommandArgument;
import me.esshd.api.utils.cmds.CommandWrapper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageSpyCommand
extends BaseCommand {
    private final CommandWrapper handler;

    public MessageSpyCommand(BasePlugin plugin) {
        super("socialspy", "Spies on the PM's of a player.");
        this.setUsage("/(command) <list|add|del|clear> [playerName]");
        ArrayList<CommandArgument> arguments = new ArrayList<CommandArgument>(4);
        arguments.add(new MessageSpyListArgument(plugin));
        arguments.add(new IgnoreClearArgument(plugin));
        arguments.add(new MessageSpyAddArgument(plugin));
        arguments.add(new MessageSpyDeleteArgument(plugin));
        Collections.sort(arguments, new CommandWrapper.ArgumentComparator());
        this.handler = new CommandWrapper(arguments);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.handler.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return this.handler.onTabComplete(sender, command, label, args);
    }

    private static class IgnoreClearArgument
    extends CommandArgument {
        private final BasePlugin plugin;

        public IgnoreClearArgument(BasePlugin plugin) {
            super("clear", "Clears your current spy list.");
            this.plugin = plugin;
        }

        @Override
        public String getUsage(String label) {
            return String.valueOf(String.valueOf('/')) + label + ' ' + this.getName();
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            ServerParticipator participator = this.plugin.getUserManager().getParticipator(sender);
            if (participator == null) {
                sender.sendMessage((Object)ChatColor.RED + "You are not able to message spy.");
                return true;
            }
            participator.getMessageSpying().clear();
            sender.sendMessage((Object)ChatColor.YELLOW + "You are no longer spying the messages of anyone.");
            return true;
        }
    }

    private static class MessageSpyAddArgument
    extends CommandArgument {
        private final BasePlugin plugin;

        public MessageSpyAddArgument(BasePlugin plugin) {
            super("add", "Adds a player to your message spy list.");
            this.plugin = plugin;
        }

        @Override
        public String getUsage(String label) {
            return String.valueOf(String.valueOf('/')) + label + ' ' + this.getName() + " <all|playerName>";
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            ServerParticipator participator = this.plugin.getUserManager().getParticipator(sender);
            if (participator == null) {
                sender.sendMessage((Object)ChatColor.RED + "You are not able to message spy.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
                return true;
            }
            Set<String> messageSpying = participator.getMessageSpying();
            boolean all = messageSpying.contains("all");
            if (all || JavaUtils.containsIgnoreCase(messageSpying, args[1])) {
                sender.sendMessage((Object)ChatColor.RED + "You are already spying on the messages of " + (all ? "all players" : args[1]) + '.');
                return true;
            }
            if (args[1].equalsIgnoreCase("all")) {
                messageSpying.clear();
                messageSpying.add("all");
                sender.sendMessage((Object)ChatColor.GREEN + "You are now spying on the messages of all players.");
                return true;
            }
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer((String)args[1]);
            if (!offlineTarget.hasPlayedBefore() && offlineTarget.getPlayer() == null) {
                sender.sendMessage((Object)ChatColor.GOLD + "Player '" + (Object)ChatColor.WHITE + args[1] + (Object)ChatColor.GOLD + "' not found.");
                return true;
            }
            if (offlineTarget.equals((Object)sender)) {
                sender.sendMessage((Object)ChatColor.RED + "You cannot spy on the messages of yourself.");
                return true;
            }
            sender.sendMessage((Object)ChatColor.YELLOW + "You are " + (messageSpying.add(offlineTarget.getUniqueId().toString()) ? new StringBuilder().append((Object)ChatColor.GREEN).append("now").toString() : new StringBuilder().append((Object)ChatColor.RED).append("already").toString()) + (Object)ChatColor.YELLOW + " spying on the messages of " + offlineTarget.getName() + '.');
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            return args.length == 2 ? null : Collections.emptyList();
        }
    }

    private static class MessageSpyDeleteArgument
    extends CommandArgument {
        private final BasePlugin plugin;

        public MessageSpyDeleteArgument(BasePlugin plugin) {
            super("delete", "Deletes a player from your message spy list.");
            this.plugin = plugin;
            this.aliases = new String[]{"del", "remove"};
        }

        @Override
        public String getUsage(String label) {
            return String.valueOf(String.valueOf('/')) + label + ' ' + this.getName() + " <playerName>";
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            ServerParticipator participator = this.plugin.getUserManager().getParticipator(sender);
            if (participator == null) {
                sender.sendMessage((Object)ChatColor.RED + "You are not able to message spy.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage((Object)ChatColor.RED + "Usage: " + this.getUsage(label));
                return true;
            }
            Set<String> messageSpying = participator.getMessageSpying();
            if (args[1].equalsIgnoreCase("all")) {
                messageSpying.remove("all");
                sender.sendMessage((Object)ChatColor.RED + "You are no longer spying on the messages of all players.");
                return true;
            }
            OfflinePlayer offlineTarget = BukkitUtils.offlinePlayerWithNameOrUUID(args[1]);
            if (!offlineTarget.hasPlayedBefore() && !offlineTarget.isOnline()) {
                sender.sendMessage((Object)ChatColor.GOLD + "Player named or with UUID '" + (Object)ChatColor.WHITE + args[1] + (Object)ChatColor.GOLD + "' not found.");
                return true;
            }
            sender.sendMessage("You are " + (messageSpying.remove(offlineTarget.getUniqueId().toString()) ? new StringBuilder().append((Object)ChatColor.GREEN).append("no longer").toString() : new StringBuilder().append((Object)ChatColor.RED).append("still not").toString()) + (Object)ChatColor.YELLOW + " spying on the messages of " + offlineTarget.getName() + '.');
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            return args.length == 2 ? null : Collections.emptyList();
        }
    }

    private static class MessageSpyListArgument
    extends CommandArgument {
        private final BasePlugin plugin;

        public MessageSpyListArgument(BasePlugin plugin) {
            super("list", "Lists all players you're spying on.");
            this.plugin = plugin;
        }

        @Override
        public String getUsage(String label) {
            return String.valueOf(String.valueOf('/')) + label + ' ' + this.getName();
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            ServerParticipator participator = this.plugin.getUserManager().getParticipator(sender);
            if (participator == null) {
                sender.sendMessage((Object)ChatColor.RED + "You are not able to message spy.");
                return true;
            }
            LinkedHashSet<String> spyingNames = new LinkedHashSet<String>();
            Set<String> messageSpying = participator.getMessageSpying();
            if (messageSpying.size() == 1 && ((String)Iterables.getOnlyElement(messageSpying)).equals("all")) {
                sender.sendMessage((Object)ChatColor.GRAY + "You are currently spying on the messages of all players.");
                return true;
            }
            for (String spyingId : messageSpying) {
                String name = Bukkit.getOfflinePlayer((UUID)UUID.fromString(spyingId)).getName();
                if (name == null) continue;
                spyingNames.add(name);
            }
            if (spyingNames.isEmpty()) {
                sender.sendMessage((Object)ChatColor.RED + "You are not spying on the messages of any players.");
                return true;
            }
            sender.sendMessage((Object)ChatColor.GRAY + "You are currently spying on the messages of (" + spyingNames.size() + " players): " + (Object)ChatColor.RED + StringUtils.join(spyingNames, (String)new StringBuilder(String.valueOf(ChatColor.GRAY.toString())).append(", ").append((Object)ChatColor.RED).toString()) + (Object)ChatColor.GRAY + '.');
            return true;
        }
    }

}


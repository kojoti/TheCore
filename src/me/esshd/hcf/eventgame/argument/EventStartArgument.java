package me.esshd.hcf.eventgame.argument;

import me.esshd.api.utils.cmds.CommandArgument;
import me.esshd.hcf.HCF;
import me.esshd.hcf.eventgame.faction.EventFaction;
import me.esshd.hcf.faction.type.Faction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link CommandArgument} used for starting an event.
 */
public class EventStartArgument extends CommandArgument {

    private final HCF plugin;

    public EventStartArgument(HCF plugin) {
        super("start", "Starts an event");
        this.plugin = plugin;
        this.aliases = new String[] { "begin" };
        this.permission = "hcf.command.event.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <eventName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	
    	if (!sender.hasPermission("Core.staff.advanced")) {
			sender.sendMessage(ChatColor.RED + "No Permission.");
			return true;
		}
    	
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Faction faction = plugin.getFactionManager().getFaction(args[1]);

        if (!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
            return true;
        }

        if (plugin.getTimerManager().getEventTimer().tryContesting(((EventFaction) faction), sender)) {
            sender.sendMessage(ChatColor.YELLOW + "Successfully contested " + faction.getName() + '.');
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        return plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
    }
}

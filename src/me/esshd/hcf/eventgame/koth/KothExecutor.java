package me.esshd.hcf.eventgame.koth;

import me.esshd.api.utils.cmds.ArgumentExecutor;
import me.esshd.hcf.HCF;
import me.esshd.hcf.eventgame.koth.argument.KothHelpArgument;
import me.esshd.hcf.eventgame.koth.argument.KothNextArgument;
import me.esshd.hcf.eventgame.koth.argument.KothScheduleArgument;
import me.esshd.hcf.eventgame.koth.argument.KothSetCapDelayArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to handle KingOfTheHills.
 */
public class KothExecutor extends ArgumentExecutor {

    private final KothScheduleArgument kothScheduleArgument;

    public KothExecutor(HCF plugin) {
        super("koth");

        addArgument(new KothHelpArgument(this));
        addArgument(new KothNextArgument(plugin));
        addArgument(this.kothScheduleArgument = new KothScheduleArgument(plugin));
        addArgument(new KothSetCapDelayArgument(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	
    	if (!sender.hasPermission("Core.staff.advanced")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
		}
    	
    	
        if (args.length < 1) {
            this.kothScheduleArgument.onCommand(sender, command, label, args);
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }
}

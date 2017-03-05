package me.esshd.hcf.eventgame.crate;



import me.esshd.api.utils.BukkitUtils;
import me.esshd.api.utils.cmds.ArgumentExecutor;
import me.esshd.api.utils.cmds.CommandArgument;
import me.esshd.hcf.HCF;
import me.esshd.hcf.eventgame.crate.argument.LootGiveArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LootExecutor extends ArgumentExecutor
{
    public LootExecutor(final HCF plugin) {
        super("loot");
        this.addArgument((CommandArgument)new LootGiveArgument(plugin));
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.GRAY +BukkitUtils.STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Loot Help" + ChatColor.WHITE + " - " + ChatColor.GRAY + "All information regarding loot commands.") ;

                if(sender.hasPermission("loot.admin")) {
                    sender.sendMessage(ChatColor.YELLOW + "/loot give " + ChatColor.YELLOW + "[playerName] [type] [amount]" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Give a crate key to a player.");


                }

                sender.sendMessage(ChatColor.GRAY +BukkitUtils.STRAIGHT_LINE_DEFAULT);



            return true;
        }
        final CommandArgument argument2 = this.getArgument(args[0]);
        final String permission2 = (argument2 == null) ? null : argument2.getPermission();
        if (argument2 == null || (permission2 != null && !sender.hasPermission(permission2))) {
            sender.sendMessage(ChatColor.RED + "Command not found");
            return true;
        }
        argument2.onCommand(sender, command, label, args);
        return true;
    }
}

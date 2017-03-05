package me.esshd.hcf.faction.argument.staff;

import me.esshd.api.utils.cmds.CommandArgument;
import me.esshd.hcf.HCF;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionBypassArgument extends CommandArgument {

    public FactionBypassArgument(HCF plugin) {
        super("bypass", "Bypass player restrictions", new String[]{"admin"});
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("thecore.staff.bypass"))) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (HCF.getPlugin().bypassing.contains(player.getUniqueId())) {
            HCF.getPlugin().bypassing.remove(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "Faction bypass disabled.");
        } else {
            HCF.getPlugin().bypassing.add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Faction bypass enabled.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}


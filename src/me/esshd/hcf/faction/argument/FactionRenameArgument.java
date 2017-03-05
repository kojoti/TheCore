package me.esshd.hcf.faction.argument;

import me.esshd.api.utils.JavaUtils;
import me.esshd.api.utils.cmds.CommandArgument;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.HCF;
import me.esshd.hcf.faction.struct.Role;
import me.esshd.hcf.faction.type.PlayerFaction;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class FactionRenameArgument extends CommandArgument {

    private static final long FACTION_RENAME_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(30L);
    private static final String FACTION_RENAME_DELAY_WORDS = DurationFormatUtils.formatDurationWords(FACTION_RENAME_DELAY_MILLIS, true, true);

    private final HCF plugin;

    public FactionRenameArgument(HCF plugin) {
        super("rename", "Change the name of your faction.");
        this.plugin = plugin;
        this.aliases = new String[]{"changename", "setname"};
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <newFactionName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can create faction.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction leader to edit the name.");
            return true;
        }

        String newName = args[1];

        if (ConfigurationService.DISALLOWED_FACTION_NAMES.contains(newName.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "'" + newName + "' is a blocked faction name. Please try another faction name.");
            return true;
        }

        if (newName.length() < ConfigurationService.FACTION_NAME_CHARACTERS_MIN) {
            sender.sendMessage(ChatColor.RED + "Faction names must have at least " + ConfigurationService.FACTION_NAME_CHARACTERS_MIN + " characters.");
            return true;
        }

        if (newName.length() > ConfigurationService.FACTION_NAME_CHARACTERS_MAX) {
            sender.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + ConfigurationService.FACTION_NAME_CHARACTERS_MAX + " characters.");
            return true;
        }

        if (!JavaUtils.isAlphanumeric(newName)) {
            sender.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric. Ex: 1-9, A-Z");
            return true;
        }

        if (plugin.getFactionManager().getFaction(newName) != null) {
            sender.sendMessage(ChatColor.RED + "The faction " + newName + ChatColor.RED + " already exists.");
            return true;
        }

        long difference = (playerFaction.lastRenameMillis - System.currentTimeMillis()) + FACTION_RENAME_DELAY_MILLIS;

        if (!player.isOp() && difference > 0L) {
            player.sendMessage(ChatColor.RED + "There is a faction rename delay of " + FACTION_RENAME_DELAY_WORDS + ". Therefore you need to wait another "
                    + DurationFormatUtils.formatDurationWords(difference, true, true) + " to rename your faction.");

            return true;
        }

        playerFaction.setName(args[1], sender);
        return true;
    }
}

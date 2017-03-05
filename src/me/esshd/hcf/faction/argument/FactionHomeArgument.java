package me.esshd.hcf.faction.argument;

import me.esshd.api.utils.cmds.CommandArgument;
import me.esshd.hcf.DurationFormatter;
import me.esshd.hcf.HCF;
import me.esshd.hcf.eventgame.faction.EventFaction;
import me.esshd.hcf.faction.FactionExecutor;
import me.esshd.hcf.faction.type.Faction;
import me.esshd.hcf.faction.type.PlayerFaction;
import me.esshd.hcf.timer.PlayerTimer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

/**
 * Faction argument used to teleport to {@link Faction} home {@link Location}s.
 */
public class FactionHomeArgument extends CommandArgument {

    private final FactionExecutor factionExecutor;
    private final HCF plugin;

    public FactionHomeArgument(FactionExecutor factionExecutor, HCF plugin) {
        super("home", "Teleport to the faction home.");
        this.factionExecutor = factionExecutor;
        this.plugin = plugin;
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

        if (args.length >= 2 && args[1].equalsIgnoreCase("set")) {
            factionExecutor.getArgument("sethome").onCommand(sender, command, label, args);
            return true;
        }

        UUID uuid = player.getUniqueId();

        PlayerTimer timer = plugin.getTimerManager().getEnderPearlTimer();
        long remaining = timer.getRemaining(player);

        if ((remaining = (timer = plugin.getTimerManager().getCombatTimer()).getRemaining(player)) > 0L) {
            sender.sendMessage(ChatColor.RED + "You cannot /f home whilst your Spawn Tag" + ChatColor.RED + " timer is active! " + ChatColor.BOLD
                    + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + " remaining.");

            return true;
        }

        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        Location home = playerFaction.getHome();

        if (home == null) {
            sender.sendMessage(ChatColor.RED + "Your faction does not have a home set. Use the command /f sethome in your territory.");
            return true;
        }

        Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());

        if (factionAt instanceof EventFaction) {
            sender.sendMessage(ChatColor.RED + "You may not /f home in event zones.");
            return true;
        }


        if (factionAt != playerFaction && factionAt instanceof PlayerFaction) {
            player.sendMessage(ChatColor.RED + "You may not warp in enemy claims. Use " + ChatColor.YELLOW + '/' + label +
                    " stuck" + ChatColor.RED + " if trapped.");
            return true;
        }


        long millis;
        if (factionAt.isSafezone()) {
            millis = 0L;
        } else {
            switch (player.getWorld().getEnvironment()) {
                case THE_END:
                    sender.sendMessage(ChatColor.RED + "You may not /f home in the end.");
                    return true;
                case NETHER:
                    millis = 30000L;
                    break;
                default:
                    millis = 10000L;
                    break;
            }
        }

        if (factionAt != playerFaction && factionAt instanceof PlayerFaction) {
            millis *= 2L;
        }

        plugin.getTimerManager()
                .getTeleportTimer()
                .teleport(
                        player,
                        home,
                        millis,
                        ChatColor.RED + "Teleporting to your faction home in " + ChatColor.WHITE + DurationFormatter.getRemaining(millis, true, false) + ChatColor.RED
                                + ". Do not move or take damage.", PlayerTeleportEvent.TeleportCause.COMMAND);

        return true;
    }
}

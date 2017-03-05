package me.esshd.hcf.timer.argument;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import me.esshd.api.utils.JavaUtils;
import me.esshd.api.utils.cmds.CommandArgument;
import me.esshd.com.google.common.collect.FluentIterableCompat;
import me.esshd.hcf.HCF;
import me.esshd.hcf.LocaleService;
import me.esshd.hcf.timer.PlayerTimer;
import me.esshd.hcf.timer.Timer;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class TimerSetArgument extends CommandArgument {

    private static final Pattern WHITESPACE_TRIMMER = Pattern.compile("\\s");

    private final HCF plugin;

    public TimerSetArgument(HCF plugin) {
        super("set", "Set remaining timer time");
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <timerName> <all|playerName> <remaining>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(!sender.hasPermission("Core.staff")){
    		sender.sendMessage(LocaleService.NO_PERM);
    		return true;
    	}
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        long duration = JavaUtils.parse(args[3]);

        if (duration == -1L) {
            sender.sendMessage(ChatColor.RED + "Invalid time, use the correct format: 1h 10m 1s");
            return true;
        }

        PlayerTimer playerTimer = null;
        for (Timer timer : plugin.getTimerManager().getTimers()) {
            if (timer instanceof PlayerTimer && WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("").equalsIgnoreCase(args[1])) {
                playerTimer = (PlayerTimer) timer;
                break;
            }
        }

        if (playerTimer == null) {
            sender.sendMessage(ChatColor.GOLD + "Timer '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
            return true;
        }

        if (args[2].equalsIgnoreCase("all")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerTimer.setCooldown(player, player.getUniqueId(), duration, true, null);
            }

            sender.sendMessage(ChatColor.GOLD + "Set timer  for all to " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(duration, true, true) + '.');
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]); // TODO: breaking
            Player targetPlayer = null;

            if (target == null || (sender instanceof Player && ((targetPlayer = target.getPlayer()) != null) && !((Player) sender).canSee(targetPlayer))) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            playerTimer.setCooldown(targetPlayer, target.getUniqueId(), duration, true, null);
            sender.sendMessage(ChatColor.GOLD + "Set timer " + ChatColor.WHITE + playerTimer.getName() + ChatColor.GOLD + " duration to " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(duration, true, true) + ChatColor.GOLD + " for " + ChatColor.WHITE + target.getName()
                    + '.');
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            return FluentIterableCompat.from(plugin.getTimerManager().getTimers()).filter(new Predicate<Timer>() {
                @Override
                public boolean apply(Timer timer) {
                    return timer instanceof PlayerTimer;
                }
            }).transform(new Function<Timer, String>() {
                @Nullable
                @Override
                public String apply(Timer timer) {
                    return WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("");
                }
            }).toList();
        } else if (args.length == 3) {
            List<String> list = new ArrayList<>();
            list.add("ALL");
            Player player = sender instanceof Player ? (Player) sender : null;
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (player == null || player.canSee(target)) {
                    list.add(target.getName());
                }
            }

            return list;
        }

        return Collections.emptyList();
    }
}

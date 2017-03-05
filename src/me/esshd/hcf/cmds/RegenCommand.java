/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 */
package me.esshd.hcf.cmds;

import java.util.Collections;
import java.util.List;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.HCF;
import me.esshd.hcf.faction.FactionManager;
import me.esshd.hcf.faction.struct.RegenStatus;
import me.esshd.hcf.faction.type.PlayerFaction;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class RegenCommand
implements CommandExecutor,
TabCompleter {
    private final HCF plugin;
    private static /* synthetic */ int[] $SWITCH_TABLE$me$esshd$hcf$faction$struct$RegenStatus;

    public RegenCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player)sender;
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage((Object)ChatColor.RED + "You are not in a faction.");
            return true;
        }
        RegenStatus regenStatus = playerFaction.getRegenStatus();
        switch (RegenCommand.$SWITCH_TABLE$me$esshd$hcf$faction$struct$RegenStatus()[regenStatus.ordinal()]) {
            case 1: {
                sender.sendMessage((Object)ChatColor.RED + "Your faction currently has full DTR.");
                return true;
            }
            case 3: {
                sender.sendMessage((Object)ChatColor.RED + "Your faction is currently on DTR freeze for another " + (Object)ChatColor.WHITE + DurationFormatUtils.formatDurationWords((long)playerFaction.getRemainingRegenerationTime(), (boolean)true, (boolean)true) + (Object)ChatColor.RED + '.');
                return true;
            }
            case 2: {
                sender.sendMessage((Object)ChatColor.YELLOW + "Your faction currently has " + (Object)ChatColor.YELLOW + regenStatus.getSymbol() + ' ' + playerFaction.getDeathsUntilRaidable() + (Object)ChatColor.YELLOW + " DTR and is regenerating at a rate of " + (Object)ChatColor.GOLD + ConfigurationService.DTR_INCREMENT_BETWEEN_UPDATES + (Object)ChatColor.YELLOW + " every " + (Object)ChatColor.GOLD + ConfigurationService.DTR_WORDS_BETWEEN_UPDATES + (Object)ChatColor.YELLOW + ". Your ETA for maximum DTR is " + (Object)ChatColor.RED + DurationFormatUtils.formatDurationWords((long)this.getRemainingRegenMillis(playerFaction), (boolean)true, (boolean)true) + (Object)ChatColor.YELLOW + '.');
                return true;
            }
        }
        sender.sendMessage((Object)ChatColor.RED + "Unrecognised regeneration status, please inform an Developer or an System Admin.");
        return true;
    }

    public long getRemainingRegenMillis(PlayerFaction faction) {
        long millisPassedSinceLastUpdate = System.currentTimeMillis() - faction.getLastDtrUpdateTimestamp();
        double dtrRequired = faction.getMaximumDeathsUntilRaidable() - faction.getDeathsUntilRaidable();
        return (long)((double)ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES / ConfigurationService.DTR_INCREMENT_BETWEEN_UPDATES * dtrRequired) - millisPassedSinceLastUpdate;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }

    static /* synthetic */ int[] $SWITCH_TABLE$me$esshd$hcf$faction$struct$RegenStatus() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$me$esshd$hcf$faction$struct$RegenStatus;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[RegenStatus.values().length];
        try {
            arrn[RegenStatus.FULL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            arrn[RegenStatus.PAUSED.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            arrn[RegenStatus.REGENERATING.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        $SWITCH_TABLE$me$esshd$hcf$faction$struct$RegenStatus = arrn;
        return $SWITCH_TABLE$me$esshd$hcf$faction$struct$RegenStatus;
    }
}


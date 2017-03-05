/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.org.apache.commons.lang3.time.FastDateFormat
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 */
package me.esshd.hcf.cmds;

import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import me.esshd.hcf.ConfigurationService;
import net.minecraft.util.org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ServerTimeCommand
implements CommandExecutor,
TabCompleter {
    private static final FastDateFormat FORMAT = FastDateFormat.getInstance((String)"E MMM dd h:mm:ssa z yyyy", (TimeZone)ConfigurationService.SERVER_TIME_ZONE);

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage((Object)ChatColor.YELLOW + "The server time is " + (Object)ChatColor.GOLD + FORMAT.format(System.currentTimeMillis()) + (Object)ChatColor.GOLD + '.');
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}


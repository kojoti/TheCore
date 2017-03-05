/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.ArrayUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.esshd.api.main.cmds;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.esshd.api.main.BaseConstants;
import me.esshd.api.utils.BukkitUtils;
import me.esshd.api.utils.cmds.ArgumentExecutor;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand
extends ArgumentExecutor {
    private static final Pattern USAGE_REPLACER_PATTERN = Pattern.compile("(command)", 16);
    private final String name;
    private final String description;
    private String[] aliases;
    private String usage;

    public BaseCommand(String name, String description) {
        super(name);
        this.name = name;
        this.description = description;
    }

    public static boolean checkNull(CommandSender sender, String player) {
        Player target = BukkitUtils.playerWithNameOrUUID(player);
        if (target == null || !BaseCommand.canSee(sender, target)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, player));
            return true;
        }
        return false;
    }

    public static boolean canSee(CommandSender sender, Player target) {
        if (target != null && (!(sender instanceof Player) || ((Player)sender).canSee(target))) {
            return true;
        }
        return false;
    }

    public final String getPermission() {
        return "thecore.command." + this.name;
    }

    public boolean isPlayerOnlyCommand() {
        return false;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUsage() {
        if (this.usage == null) {
            this.usage = "";
        }
        return (Object)ChatColor.RED + "Usage: " + USAGE_REPLACER_PATTERN.matcher(this.usage).replaceAll(this.name) + (Object)ChatColor.GRAY + " - " + this.description;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getUsage(String label) {
        return (Object)ChatColor.RED + "Usage: " + USAGE_REPLACER_PATTERN.matcher(this.usage).replaceAll(label) + (Object)ChatColor.GRAY + " - " + this.description;
    }

    public String[] getAliases() {
        if (this.aliases == null) {
            this.aliases = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return Arrays.copyOf(this.aliases, this.aliases.length);
    }

    protected void setAliases(String[] aliases) {
        this.aliases = aliases;
    }
}


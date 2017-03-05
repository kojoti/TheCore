/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.esshd.api.main.cmds.modules.essential;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import me.esshd.api.main.BaseConstants;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.utils.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand
extends BaseCommand {
    public GamemodeCommand() {
        super("gamemode", "Sets a gamemode for a player.");
        this.setAliases(new String[]{"gm"});
        this.setUsage("/(command) <creative/survival> [player]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length < 1) {
            sender.sendMessage(this.getUsage(label));
            return true;
        }
        GameMode mode = this.getGameModeByName(args[0]);
        if (mode == null) {
            sender.sendMessage((Object)ChatColor.RED + "Gamemode '" + args[0] + "' not found.");
            return true;
        }
        if (args.length > 1) {
            target = sender.hasPermission(String.valueOf(String.valueOf(command.getPermission())) + ".others") ? BukkitUtils.playerWithNameOrUUID(args[1]) : null;
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(this.getUsage(label));
                return true;
            }
            target = (Player)sender;
        }
        if (target == null || !BaseCommand.canSee(sender, target)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[1]));
            return true;
        }
        if (target.getGameMode() == mode) {
            sender.sendMessage((Object)ChatColor.RED + "Gamemode of " + target.getName() + " is already " + mode.name() + '.');
            return true;
        }
        target.sendMessage((Object)ChatColor.GREEN + "Your gamemode has been changed.");
        target.setGameMode(mode);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        GameMode[] gameModes = GameMode.values();
        ArrayList<String> results = new ArrayList<String>(gameModes.length);
        GameMode[] arrgameMode = gameModes;
        int n = arrgameMode.length;
        int n2 = 0;
        while (n2 < n) {
            GameMode mode = arrgameMode[n2];
            results.add(mode.name());
            ++n2;
        }
        return BukkitUtils.getCompletions(args, results);
    }

    private GameMode getGameModeByName(String id) {
        if ((id = id.toLowerCase(Locale.ENGLISH)).equalsIgnoreCase("gmc") || id.contains("creat") || id.equalsIgnoreCase("1") || id.equalsIgnoreCase("c")) {
            return GameMode.CREATIVE;
        }
        if (id.equalsIgnoreCase("gms") || id.contains("survi") || id.equalsIgnoreCase("0") || id.equalsIgnoreCase("s")) {
            return GameMode.SURVIVAL;
        }
        if (id.equalsIgnoreCase("gma") || id.contains("advent") || id.equalsIgnoreCase("2") || id.equalsIgnoreCase("a")) {
            return GameMode.ADVENTURE;
        }
        if (id.equalsIgnoreCase("gmt") || id.contains("toggle") || id.contains("cycle") || id.equalsIgnoreCase("t")) {
            return null;
        }
        return null;
    }
}


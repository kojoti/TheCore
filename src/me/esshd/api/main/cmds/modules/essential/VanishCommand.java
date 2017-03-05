/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.esshd.api.main.cmds.modules.essential;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import me.esshd.api.main.BaseConstants;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.main.user.BaseUser;
import me.esshd.api.main.user.UserManager;
import me.esshd.api.utils.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand
extends BaseCommand {
    private final BasePlugin plugin;

    public VanishCommand(BasePlugin plugin) {
        super("vanish", "Hide from other players.");
        this.setAliases(new String[]{"v", "vis", "vanish", "invis"});
        this.setUsage("/(command) [player]");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length > 0 && sender.hasPermission(String.valueOf(String.valueOf(command.getPermission())) + ".others")) {
            target = BukkitUtils.playerWithNameOrUUID(args[0]);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(this.getUsage(label));
                return true;
            }
            target = (Player)sender;
        }
        if (target == null || sender instanceof Player && !((Player)sender).canSee(target)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }
        BaseUser baseUser = this.plugin.getUserManager().getUser(target.getUniqueId());
        boolean newVanished = !baseUser.isVanished() || args.length >= 1 && Boolean.parseBoolean(args[1]);
        baseUser.setVanished(target, newVanished, true);
        sender.sendMessage((Object)ChatColor.GRAY + "Vanish mode of " + target.getName() + " set to " + newVanished + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.emptyList();
    }
}


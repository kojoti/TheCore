/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package me.esshd.hcf.cmds;

import me.esshd.api.utils.chat.ClickAction;
import me.esshd.api.utils.chat.Text;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class HcfCommand
implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

			new Text(ChatColor.GRAY + "[" + ChatColor.AQUA + "TheCore" + ChatColor.GRAY + "] " + ChatColor.YELLOW + "This server is running " + ChatColor.RESET + "TheCore " + ChatColor.YELLOW + "version 2.9 by " + ChatColor.LIGHT_PURPLE + "Descriptive" + ChatColor.YELLOW + ".")
			.setHoverText(ChatColor.YELLOW + "Click to Purchase")
			.setClick(ClickAction.OPEN_URL, "http://www.mc-market.org/threads/160105/")
			.send(sender);
		
		return true;
	}
}


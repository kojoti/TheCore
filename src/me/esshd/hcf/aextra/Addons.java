package me.esshd.hcf.aextra;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Sets;

import lombok.RequiredArgsConstructor;
import me.esshd.hcf.HCF;
import me.esshd.hcf.faction.type.PlayerFaction;

public class Addons {


	@RequiredArgsConstructor
	public static class CobbleManager implements CommandExecutor , Listener{
		private final HCF plugin;
		private Set<Player> disabled = Sets.newHashSet();
		public void init(){
			plugin.getCommand("cobble").setExecutor(this);
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
		}

		@EventHandler
		public void onPlayerPickup(PlayerQuitEvent event){
			disabled.remove(event.getPlayer());
		}
		@EventHandler
		public void onPlayerPickup(PlayerPickupItemEvent event){
			Material type = event.getItem().getItemStack().getType();
			if(type == Material.STONE || type == Material.COBBLESTONE){
				if(disabled.contains(event.getPlayer())){
					event.setCancelled(true);
				}
			}
		}
		@Override
		public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can focus.");
				return true;
			}
			Player player = (Player) sender;
			if(disabled.contains(player)){
				disabled.remove(player);
				player.sendMessage(ChatColor.YELLOW + "You have enabled cobblestone pickups");
			}else{
				disabled.add(player);
				player.sendMessage(ChatColor.YELLOW + "You have disabled cobblestone pickups");
			}
			return true;

		}
	}

	public static void initialize(HCF hcf) {
		// TODO Auto-generated method stub
		
	}
}

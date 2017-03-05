package me.esshd.hcf.listener.fixes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class KeyDupeFixListener implements Listener{

	@EventHandler
	public void onPlace(BlockPlaceEvent e){
		ItemStack i = e.getItemInHand();
		if(i.getItemMeta().hasDisplayName()){
			if(i.getItemMeta().getDisplayName().contains("Key Reward") && i.getType() == Material.CHEST || i.getType() == Material.TRAPPED_CHEST || i.getType() == Material.STORAGE_MINECART || i.getType() == Material.HOPPER_MINECART){
				e.getPlayer().kickPlayer(ChatColor.RED + "Duping is not tolerated on this server! All online staff have been notified.");
				Bukkit.broadcast(ChatColor.RED + e.getPlayer().getName() + " attempted to place a Key Reward chest! (Duping)", "core.staff");
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getType() == Material.STORAGE_MINECART){
				StorageMinecart minecart = (StorageMinecart)e.getClickedBlock();
				if(minecart.getInventory().getName().endsWith(" Key Reward")){
					e.getPlayer().kickPlayer(ChatColor.RED + "Duping is not tolerated on this server! All online staff have been notified.");
					Bukkit.broadcast(ChatColor.RED + e.getPlayer().getName() + " attempted to open a Key Reward Storage Minecart (Duping)", "core.staff");
					e.setCancelled(true);
				}
			}
		}
	}
}

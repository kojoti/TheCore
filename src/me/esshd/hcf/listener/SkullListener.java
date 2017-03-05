package me.esshd.hcf.listener;

import me.esshd.hcf.ConfigurationService;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (ConfigurationService.KIT_MAP)
            return;
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (killer != null) {
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwner(player.getName());
            skull.setItemMeta(meta);
            event.getDrops().add(skull);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player player = event.getPlayer();
            final BlockState state = event.getClickedBlock().getState();
            if (state instanceof Skull) {
                final Skull skull = (Skull) state;
                player.sendMessage(ChatColor.YELLOW + "This head belongs to " + ChatColor.WHITE
                        + ((skull.getSkullType() == SkullType.PLAYER && skull.hasOwner()) ? skull.getOwner()
                        : ("a " + WordUtils.capitalizeFully(skull.getSkullType().name()) + " skull"))
                        + ChatColor.YELLOW + '.');
            }
        }
    }
}

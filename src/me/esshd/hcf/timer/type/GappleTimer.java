package me.esshd.hcf.timer.type;

import com.google.common.base.Predicate;
import me.esshd.hcf.DurationFormatter;
import me.esshd.hcf.timer.PlayerTimer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * Timer used to prevent {@link Player}s from using Notch Apples too often.
 */
public class GappleTimer extends PlayerTimer implements Listener {

    public GappleTimer(JavaPlugin plugin) {
        super("Gapple", TimeUnit.HOURS.toMillis(6L));
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack stack = event.getItem();
        if (stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 1) {
            Player player = event.getPlayer();
            if (setCooldown(player, player.getUniqueId(), defaultCooldown, false, new Predicate<Long>() {
                @Override
                public boolean apply(@Nullable Long value) {
                    return false;
                }
            })) {
                player.sendMessage("");
                player.sendMessage(ChatColor.DARK_PURPLE + "███" + ChatColor.BLACK + "█" + ChatColor.DARK_PURPLE + "████");
                player.sendMessage(ChatColor.DARK_PURPLE + "██" + ChatColor.GOLD + "████" + ChatColor.DARK_PURPLE + "██ " + ChatColor.GOLD + "Gapple:");
                player.sendMessage(ChatColor.DARK_PURPLE + "█" + ChatColor.GOLD + "██" + ChatColor.WHITE + "█" + ChatColor.GOLD + "███" + ChatColor.DARK_PURPLE + "█  " + ChatColor.GREEN + "Consumed");
                player.sendMessage(ChatColor.DARK_PURPLE + "█" + ChatColor.GOLD + "█" + ChatColor.WHITE + "█" + ChatColor.GOLD + "████" + ChatColor.DARK_PURPLE + "█ " + ChatColor.GOLD + "Cooldown Remaining:");
                player.sendMessage(ChatColor.DARK_PURPLE + "█" + ChatColor.GOLD + "██████" + ChatColor.DARK_PURPLE + "█  " + ChatColor.WHITE + "" + DurationFormatter.getRemaining(getRemaining(player), true, false));
                player.sendMessage(ChatColor.DARK_PURPLE + "█" + ChatColor.GOLD + "██████" + ChatColor.DARK_PURPLE + "█");
                player.sendMessage(ChatColor.DARK_PURPLE + "██" + ChatColor.GOLD + "████" + ChatColor.DARK_PURPLE + "██ ");
                player.sendMessage("");
                // player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You have consumed a God Apple! You now have a cooldown for: " + DurationFormatter.getRemaining(getRemaining(player), true, false));
            } else {
                event.setCancelled(true);
                player.sendMessage(ChatColor.GOLD + "You still have a God Apple cooldown for another " + ChatColor.WHITE
                        + DurationFormatter.getRemaining(getRemaining(player), true, false) + ChatColor.GOLD + '.');
            }
        }
    }
}

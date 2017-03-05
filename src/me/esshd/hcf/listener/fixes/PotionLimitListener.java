package me.esshd.hcf.listener.fixes;

import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * Listener that prevents the brewing of illegal {@link org.bukkit.potion.PotionEffectType}s.
 */
public class PotionLimitListener implements Listener {

    private final int EMPTY_BREW_TIME = 400;

    /**
     * Gets the new fixed level for a {@link PotionType}.
     *
     * @param type the {@link PotionType} to get for
     * @return the maximum level of the {@link PotionType}
     */
    public int getMaxLevel(PotionType type) {
        return ConfigurationService.POTION_LIMITS.getOrDefault(type, type.getMaxLevel());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBrew(BrewEvent event) {
        /*
         * if (!testValidity(event.getContents().getContents())) { event.setCancelled(true); event.getContents().getHolder().setBrewingTime(EMPTY_BREW_TIME); }
         */

        // *** Version that works with a Spigot version that does not
        // *** have a BrewEvent#getResults() method:

        BrewerInventory inventory = event.getContents();
        ItemStack[] contents = inventory.getContents();
        int length = contents.length;
        ItemStack[] cloned = new ItemStack[length];
        for (int i = 0; i < length; i++) {
            ItemStack previous = contents[i];
            cloned[i] = (previous == null ? null : previous.clone());
        }

        BrewingStand stand = inventory.getHolder();
        Bukkit.getScheduler().runTask(HCF.getPlugin(), () -> {
            if (!testValidity(inventory.getContents())) {
                stand.setBrewingTime(EMPTY_BREW_TIME);
                inventory.setContents(cloned);
            }
        });

    }

    @EventHandler
    public void onSplash(final PotionSplashEvent e) {
        if (e.getEntity() != null) {
            for (final PotionEffect effect : e.getEntity().getEffects()) {
                if (effect != null && effect.getType() != null && effect.getType().equals((Object) PotionEffectType.INCREASE_DAMAGE)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onConsume(final PlayerItemConsumeEvent e) {
        if (e.getItem() != null && e.getItem().getType() == Material.POTION && e.getItem().getDurability() != 0) {
            final Potion potion = Potion.fromItemStack(e.getItem());
            if (potion != null) {
                final PotionType type = potion.getType();
                if (type != null && type == PotionType.STRENGTH) {
                    e.setCancelled(true);
                    e.setItem((ItemStack) null);
                }
            }
        }
    }

    private boolean testValidity(ItemStack[] contents) {
        for (ItemStack stack : contents) {
            if (stack != null && stack.getType() == Material.POTION && stack.getDurability() != 0) {
                Potion potion = Potion.fromItemStack(stack);

                // Just to be safe, null check this.
                if (potion == null)
                    continue;

                PotionType type = potion.getType();

                // Mundane potions etc, can return a null type
                if (type == null)
                    continue;

                // is 33s poison, allow
                if (type == PotionType.POISON && !potion.hasExtendedDuration() && potion.getLevel() == 1) {
                    continue;
                }

                if (potion.getLevel() > getMaxLevel(type)) {
                    return false;
                }
            }
        }
        return true;
    }
}

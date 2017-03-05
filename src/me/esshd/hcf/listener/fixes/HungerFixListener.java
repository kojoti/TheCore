package me.esshd.hcf.listener.fixes;

import me.esshd.hcf.HCF;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class HungerFixListener implements Listener {
    /*@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final EntityPlayer entityPlayer = ((CraftPlayer)event.getPlayer()).getHandle();
        entityPlayer.knockbackReductionX = 0.6f;
        entityPlayer.knockbackReductionY = 0.55f;
        entityPlayer.knockbackReductionZ = 0.6f;
    }*/

    @EventHandler
    public void onMove(final PlayerMoveEvent e) {
        if (HCF.getPlugin().getFactionManager().getFactionAt(e.getPlayer().getLocation()).isSafezone() && e.getPlayer().getFoodLevel() < 20) {
            e.getPlayer().setFoodLevel(20);
            e.getPlayer().setSaturation(20.0f);
        }
    }

    @EventHandler
    public void onHungerChange(final FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player) e.getEntity();
            if (HCF.getPlugin().getFactionManager().getFactionAt(p.getLocation()).isSafezone()) {
                p.setSaturation(20.0f);
                p.setHealth(20.0);
            }
            p.setSaturation(10.0f);
        }
    }
}

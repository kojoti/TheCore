package me.esshd.hcf.listener;

import me.esshd.api.utils.JavaUtils;
import me.esshd.api.utils.server.TPS;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.HCF;
import me.esshd.hcf.faction.struct.Role;
import me.esshd.hcf.faction.type.Faction;
import me.esshd.hcf.faction.type.PlayerFaction;
import me.esshd.hcf.user.FactionUser;
import net.minecraft.server.v1_7_R4.EntityLightning;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.TimeUnit;

public class DeathListener implements Listener {

    private final HCF plugin;

    public DeathListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeathKillIncrement(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            FactionUser user = plugin.getUserManager().getUser(killer.getUniqueId());
            user.setKills(user.getKills() + 1);
        }
    }

    private static final long BASE_REGEN_DELAY = TimeUnit.MINUTES.toMillis(45L);
    // *Kitmap* private static final long BASE_REGEN_DELAY = TimeUnit.MINUTES.toMillis(0L);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        if (playerFaction != null) {
            Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());
            double dtrLoss = (1.0D * factionAt.getDtrLossMultiplier());
            double newDtr = playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - dtrLoss);
            Role role = playerFaction.getMember(player.getUniqueId()).getRole();
            playerFaction.setRemainingRegenerationTime(BASE_REGEN_DELAY + (playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L)));
            playerFaction.broadcast(ChatColor.GOLD + "Member Death: " + ConfigurationService.TEAMMATE_COLOUR + role.getAstrix() + player.getName() + ChatColor.GOLD + ". " + "DTR: (" + ChatColor.WHITE
                    + JavaUtils.format(newDtr, 2) + '/' + JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable(), 2) + ChatColor.GOLD + ").");
        }
        Integer balance = this.plugin.getEconomyManager().getBalance(player.getUniqueId());
        if (this.plugin.getEconomyManager().getBalance(player.getUniqueId()) > 0) {
            balance = this.plugin.getEconomyManager().getBalance(player.getUniqueId());
            this.plugin.getEconomyManager().addBalance(player.getKiller().getUniqueId(), balance);
            this.plugin.getEconomyManager().subtractBalance(player.getUniqueId(), balance);
            player.getKiller().sendMessage(ChatColor.GOLD + "You earned " + ChatColor.GREEN + ChatColor.BOLD + '$' + balance + ChatColor.GOLD + " for killing " + ChatColor.WHITE + player.getName());
        }


        if (TPS.getTPS() > 15) { // Prevent unnecessary lag during prime times.
            Location location = player.getLocation();
            WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();

            EntityLightning entityLightning = new EntityLightning(worldServer, location.getX(), location.getY(), location.getZ(), false);
            PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(entityLightning);
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (plugin.getUserManager().getUser(target.getUniqueId()).isShowLightning()) {
                    ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
                    target.playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
                }
            }
        }
    }
}

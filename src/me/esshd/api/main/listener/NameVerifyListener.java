/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 */
package me.esshd.api.main.listener;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.esshd.api.main.BasePlugin;
import me.esshd.hcf.HCF;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class NameVerifyListener
implements Listener {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{1,16}$");
    private final BasePlugin plugin;

    public NameVerifyListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName;
        Player player;
        PlayerLoginEvent.Result result = event.getResult();
        if (result == PlayerLoginEvent.Result.ALLOWED && !NAME_PATTERN.matcher(playerName = (player = event.getPlayer()).getName()).matches()) {
            HCF.getPlugin().getLogger().info("Name verification: " + playerName + " was kicked for having an invalid name " + "(to disable, turn off the name-verification feature in the config of 'Base' plugin)");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Invalid player name detected.");
        }
    }
}


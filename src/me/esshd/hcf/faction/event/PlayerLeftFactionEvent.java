package me.esshd.hcf.faction.event;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import me.esshd.hcf.faction.event.cause.FactionLeaveCause;
import me.esshd.hcf.faction.type.Faction;
import me.esshd.hcf.faction.type.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Faction event called when a user has left their {@link Faction}.
 */
public class PlayerLeftFactionEvent extends FactionEvent {

    private static final HandlerList handlers = new HandlerList();

    private Optional<Player> player;

    private final UUID uniqueID;

    private final FactionLeaveCause cause;

    private final CommandSender sender;

    private final boolean isKick;

    private final boolean force;

    public PlayerLeftFactionEvent(CommandSender sender, @Nullable Player player, UUID playerUUID, PlayerFaction playerFaction, FactionLeaveCause cause, boolean isKick, boolean force) {
        super(playerFaction);

        Preconditions.checkNotNull(sender, "Sender cannot be null");
        Preconditions.checkNotNull(playerUUID, "Player UUID cannot be null");
        Preconditions.checkNotNull(playerFaction, "Player faction cannot be null");
        Preconditions.checkNotNull(cause, "Cause cannot be null");

        this.sender = sender;
        if (player != null) {
            this.player = Optional.of(player);
        }

        this.uniqueID = playerUUID;
        this.cause = cause;
        this.isKick = isKick;
        this.force = force;
    }

    public Optional<Player> getPlayer() {
        if (this.player == null) {
            this.player = Optional.fromNullable(Bukkit.getPlayer(uniqueID));
        }

        return player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public PlayerFaction getFaction() {
        return (PlayerFaction) super.getFaction();
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public FactionLeaveCause getCause() {
        return this.cause;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public boolean isKick() {
        return this.isKick;
    }

    public boolean isForce() {
        return this.force;
    }
}

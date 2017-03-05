package me.esshd.hcf.faction.struct;

import me.esshd.api.utils.BukkitUtils;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.faction.type.Faction;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

/**
 * Represents a relation between {@link Faction}s and {@link org.bukkit.entity.Player}s.
 */
public enum Relation {

    MEMBER(3), ALLY(2), ENEMY(1);

    private final int value;

    Relation(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isAtLeast(Relation relation) {
        return this.value >= relation.value;
    }

    public boolean isAtMost(Relation relation) {
        return this.value <= relation.value;
    }

    public boolean isMember() {
        return this == MEMBER;
    }

    public boolean isAlly() {
        return this == ALLY;
    }

    public boolean isEnemy() {
        return this == ENEMY;
    }

    public String getDisplayName() {
        switch (this) {
            case ALLY:
                return toChatColour() + "alliance";
            default:
                return toChatColour() + name().toLowerCase();
        }
    }

    public ChatColor toChatColour() {
        switch (this) {
            case MEMBER:
                return ConfigurationService.TEAMMATE_COLOUR;
            case ALLY:
                return ConfigurationService.ALLY_COLOUR;
            case ENEMY:
            default:
                return ConfigurationService.ENEMY_COLOUR;
        }
    }

    public DyeColor toDyeColour() {
        return BukkitUtils.toDyeColor(toChatColour());
    }
}

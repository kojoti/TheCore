package me.esshd.api.main;

import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;

public enum StaffPriority {

    OWNER(6),
    HEADADMIN(5),
    STAFFMANAGER(4),
    ADMIN(3),
    MODERATOR(2),
    TRIAL(1),
    NONE(0);

    private static final ImmutableMap<Integer, StaffPriority> BY_ID;
    private final int priorityLevel;

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        StaffPriority[] arrstaffPriority = StaffPriority.values();
        int n = arrstaffPriority.length;
        int n2 = 0;
        while (n2 < n) {
            StaffPriority staffPriority = arrstaffPriority[n2];
            builder.put((Object) staffPriority.priorityLevel, (Object) staffPriority);
            ++n2;
        }
        BY_ID = builder.build();
    }

    private StaffPriority(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public static StaffPriority of(int level) {
        return (StaffPriority) ((Object) BY_ID.get((Object) level));
    }

    public static StaffPriority of(Player player) {
        StaffPriority[] arrstaffPriority = StaffPriority.values();
        int n = arrstaffPriority.length;
        int n2 = 0;
        while (n2 < n) {
            StaffPriority staffPriority = arrstaffPriority[n2];
            if (player.hasPermission("staffpriority." + staffPriority.priorityLevel)) {
                return staffPriority;
            }
            ++n2;
        }
        return NONE;
    }

    public int getPriorityLevel() {
        return this.priorityLevel;
    }

    public boolean isMoreThan(StaffPriority other) {
        if (this.priorityLevel > other.priorityLevel) {
            return true;
        }
        return false;
    }
}


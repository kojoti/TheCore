package me.esshd.hcf.visualise;

import org.bukkit.Location;

public class VisualBlock {
    private final VisualType visualType;
    private final VisualBlockData blockData;
    private final Location location;

    public VisualBlock(final VisualType visualType, final VisualBlockData blockData, final Location location) {
        this.visualType = visualType;
        this.blockData = blockData;
        this.location = location;
    }

    public VisualType getVisualType() {
        return this.visualType;
    }

    public VisualBlockData getBlockData() {
        return this.blockData;
    }

    public Location getLocation() {
        return this.location;
    }
}

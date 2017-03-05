/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.Location
 *  org.bukkit.configuration.serialization.ConfigurationSerializable
 */
package me.esshd.api.main.warp;

import com.google.common.base.Preconditions;
import java.util.Map;
import me.esshd.api.utils.PersistableLocation;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Warp
extends PersistableLocation
implements ConfigurationSerializable {
    private String name;
    private String permission;

    public Warp(String name, Location location) {
        super(location);
        Preconditions.checkNotNull((Object)name, (Object)"Warp name cannot be null");
        Preconditions.checkNotNull((Object)location, (Object)"Warp location cannot be null");
        this.name = name;
        this.permission = "warp." + name;
    }

    public Warp(Map<String, String> map) {
        super(map);
        this.name = map.get("name");
        this.permission = map.get("permission");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("name", this.name);
        map.put("permission", this.permission);
        return map;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        Preconditions.checkNotNull((Object)name, (Object)"Warp name cannot be null");
        this.name = name;
    }

    public String getPermission() {
        return this.permission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Warp warp = (Warp)o;
        if (this.name != null ? !this.name.equals(warp.name) : warp.name != null) {
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return result;
    }
}


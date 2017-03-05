package me.esshd.api.utils.cuboid;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class RectSystem {

    public static boolean isInRect(Player player, Location loc1, Location loc2) {
        double[] dim = new double[2];

        dim[0] = loc1.getX();
        dim[1] = loc2.getX();
        Arrays.sort(dim);
        if (player.getLocation().getX() > dim[1] || player.getLocation().getX() < dim[0])
            return false;

        dim[0] = loc1.getZ();
        dim[1] = loc2.getZ();
        Arrays.sort(dim);
        if (player.getLocation().getZ() > dim[1] || player.getLocation().getZ() < dim[0])
            return false;

	    /*TODO same thing with y*/

        return true;
    }

}

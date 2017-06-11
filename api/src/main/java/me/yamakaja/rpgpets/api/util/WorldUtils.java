package me.yamakaja.rpgpets.api.util;

import org.bukkit.Location;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class WorldUtils {

    private WorldUtils() {
    }

    /**
     * Tries to find a random free location around the given location
     * This method is performance heavy and should not be overused
     *
     * @param center       The center location to look around
     * @param squareRadius The radius of the square in which we will try to find a suitable location
     * @return A location with free space, null if none could be found
     */
    public static Location getRandomFreeLocationAround(Location center, int squareRadius) {
        double xOffset,
                zOffset;
        for (int i = 0; i < 100; i++) {
            xOffset = Math.random() * squareRadius;
            zOffset = Math.random() * squareRadius;

            Location tempLoc = center.clone().add(xOffset, -2, zOffset);
            for (int y = 0; y < 5; y++) {
                if (isLocationValid(tempLoc))
                    return tempLoc;

                tempLoc.setY(tempLoc.getY());
            }
        }

        return null;
    }

    private static boolean isLocationValid(Location location) {
        return !location.getBlock().getType().isSolid()
                && !location.clone().add(0, 1, 0).getBlock().getType().isSolid()
                && location.clone().add(0, -1, 0).getBlock().getType().isSolid();
    }

}

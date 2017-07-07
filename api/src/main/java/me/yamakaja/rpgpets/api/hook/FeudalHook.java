package me.yamakaja.rpgpets.api.hook;

import org.bukkit.entity.Player;
import us.forseth11.feudal.core.Feudal;
import us.forseth11.feudal.kingdoms.Kingdom;
import us.forseth11.feudal.user.User;

/**
 * Created by Yamakaja on 07.07.17.
 */
public class FeudalHook {

    private static Feudal feudal;

    private FeudalHook() {
    }

    /**
     * Initialize Feudal hook
     */
    public static void initialize() {
        feudal = Feudal.getPlugin();
    }

    /**
     * @param first  The first player
     * @param second The second player
     * @return Whether the players are in the same kingdom or in allied kingdoms
     */
    public static boolean areAllied(Player first, Player second) {
        User firstUser = Feudal.getUser(first.getUniqueId().toString());
        User secondUser = Feudal.getUser(second.getUniqueId().toString());

        Kingdom firstKingdom = Feudal.getKingdom(firstUser.getKingdomUUID());
        Kingdom secondKingdom = Feudal.getKingdom(secondUser.getKingdomUUID());

        return (firstKingdom != null && secondKingdom != null)
                && (firstKingdom == secondKingdom || firstKingdom.isAllied(secondKingdom));
    }

}

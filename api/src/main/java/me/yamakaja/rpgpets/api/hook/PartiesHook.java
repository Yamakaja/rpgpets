package me.yamakaja.rpgpets.api.hook;

import com.alessiodp.parties.utils.api.PartiesAPI;
import org.bukkit.entity.Player;

/**
 * Created by Yamakaja on 18.06.17.
 */
public class PartiesHook {

    private static PartiesAPI partiesAPI;
    private static boolean initialized = false;

    private PartiesHook() {
    }

    /**
     * Checks whether two people are in a party together
     *
     * @param playerOne The first player
     * @param playerTwo The second player
     * @return Whether they're both in the party
     */
    public static boolean areInSameParty(Player playerOne, Player playerTwo) {
        if (partiesAPI == null && isAvailable())
            partiesAPI = new PartiesAPI();

        return isAvailable() && !(!partiesAPI.haveParty(playerOne.getUniqueId())
                || !partiesAPI.haveParty(playerTwo.getUniqueId()))
                && partiesAPI.getPartyName(playerOne.getUniqueId()).equals(partiesAPI.getPartyName(playerTwo.getUniqueId()));

    }

    /**
     * @return Whether or not the Parties hook is available
     */
    public static boolean isAvailable() {
        return PartiesHook.initialized;
    }

    /**
     * Initialize the Parties hook
     */
    public static void initialize() {
        initialized = true;
    }

}

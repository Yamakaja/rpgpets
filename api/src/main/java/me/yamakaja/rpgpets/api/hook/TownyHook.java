package me.yamakaja.rpgpets.api.hook;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Yamakaja on 3/29/18.
 */
public final class TownyHook {

    private TownyHook() {
        throw new UnsupportedOperationException("Utility class!");
    }

    public static void initialize() {

    }

    public static boolean areAllied(Player a, Player b) {
        try {
            Town townA = TownyUniverse.getDataSource().getResident(a.getName()).getTown();
            Town townB = TownyUniverse.getDataSource().getResident(b.getName()).getTown();

            return townA == townB || townA.isAlliedWith(townB) ||
                    (townA.getNation() != null && townB.getNation() != null && (townA.getNation() == townB.getNation()
                            || townA.getNation().hasAlly(townB.getNation())));
        } catch (NotRegisteredException e) {
            return false;
        }
    }

    public static boolean isPvpEnabled(Location location) {
        try {
            return TownyUniverse.getTownBlock(location).getTown().isPVP();
        } catch (NotRegisteredException | NullPointerException e) {
            return true;
        }
    }
}

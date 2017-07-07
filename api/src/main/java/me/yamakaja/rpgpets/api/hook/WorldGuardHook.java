package me.yamakaja.rpgpets.api.hook;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Yamakaja on 21.06.17.
 */
public class WorldGuardHook {

    private static Object plugin;

    private WorldGuardHook() {
    }

    public static void initialize() {
        plugin = WorldGuardPlugin.inst();
    }

    /**
     * Check whether pvp is allowed at the passed location for the passed player
     *
     * @param owner The owner to check for
     * @param loc   The location to query
     * @return Whether pvp is allowed
     */
    public static boolean isPvpEnabled(Player owner, Location loc) {
        StateFlag.State state = ((WorldGuardPlugin) plugin).getRegionManager(loc.getWorld()).getApplicableRegions(loc).queryState(((WorldGuardPlugin) plugin).wrapPlayer(owner), DefaultFlag.PVP);
        return state == StateFlag.State.ALLOW || state == null;
    }

}

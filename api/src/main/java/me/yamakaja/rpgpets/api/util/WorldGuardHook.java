package me.yamakaja.rpgpets.api.util;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Yamakaja on 21.06.17.
 */
public class WorldGuardHook {

    static WorldGuardPlugin plugin;

    private WorldGuardHook() {
    }

    public static void initialize() {
        plugin = WorldGuardPlugin.inst();
    }

    public static boolean isPvpEnabled(Player owner, Location loc) {
        if (plugin == null)
            return true;

        StateFlag.State state = plugin.getRegionManager(loc.getWorld()).getApplicableRegions(loc).queryState(plugin.wrapPlayer(owner), DefaultFlag.PVP);
        return state == StateFlag.State.ALLOW || state == null;
    }

}

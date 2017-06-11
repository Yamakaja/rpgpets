package me.yamakaja.rpgpets.v1_11_R1.entity;

import me.yamakaja.rpgpets.api.entity.Pet;
import me.yamakaja.rpgpets.api.entity.PetType;
import me.yamakaja.rpgpets.v1_11_R1.NMSUtils;
import me.yamakaja.rpgpets.v1_11_R1.pathfinding.PathfinderGoalFollowOwner;
import net.minecraft.server.v1_11_R1.EntityCow;
import net.minecraft.server.v1_11_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class PetCow extends EntityCow implements Pet {

    private Player owner;

    public PetCow(World world) {
        super(world);
        this.die();
    }

    public PetCow(Player player) {
        super(((CraftPlayer) player).getHandle().getWorld());

        this.owner = player;

        Location playerLoc = player.getLocation();
        this.locX = playerLoc.getX();
        this.locY = playerLoc.getY();
        this.locZ = playerLoc.getZ();

        NMSUtils.clearGoalsAndTargets(goalSelector, targetSelector);

        this.goalSelector.a(new PathfinderGoalFollowOwner(this, owner));
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public float getExperience() {
        return 0;
    }

    @Override
    public PetType getPetType() {
        return PetType.COW;
    }
}

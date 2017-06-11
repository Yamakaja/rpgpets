package me.yamakaja.rpgpets.v1_11_R1.pathfinding;

import me.yamakaja.rpgpets.api.util.WorldUtils;
import net.minecraft.server.v1_11_R1.EntityCreature;
import net.minecraft.server.v1_11_R1.NavigationAbstract;
import net.minecraft.server.v1_11_R1.PathEntity;
import net.minecraft.server.v1_11_R1.PathfinderGoal;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Yamakaja on 11.06.17.
 */
public class PathfinderGoalFollowOwner extends PathfinderGoal {

    private EntityCreature pet;
    private Player owner;
    private NavigationAbstract pathfinder;
    private Location targetLoc;

    public PathfinderGoalFollowOwner(EntityCreature pet, Player owner) {
        this.pet = pet;
        this.owner = owner;

        this.pathfinder = pet.getNavigation();
    }

    @Override
    public boolean a() { // shouldExecute
        double distanceSquared = owner.getLocation().distanceSquared(pet.getBukkitEntity().getLocation());
        return distanceSquared > 10 * 10 && distanceSquared < 20 * 20;
    }

    @Override
    public boolean b() { // shouldContinueExecuting
        return this.a();
    }

    @Override
    public void c() { // startExecuting
        if (targetLoc == null)
            this.targetLoc = WorldUtils.getRandomFreeLocationAround(owner.getLocation(), 5);
        if (this.targetLoc == null)
            this.targetLoc = owner.getLocation();

        PathEntity pathEntity = this.pathfinder.a(targetLoc.getX(), targetLoc.getY(), targetLoc.getZ());
        this.pathfinder.a(pathEntity, 0.2);
    }

    @Override
    public void d() { // resetTask
        this.targetLoc = null;
    }

    @Override
    public void e() { // updateTask
        super.e();
    }

    @Override
    public boolean g() { // isInterruptible
        return true;
    }

}

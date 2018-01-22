package me.yamakaja.rpgpets.v1_11_R1.pathfinding;

import me.yamakaja.rpgpets.api.entity.Pet;
import net.minecraft.server.v1_11_R1.EntityCreature;
import net.minecraft.server.v1_11_R1.EntityLiving;
import net.minecraft.server.v1_11_R1.PathfinderGoalTarget;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Created by Yamakaja on 12.06.17.
 */
public class PetPathfinderGoalOwnerHurtTarget extends PathfinderGoalTarget {

    private Pet entity;
    private EntityLiving target;
    private int c;

    public PetPathfinderGoalOwnerHurtTarget(Pet pet) {
        super((EntityCreature) pet, false);
        this.entity = pet;
        this.a(1); // Target mutex bits
    }

    @Override
    public boolean a() {
        EntityLiving nmsPlayer = ((CraftPlayer) entity.getPetDescriptor().getOwner()).getHandle();
        this.target = nmsPlayer.bM();

        int ticksLived = nmsPlayer.bN();
        return ticksLived != this.c && this.a(this.target, false);
    }

    public void c() {
        this.e.setGoalTarget(this.target, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
        EntityLiving entityliving = ((CraftPlayer) this.entity.getPetDescriptor().getOwner()).getHandle();

        if (entityliving != null)
            this.c = entityliving.bN();

        super.c();
    }

}

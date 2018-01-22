//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package me.yamakaja.rpgpets.v1_11_R1.pathfinding;

import me.yamakaja.rpgpets.api.entity.Pet;
import net.minecraft.server.v1_11_R1.EntityCreature;
import net.minecraft.server.v1_11_R1.EntityLiving;
import net.minecraft.server.v1_11_R1.PathfinderGoalTarget;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class PetPathfinderGoalHurtByTarget extends PathfinderGoalTarget {
    private int b;
    private Pet pet;

    public PetPathfinderGoalHurtByTarget(Pet entity) {
        super((EntityCreature) entity, true);
        this.pet = entity;
        this.a(1);
    }

    public boolean a() {
        int i = this.e.bL();
        EntityLiving entityliving = this.e.getLastDamager();
        return i != this.b && entityliving != null && this.a(entityliving, false);
    }

    public void c() {
        EntityLiving lastDamager = this.e.getLastDamager();

        if (lastDamager == null || lastDamager.getBukkitEntity() == pet.getPetDescriptor().getOwner()
                || lastDamager == this.e)
            return;

        this.e.setGoalTarget(this.e.getLastDamager(), TargetReason.TARGET_ATTACKED_ENTITY, true);
        this.g = this.e.getGoalTarget();
        this.b = this.e.bL();
        this.h = 300;

        super.c();
    }

}

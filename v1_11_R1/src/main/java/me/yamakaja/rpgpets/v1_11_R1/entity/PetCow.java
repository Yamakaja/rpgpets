package me.yamakaja.rpgpets.v1_11_R1.entity;

import me.yamakaja.rpgpets.api.entity.Pet;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetType;
import me.yamakaja.rpgpets.v1_11_R1.NMSUtils;
import me.yamakaja.rpgpets.v1_11_R1.pathfinding.PathfinderGoalFollowOwner;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class PetCow extends EntityCow implements Pet {

    private Player owner;
    private PetDescriptor petDescriptor;

    public PetCow(World world) {
        super(world);
        System.out.println("World Constructor!");
        NMSUtils.clearGoalsAndTargets(goalSelector, targetSelector);
    }

    public PetCow(PetDescriptor petDescriptor) {
        this(((CraftPlayer) petDescriptor.getOwner()).getHandle().getWorld());
        System.out.println("Created using custom constructor");

        Location playerLoc = petDescriptor.getOwner().getLocation();
        this.setLocation(playerLoc.getX(), playerLoc.getY(), playerLoc.getZ(), playerLoc.getYaw(), playerLoc.getPitch());

        this.goalSelector.a(0, new PathfinderGoalFollowOwner(this, petDescriptor.getOwner()));

        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 2.5, true));
        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<>(this, EntityMonster.class, true));

        this.setCustomNameVisible(true);

        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(100);
    }

    @Override
    public boolean B(Entity entity) { // onAttack
        final float damage = 5f;
        final int knockback = 2;
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), damage);
        if(flag) {
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).a(this, (float) knockback * 0.5F, (double) MathHelper.sin(this.yaw * 0.017453292F), (double) (-MathHelper.cos(this.yaw * 0.017453292F))); // Deal knockback
                this.motX *= 0.6D;
                this.motZ *= 0.6D;
            }
        }
        return flag;
    }

    @Override
    public void A_() { // onUpdate
        super.A_();

        if (this.petDescriptor.getOwner().getLocation().distanceSquared(this.getBukkitEntity().getLocation()) > 30 * 30)
            this.getBukkitEntity().teleport(this.petDescriptor.getOwner());

        this.setCustomName(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "51" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + "Rambo " + ChatColor.RED + (this.getHealth() / 2) + "\u2764");
    }

    @Override
    public PetDescriptor getPetDescriptor() {
        return petDescriptor;
    }

    @Override
    public int getAge() {
        return this.petDescriptor.isGrownUp() ? 1 : Integer.MIN_VALUE;
    }
}

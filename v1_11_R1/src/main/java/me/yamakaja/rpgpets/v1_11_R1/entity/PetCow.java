package me.yamakaja.rpgpets.v1_11_R1.entity;

import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.entity.Pet;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.util.WorldUtils;
import me.yamakaja.rpgpets.v1_11_R1.NMSUtils;
import me.yamakaja.rpgpets.v1_11_R1.pathfinding.PetPathfinderGoalFollowOwner;
import me.yamakaja.rpgpets.v1_11_R1.pathfinding.PetPathfinderGoalHurtByTarget;
import me.yamakaja.rpgpets.v1_11_R1.pathfinding.PetPathfinderGoalOwnerHurtTarget;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class PetCow extends EntityCow implements Pet {

    private PetDescriptor petDescriptor;
    private PathfinderGoalMeleeAttack meleeAttackGoal;

    @SuppressWarnings("unused") // Called and required my Minecraft code
    public PetCow(World world) {
        super(world);
        this.die();
    }

    public PetCow(PetDescriptor petDescriptor) {
        super(((CraftPlayer) petDescriptor.getOwner()).getHandle().getWorld());

        this.petDescriptor = petDescriptor;

        Location playerLoc = petDescriptor.getOwner().getEyeLocation();
        this.setLocation(playerLoc.getX(), playerLoc.getY(), playerLoc.getZ(), playerLoc.getYaw(), playerLoc.getPitch());

        NMSUtils.clearGoalsAndTargets(goalSelector, targetSelector);

        this.goalSelector.a(0, new PetPathfinderGoalFollowOwner(this, this.petDescriptor));
        this.goalSelector.a(1, meleeAttackGoal = new PathfinderGoalMeleeAttack(this, this.petDescriptor.getSpeed(), true)); // flag: hasToSeeTarget
        this.goalSelector.a(2, new PathfinderGoalRandomLookaround(this));

        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<>(this, EntityMonster.class, false)); // flag: Calls for help
        this.targetSelector.a(1, new PetPathfinderGoalOwnerHurtTarget(this));
        this.targetSelector.a(2, new PetPathfinderGoalHurtByTarget(this));

        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(this.petDescriptor.getMaxHealth());
        this.setHealth(this.petDescriptor.getMaxHealth());

        this.updateCustomName();
        this.setCustomNameVisible(true);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(30);
    }

    @Override
    public void updateAttributes() {
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(this.petDescriptor.getMaxHealth());
        NMSUtils.setSpeed(this.meleeAttackGoal, this.petDescriptor.getSpeed());
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        boolean flag = super.damageEntity(damagesource, f);
        this.updateCustomName();
        return flag;
    }

    @Override
    public boolean B(Entity entity) { // onAttack
        if ((entity instanceof EntityPlayer || entity instanceof Pet) && !WorldUtils.isPvpEnabled(this.petDescriptor.getOwner(), this.getBukkitEntity().getLocation()))
            return false;

        final float damage = this.petDescriptor.getAttackDamage();
        final float knockback = this.petDescriptor.getKnockback();
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), damage);
        if (flag) {
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).a(this, knockback * 0.5F, (double) MathHelper.sin(this.yaw * 0.017453292F), (double) (-MathHelper.cos(this.yaw * 0.017453292F))); // Deal knockback
                this.motX *= 0.6D;
                this.motZ *= 0.6D;

                boolean levelup = this.petDescriptor.addExperience(damage);

                if (!entity.isAlive())
                    levelup = levelup || this.petDescriptor.addExperience(((EntityLiving) entity).getMaxHealth());

                if (levelup)
                    this.updateAttributes();
            }

        }
        this.updateCustomName();
        return flag;
    }

    private void updateCustomName() {
        this.setCustomName(ConfigMessages.GENERAL_PETNAME.get(Integer.toString(this.petDescriptor.getLevel()),
                this.petDescriptor.getName(), Float.toString(this.getHealth() / 2)));
    }

    @Override
    public void A_() { // onUpdate
        super.A_();

        if (this.ticksLived % 10 == 0)
            this.updateCustomName();

        if (this.petDescriptor.getOwner().getLocation().distanceSquared(this.getBukkitEntity().getLocation()) > 30 * 30)
            this.getBukkitEntity().teleport(this.petDescriptor.getOwner());

        if (this.isAlive() && this.ticksLived % 80 == 0 && this.getHealth() < this.getMaxHealth())
            this.setHealth(this.getHealth() + 1);
    }

    @Override
    public PetDescriptor getPetDescriptor() {
        return petDescriptor;
    }

    @Override
    public int getAge() {
        return this.petDescriptor.isAdult() ? 1 : Integer.MIN_VALUE;
    }
}

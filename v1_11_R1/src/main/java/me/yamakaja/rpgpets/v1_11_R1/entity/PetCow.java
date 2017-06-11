package me.yamakaja.rpgpets.v1_11_R1.entity;

import me.yamakaja.rpgpets.api.entity.Pet;
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
    private int level;
    private float experience;

    public PetCow(World world) {
        super(world);
        System.out.println("World Constructor!");
        NMSUtils.clearGoalsAndTargets(goalSelector, targetSelector);
    }

    public PetCow(Player player) {
        this(((CraftPlayer) player).getHandle().getWorld());
        System.out.println("Created using custom constructor");

        this.owner = player;

        Location playerLoc = player.getLocation();
        this.setLocation(playerLoc.getX(), playerLoc.getY(), playerLoc.getZ(), playerLoc.getYaw(), playerLoc.getPitch());

        if (owner != null)
            this.goalSelector.a(0, new PathfinderGoalFollowOwner(this, owner));
        else
            System.out.println("Couldn't initialize pathfinder goals because owner is null!");

        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 2.5, true));
        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<>(this, EntityMonster.class, true));

        this.setCustomNameVisible(true);

        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(100);
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public float getExperience() {
        return experience;
    }

    @Override
    public PetType getPetType() {
        return PetType.COW;
    }

    @Override
    public boolean B(Entity entity) { // On Attack
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
    public void A_() {
        super.A_();

        if (owner.getLocation().distanceSquared(this.getBukkitEntity().getLocation()) > 30 * 30)
            this.getBukkitEntity().teleport(owner);

        this.setCustomName(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "51" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + "Rambo " + ChatColor.RED + (this.getHealth() / 2) + "\u2764");
    }

    @Override
    public void f(NBTTagCompound nbttagcompound) { // Load from NBT
        super.f(nbttagcompound);

        System.out.println("Loading from NBT");

        NBTTagCompound rpgTag = nbttagcompound.getCompound("rpgpets");

        this.owner = Bukkit.getPlayer(new UUID(rpgTag.getLong("OwnerUUIDMost"), rpgTag.getLong("OwnerUUIDLeast")));
        this.level = rpgTag.getInt("Level");
        this.experience = rpgTag.getFloat("Experience");
    }

    @Override
    public void d(EntityHuman entityhuman) {
    }

    @Override
    public NBTTagCompound e(NBTTagCompound nbttagcompound) { // Save to NBT
        nbttagcompound = super.e(nbttagcompound);

        System.out.println("Saving to NBT");

        NBTTagCompound rpgTag = new NBTTagCompound();

        UUID ownerUuid = this.owner.getUniqueId();

        rpgTag.setLong("OwnerUUIDMost", ownerUuid.getMostSignificantBits());
        rpgTag.setLong("OwnerUUIDLeast", ownerUuid.getLeastSignificantBits());

        rpgTag.setInt("Level", level);
        rpgTag.setFloat("Experience", experience);

        nbttagcompound.set("rpgpets", rpgTag);

        return nbttagcompound;
    }



}

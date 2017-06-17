package me.yamakaja.rpgpets.api.entity;

import me.yamakaja.rpgpets.api.config.ConfigGeneral;
import me.yamakaja.rpgpets.api.event.PetLevelUpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Yamakaja on 12.06.17.
 */
public class PetDescriptor {

    private PetType petType;
    private Player owner;
    private String name;
    private int level;

    private PetState state;

    private int entityId;

    private float experience;
    private float experienceRequirement;

    private boolean grownUp;

    private float speed;
    private float attackDamage;
    private float knockback;
    private float maxHealth;

    public PetDescriptor(PetType petType, Player owner, String name, int level, float experience, boolean grownUp) {
        this.petType = petType;
        this.owner = owner;
        this.name = name;
        this.level = level;
        this.experience = experience;
        this.grownUp = grownUp;

        this.state = PetState.READY;

        this.updateStats();
    }

    public PetState getState() {
        return state;
    }

    public void setState(PetState state) {
        this.state = state;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public boolean hasEntityId() {
        return entityId != 0;
    }

    public boolean isGrownUp() {
        return grownUp;
    }

    public void setGrownUp(boolean grownUp) {
        this.grownUp = grownUp;
    }

    public PetType getPetType() {
         return petType;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        this.updateStats();
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public float getSpeed() {
        return speed;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public float getKnockback() {
        return knockback;
    }

    public float getExperienceRequirement() {
        return experienceRequirement;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    private void updateStats() {
        this.experienceRequirement = (float) (this.petType.getBaseExpRequirement() * Math.pow(this.petType.getExpRequirementModifier(), this.level));

        this.speed = this.petType.getBaseSpeed() + this.petType.getLevelupSpeed() * this.level;
        this.attackDamage = this.petType.getBaseAttackDamage() + this.petType.getLevelupAttackDamage() * this.level;
        this.knockback = this.petType.getBaseKnockback() + this.petType.getLevelupKnockback() * this.level;
        this.maxHealth = this.petType.getBaseMaxHealth() + this.petType.getLevelupMaxHealth() * this.level;

        if (!this.isGrownUp()) {
            this.speed = this.speed * this.petType.getBabyModifierSpeed();
            this.attackDamage = this.attackDamage * this.petType.getBabyModifierAttackDamage();
            this.knockback = this.knockback * this.petType.getBabyModifierKnockback();
            this.maxHealth = this.maxHealth * this.petType.getBabyModifierMaxHealth();
        }
    }

    public boolean canLevelUp() {
        return this.level < this.petType.getMaxLevel();
    }

    private void updateGrownUpStatus() {
        if (grownUp)
            return;

        int start = ConfigGeneral.GROWUP_START.getAsInt();
        int end = ConfigGeneral.GROWUP_END.getAsInt();

        int levels = end - start;
        double probability = (this.level - start) / (double) levels;

        if (Math.random() < probability)
            this.setGrownUp(true);

    }

    /**
     * Add experience
     * Will make the pet level up if it gains enough experience
     *
     * @param exp The amount of experience to add
     * @return Whether the mob leveled up
     */
    public boolean addExperience(float exp) {
        if (!canLevelUp())
            return false;

        boolean levelUp = experience + exp > experienceRequirement;

        if (levelUp) {
            experience = (experience + exp) % experienceRequirement;
            level++;

            updateGrownUpStatus();

            Bukkit.getServer().getPluginManager().callEvent(new PetLevelUpEvent(this));
            updateStats();
        } else
            experience += exp;

        return levelUp;
    }

}

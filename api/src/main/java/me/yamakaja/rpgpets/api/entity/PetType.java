package me.yamakaja.rpgpets.api.entity;

import org.bukkit.entity.EntityType;

/**
 * Created by Yamakaja on 10.06.17.
 */
public enum PetType {
    COW(EntityType.COW, "cow", "PetCow");

    private EntityType baseType;
    private Class<?> entityClass;
    private String entityId;
    private String entityName;

    private int maxLevel;
    private float baseExpRequirement;
    private float expRequirementModifier;

    private float baseSpeed;
    private float baseAttackDamage;
    private float baseKnockback;
    private float baseMaxHealth;

    private float levelupSpeed;
    private float levelupAttackDamage;
    private float levelupKnockback;
    private float levelupMaxHealth;

    private float babyModifierSpeed;
    private float babyModifierAttackDamage;
    private float babyModifierKnockback;
    private float babyModifierMaxHealth;

    PetType(EntityType baseType, String entityId, String entityName) {
        this.baseType = baseType;
        this.entityId = "rpgpets:" + entityId;
        this.entityName = entityName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public EntityType getBaseType() {
        return baseType;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void initStats(int maxLevel, float baseExpRequirement, float expRequirementModifier, float baseSpeed, float baseAttackDamage, float baseKnockback, float baseMaxHealth, float levelupSpeed, float levelupAttackDamage, float levelupKnockback, float levelupMaxHealth, float babyModifierSpeed, float babyModifierAttackDamage, float babyModifierKnockback, float babyModifierMaxHealth) {
        this.maxLevel = maxLevel;
        this.baseExpRequirement = baseExpRequirement;
        this.expRequirementModifier = expRequirementModifier;
        this.baseSpeed = baseSpeed;
        this.baseAttackDamage = baseAttackDamage;
        this.baseKnockback = baseKnockback;
        this.baseMaxHealth = baseMaxHealth;
        this.levelupSpeed = levelupSpeed;
        this.levelupAttackDamage = levelupAttackDamage;
        this.levelupKnockback = levelupKnockback;
        this.levelupMaxHealth = levelupMaxHealth;
        this.babyModifierSpeed = babyModifierSpeed;
        this.babyModifierAttackDamage = babyModifierAttackDamage;
        this.babyModifierKnockback = babyModifierKnockback;
        this.babyModifierMaxHealth = babyModifierMaxHealth;
    }

    public float getBaseSpeed() {
        return baseSpeed;
    }

    public float getBaseAttackDamage() {
        return baseAttackDamage;
    }

    public float getBaseKnockback() {
        return baseKnockback;
    }

    public float getBaseMaxHealth() {
        return baseMaxHealth;
    }

    public float getLevelupSpeed() {
        return levelupSpeed;
    }

    public float getLevelupAttackDamage() {
        return levelupAttackDamage;
    }

    public float getLevelupKnockback() {
        return levelupKnockback;
    }

    public float getLevelupMaxHealth() {
        return levelupMaxHealth;
    }

    public float getBabyModifierSpeed() {
        return babyModifierSpeed;
    }

    public float getBabyModifierAttackDamage() {
        return babyModifierAttackDamage;
    }

    public float getBabyModifierKnockback() {
        return babyModifierKnockback;
    }

    public float getBabyModifierMaxHealth() {
        return babyModifierMaxHealth;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public float getBaseExpRequirement() {
        return baseExpRequirement;
    }

    public float getExpRequirementModifier() {
        return expRequirementModifier;
    }

    @Override
    public String toString() {
        return "PetType{" +
                "baseType=" + baseType +
                ", entityClass=" + entityClass +
                ", entityId='" + entityId + '\'' +
                ", entityName='" + entityName + '\'' +
                ", maxLevel=" + maxLevel +
                ", baseExpRequirement=" + baseExpRequirement +
                ", expRequirementModifier=" + expRequirementModifier +
                ", baseSpeed=" + baseSpeed +
                ", baseAttackDamage=" + baseAttackDamage +
                ", baseKnockback=" + baseKnockback +
                ", baseMaxHealth=" + baseMaxHealth +
                ", levelupSpeed=" + levelupSpeed +
                ", levelupAttackDamage=" + levelupAttackDamage +
                ", levelupKnockback=" + levelupKnockback +
                ", levelupMaxHealth=" + levelupMaxHealth +
                ", babyModifierSpeed=" + babyModifierSpeed +
                ", babyModifierAttackDamage=" + babyModifierAttackDamage +
                ", babyModifierKnockback=" + babyModifierKnockback +
                ", babyModifierMaxHealth=" + babyModifierMaxHealth +
                '}';
    }
}

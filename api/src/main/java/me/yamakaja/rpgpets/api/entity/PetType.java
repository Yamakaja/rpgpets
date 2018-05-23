package me.yamakaja.rpgpets.api.entity;

import me.yamakaja.rpgpets.api.classgen.PetClassGenerator;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Yamakaja on 10.06.17.
 */
public enum PetType {
    CHICKEN(EntityType.CHICKEN, "chicken", "PetChicken", PetHead.CHICKEN),
    COW(EntityType.COW, "cow", "PetCow", PetHead.COW),
    DONKEY(EntityType.DONKEY, "donkey", "PetDonkey", PetHead.DONKEY),
    HORSE(EntityType.HORSE, "horse", "PetHorse", PetHead.HORSE),
    MUSHROOM_COW(EntityType.MUSHROOM_COW, "mushroom_cow", "PetMushroomCow", PetHead.MUSHROOM_COW),
    OCELOT(EntityType.OCELOT, "ocelot", "PetOcelot", PetHead.OCELOT),
    PIG(EntityType.PIG, "pig", "PetPig", PetHead.PIG),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE, "pig_zombie", "PetPigZombie", PetHead.PIG_ZOMBIE),
    POLAR_BEAR(EntityType.POLAR_BEAR, "polar_bear", "PetPolarBear", PetHead.POLAR_BEAR),
    RABBIT(EntityType.RABBIT, "rabbit", "PetRabbit", PetHead.RABBIT),
    LLAMA(EntityType.LLAMA, "llama", "PetLlama", PetHead.LLAMA),
    SHEEP(EntityType.SHEEP, "sheep", "PetSheep", PetHead.SHEEP),
    VILLAGER(EntityType.VILLAGER, "villager", "PetVillager", PetHead.VILLAGER),
    WOLF(EntityType.WOLF, "wolf", "PetWolf", PetHead.WOLF),
    ZOMBIE(EntityType.ZOMBIE, "zombie", "PetZombie", PetHead.ZOMBIE);

    private static Map<Double, PetType> weightDistributionMap = new LinkedHashMap<>();
    private static List<Double> sortedWeightList = new LinkedList<>();

    private EntityType baseType;
    private Class<?> entityClass;
    private Class<?> entitySuperClass;
    private Constructor<?> constructor;
    private String entityId;
    private String entityName;
    private String typeName;
    private int randomWeight;
    private PetHead mhfName;
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

    PetType(EntityType baseType, String entityId, String entityName, PetHead mhfName) {
        this.baseType = baseType;
        this.entityId = "rpgpets:" + entityId;
        this.entityName = entityName;
        this.mhfName = mhfName;
    }

    public static void initWeightMap() {
        int weightSum = Arrays.stream(PetType.values()).mapToInt(type -> type.randomWeight).sum();
        double lastWeight = 0;

        for (PetType type : PetType.values()) {
            double probability = (double) type.randomWeight / weightSum;
            weightDistributionMap.put(lastWeight + probability, type);
            sortedWeightList.add(lastWeight + probability);
            lastWeight += probability;
        }

        sortedWeightList.sort(Comparator.naturalOrder()); // Probably unnecessary, but a one-time thing anyways
    }

    /**
     * @return A random {@link PetType} based on the weight
     */
    public static PetType getRandomPetType() {
        double random = Math.random();

        for (Double prb : sortedWeightList)
            if (prb > random)
                return weightDistributionMap.get(prb);

        throw new RuntimeException("Failed to selection random element - logic error!");
    }

    public static void generateClasses(PetClassGenerator classGen) {
        for (PetType type : PetType.values())
            type.setEntityClass(classGen.generatePetClass(type));
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;

        try {
            this.constructor = entityClass.getConstructor(PetDescriptor.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public Class<?> getEntitySuperClass() {
        return entitySuperClass;
    }

    public void setEntitySuperClass(Class<?> entitySuperClass) {
        this.entitySuperClass = entitySuperClass;
    }

    public Pet summon(PetDescriptor descriptor) {
        try {
            return (Pet) this.constructor.newInstance(descriptor);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
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

    public void initStats(int maxLevel, float baseExpRequirement, float expRequirementModifier, int randomWeight,
                          float baseSpeed, float baseAttackDamage, float baseKnockback, float baseMaxHealth,
                          float levelupSpeed, float levelupAttackDamage, float levelupKnockback, float levelupMaxHealth,
                          float babyModifierSpeed, float babyModifierAttackDamage, float babyModifierKnockback,
                          float babyModifierMaxHealth) {
        this.maxLevel = maxLevel;
        this.baseExpRequirement = baseExpRequirement;
        this.expRequirementModifier = expRequirementModifier;
        this.baseSpeed = baseSpeed;
        this.randomWeight = randomWeight;

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

    /**
     * @return The name for skulls to display the correct skin
     */
    public PetHead getHead() {
        return mhfName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "PetType{" +
                "baseType=" + baseType +
                ", entityClass=" + entityClass +
                ", entityId='" + entityId + '\'' +
                ", entityName='" + entityName + '\'' +
                ", typeName='" + typeName + '\'' +
                ", randomWeight=" + randomWeight +
                ", mhfName='" + mhfName + '\'' +
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
                "} " + super.toString();
    }
}

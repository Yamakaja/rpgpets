package me.yamakaja.rpgpets.v1_11_R1;

import net.minecraft.server.v1_11_R1.NBTBase;
import net.minecraft.server.v1_11_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_11_R1.PathfinderGoalSelector;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class NMSUtils {

    private static Field pathfinderB = getPrivateField("b", PathfinderGoalSelector.class);
    private static Field pathfinderC = getPrivateField("c", PathfinderGoalSelector.class);
    private static Field pathGoalMeleeD = getPrivateField("d", PathfinderGoalMeleeAttack.class);

    private static Class<?> craftMetaItemClass;

    private static Field unhandledTagsField;

    static {
        try {
            craftMetaItemClass = Class.forName("org.bukkit.craftbukkit.v1_11_R1.inventory.CraftMetaItem");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if (craftMetaItemClass != null) {
                unhandledTagsField = craftMetaItemClass.getDeclaredField("unhandledTags");
                unhandledTagsField.setAccessible(true);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    private NMSUtils() {
    }

    /**
     * Retrieve some custom nbt data
     * <b>Warning:</b> This cannot be used for existing minecraft tags!
     *
     * @param meta The meta to read from
     * @param key  Where to read from
     * @return The value returned
     */
    public static NBTBase getUnhandledTag(ItemMeta meta, String key) {
        try {
            return ((Map<String, NBTBase>) unhandledTagsField.get(meta)).get(key);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Writes a custom tag to some {@link ItemMeta}
     * <b>Warning:</b> This cannot be used to overwrite minecraft tags!
     *
     * @param meta  The item meta to act upon
     * @param key   Where to store the value
     * @param value What to write there
     */
    public static void setUnhandledTag(ItemMeta meta, String key, NBTBase value) {
        try {
            ((Map<String, NBTBase>) unhandledTagsField.get(meta)).put(key, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the pathfinder information off of entities goal and target selectors
     *
     * @param goalSelector   The goal selector to clear
     * @param targetSelector The target selector to clear
     */
    public static void clearGoalsAndTargets(PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector) {
        try {
            ((Set) pathfinderB.get(goalSelector)).clear();
            ((Set) pathfinderC.get(goalSelector)).clear();
            ((Set) pathfinderB.get(targetSelector)).clear();
            ((Set) pathfinderC.get(targetSelector)).clear();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setSpeed(PathfinderGoalMeleeAttack meleeGoal, double speed) {
        try {
            pathGoalMeleeD.set(meleeGoal, speed);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method for getting a private field
     *
     * @param name  The name of the field to get
     * @param owner The owner of the field
     * @return The Field object which has already been made accessible
     */
    public static Field getPrivateField(String name, Class owner) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

}

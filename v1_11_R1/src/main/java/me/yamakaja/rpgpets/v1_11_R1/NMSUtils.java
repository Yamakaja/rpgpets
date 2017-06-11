package me.yamakaja.rpgpets.v1_11_R1;

import net.minecraft.server.v1_11_R1.PathfinderGoalSelector;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class NMSUtils {

    private static Field pathfinderB = getPrivateField("b", PathfinderGoalSelector.class);
    private static Field pathfinderC = getPrivateField("c", PathfinderGoalSelector.class);

    private NMSUtils() {
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

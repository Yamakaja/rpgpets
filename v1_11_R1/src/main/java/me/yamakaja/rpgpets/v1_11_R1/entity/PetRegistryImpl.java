package me.yamakaja.rpgpets.v1_11_R1.entity;

import me.yamakaja.rpgpets.api.entity.PetRegistry;
import net.minecraft.server.v1_11_R1.EntityTypes;
import org.bukkit.entity.EntityType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Yamakaja on 11.06.17.
 */
public class PetRegistryImpl implements PetRegistry {

    private Method registerEntityMethod;

    {
        try {
            registerEntityMethod = EntityTypes.class.getDeclaredMethod("a", int.class, String.class, Class.class, String.class);
            registerEntityMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerEntity(String entityId, EntityType baseType, Class<?> entityClass, String entityName) {
        try {
            registerEntityMethod.invoke(null, baseType.getTypeId() + 1000, entityId, entityClass, entityName);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("An error occurred while trying to register entity!");
            e.printStackTrace();
        }
    }

}

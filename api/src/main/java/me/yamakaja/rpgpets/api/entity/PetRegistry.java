package me.yamakaja.rpgpets.api.entity;

import org.bukkit.entity.EntityType;

/**
 * Created by Yamakaja on 11.06.17.
 */
public interface PetRegistry {

    /**
     * Register a Pet Entity
     *
     * @param entityId    The string-id of the pet
     * @param baseType    The entity the client should see
     * @param entityClass The Class which defines the entity
     * @param entityName  The name of the entity
     */
    void registerEntity(String entityId, EntityType baseType, Class<?> entityClass, String entityName);

}

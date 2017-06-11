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

}

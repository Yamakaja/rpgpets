package me.yamakaja.rpgpets.api;

import me.yamakaja.rpgpets.api.entity.Pet;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetHead;
import me.yamakaja.rpgpets.api.entity.PetRegistry;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by Yamakaja on 10.06.17.
 */
public interface NMSHandler {

    /**
     * @return The NMS version the {@link NMSHandler} implements
     */
    String getNMSVersion();

    /**
     * @return The {@link PetRegistry} which handles registering entities
     */
    PetRegistry getPetRegistry();

    /**
     * @return The plugin instance
     */
    RPGPets getPlugin();

    /**
     * Ads an NMS entity to a world
     *  @param entity The entity to add
     * @param world  The world to add the entity to
     */
    LivingEntity addToWorld(Pet entity, World world);

    /**
     * @return The pet descritor of the passed bukkit entity, null if the entity isn't a pet
     * @param entity
     */
    PetDescriptor getPetDescriptor(LivingEntity entity);

    void setHeadSkin(SkullMeta meta, PetHead head);
}

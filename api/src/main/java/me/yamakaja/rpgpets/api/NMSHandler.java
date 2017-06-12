package me.yamakaja.rpgpets.api;

import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetRegistry;
import me.yamakaja.rpgpets.api.entity.PetType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

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
     * Summon a pet
     */
    void summon(PetDescriptor petDescriptor);

    /**
     * @return The pet descritor of the passed bukkit entity, null if the entity isn't a pet
     */
    PetDescriptor getPetDescriptor(Entity entity);

}

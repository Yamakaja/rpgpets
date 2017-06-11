package me.yamakaja.rpgpets.api;

import me.yamakaja.rpgpets.api.entity.PetRegistry;
import me.yamakaja.rpgpets.api.entity.PetType;
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
     * Summon a pet for a player
     */
    void summon(PetType type, Player owner);

}

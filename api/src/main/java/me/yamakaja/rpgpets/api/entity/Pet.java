package me.yamakaja.rpgpets.api.entity;

import org.bukkit.entity.Player;

/**
 * Created by Yamakaja on 10.06.17.
 */
public interface Pet {

    /**
     * @return The player who owns this pet
     */
    Player getOwner();

    /**
     * @return The pets level
     */
    int getLevel();

    /**
     * @return The pets experience
     */
    float getExperience();

    /**
     * @return The pets type
     */
    PetType getPetType();

}

package me.yamakaja.rpgpets.api.event;

import me.yamakaja.rpgpets.api.entity.Pet;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by david on 12.06.17.
 */
public class PetLevelUpEvent extends Event {

    private static HandlerList handlerList;
    private Pet pet;

    public PetLevelUpEvent(Pet pet) {
        this.pet = pet;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public Pet getPet() {
        return pet;
    }

}

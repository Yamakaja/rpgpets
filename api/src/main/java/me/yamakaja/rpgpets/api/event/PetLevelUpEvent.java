package me.yamakaja.rpgpets.api.event;

import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Yamakaja on 12.06.17.
 */
public class PetLevelUpEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private PetDescriptor pet;

    public PetLevelUpEvent(PetDescriptor pet) {
        this.pet = pet;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public PetDescriptor getPetDescriptor() {
        return pet;
    }

}

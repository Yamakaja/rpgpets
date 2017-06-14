package me.yamakaja.rpgpets.api.entity;

/**
 * Created by Yamakaja on 10.06.17.
 */
public interface Pet {

    void updateAttributes();

    /**
     * @return The {@link PetDescriptor} describing the pet
     */
    PetDescriptor getPetDescriptor();

}

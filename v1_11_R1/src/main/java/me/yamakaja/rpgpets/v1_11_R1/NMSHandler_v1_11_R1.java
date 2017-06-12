package me.yamakaja.rpgpets.v1_11_R1;

import me.yamakaja.rpgpets.api.NMSHandler;
import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.entity.Pet;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetRegistry;
import me.yamakaja.rpgpets.api.entity.PetType;
import me.yamakaja.rpgpets.v1_11_R1.entity.PetCow;
import me.yamakaja.rpgpets.v1_11_R1.entity.PetRegistryImpl;
import net.minecraft.server.v1_11_R1.World;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class NMSHandler_v1_11_R1 implements NMSHandler {

    private RPGPets plugin;
    private PetRegistry petRegistry;

    public NMSHandler_v1_11_R1(RPGPets plugin) {
        this.plugin = plugin;
        this.petRegistry = new PetRegistryImpl();

        PetType.COW.setEntityClass(PetCow.class);
    }

    @Override
    public String getNMSVersion() {
        return "v1_11_R1";
    }

    @Override
    public PetRegistry getPetRegistry() {
        return petRegistry;
    }

    @Override
    public RPGPets getPlugin() {
        return plugin;
    }

    @Override
    public void summon(PetDescriptor petDescriptor) {
        World world = ((CraftPlayer) petDescriptor.getOwner()).getHandle().getWorld();
        switch (petDescriptor.getPetType()) {
            case COW:
                world.addEntity(new PetCow(petDescriptor));
                break;
        }
    }

    @Override
    public PetDescriptor getPetDescriptor(Entity entity) {
        net.minecraft.server.v1_11_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();

        if (nmsEntity instanceof Pet) {
            return ((Pet)nmsEntity).getPetDescriptor();
        }

        return null;
    }
}

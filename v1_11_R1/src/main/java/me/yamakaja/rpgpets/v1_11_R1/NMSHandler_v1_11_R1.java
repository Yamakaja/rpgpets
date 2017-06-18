package me.yamakaja.rpgpets.v1_11_R1;

import me.yamakaja.rpgpets.api.NMSHandler;
import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.entity.Pet;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetRegistry;
import me.yamakaja.rpgpets.api.entity.PetType;
import me.yamakaja.rpgpets.v1_11_R1.entity.*;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PolarBear;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;

/**
 * Created by Yamakaja on 10.06.17.
 */
public class NMSHandler_v1_11_R1 implements NMSHandler {

    private static Field skullGameProfile;

    static {
        try {
            skullGameProfile = CraftItemFactory.instance().getItemMeta(Material.SKULL_ITEM).getClass().getDeclaredField("profile");
            skullGameProfile.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private RPGPets plugin;
    private PetRegistry petRegistry;

    public NMSHandler_v1_11_R1(RPGPets plugin) {
        this.plugin = plugin;
        this.petRegistry = new PetRegistryImpl();

        PetType.CHICKEN.setEntityClass(PetChicken.class);
        PetType.COW.setEntityClass(PetCow.class);
        PetType.LLAMA.setEntityClass(PetLlama.class);
        PetType.MUSHROOM_COW.setEntityClass(PetMushroomCow.class);
        PetType.PIG.setEntityClass(PetPig.class);
        PetType.PIG_ZOMBIE.setEntityClass(PetPigZombie.class);
        PetType.POLAR_BEAR.setEntityClass(PetPolarBear.class);
        PetType.RABBIT.setEntityClass(PetRabbit.class);
        PetType.SHEEP.setEntityClass(PetSheep.class);
        PetType.ZOMBIE.setEntityClass(PetZombie.class);
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
    public LivingEntity addToWorld(Pet entity, org.bukkit.World world) {
        ((CraftWorld) world).getHandle().addEntity((Entity) entity);
        return (LivingEntity) ((Entity) entity).getBukkitEntity();
    }

    @Override
    public PetDescriptor getPetDescriptor(LivingEntity entity) {
        Entity nmsEntity = ((CraftEntity) entity).getHandle();

        if (nmsEntity instanceof Pet) {
            return ((Pet) nmsEntity).getPetDescriptor();
        }

        return null;
    }

    @Override
    public SkullMeta fillSkullMeta(SkullMeta meta) {
        try {
            skullGameProfile.set(meta, TileEntitySkull.skinCache.get(meta.getOwner()));
        } catch (IllegalAccessException | ExecutionException e) {
            e.printStackTrace();
        }

        return meta;
    }

    @Override
    public void preloadSkins() {
        for (PetType petType : PetType.values()) {
            try {
                TileEntitySkull.skinCache.get(petType.getMhfName());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}

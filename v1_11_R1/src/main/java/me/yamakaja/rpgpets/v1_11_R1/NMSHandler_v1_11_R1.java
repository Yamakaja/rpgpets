package me.yamakaja.rpgpets.v1_11_R1;

import me.yamakaja.rpgpets.api.NMSHandler;
import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.entity.Pet;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetRegistry;
import me.yamakaja.rpgpets.api.entity.PetType;
import me.yamakaja.rpgpets.v1_11_R1.entity.PetCow;
import me.yamakaja.rpgpets.v1_11_R1.entity.PetRegistryImpl;
import net.minecraft.server.v1_11_R1.EntityLiving;
import net.minecraft.server.v1_11_R1.TileEntitySkull;
import net.minecraft.server.v1_11_R1.World;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemFactory;
import org.bukkit.entity.LivingEntity;
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
    public LivingEntity summon(PetDescriptor petDescriptor) {
        World world = ((CraftPlayer) petDescriptor.getOwner()).getHandle().getWorld();
        EntityLiving entity = null;
        switch (petDescriptor.getPetType()) {
            case COW:
                world.addEntity(entity = new PetCow(petDescriptor));
                break;
        }

        if (entity != null) {
            petDescriptor.setEntityId(entity.getId());
            return (LivingEntity) entity.getBukkitEntity();
        }

        return null;
    }

    @Override
    public PetDescriptor getPetDescriptor(LivingEntity entity) {
        net.minecraft.server.v1_11_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();

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

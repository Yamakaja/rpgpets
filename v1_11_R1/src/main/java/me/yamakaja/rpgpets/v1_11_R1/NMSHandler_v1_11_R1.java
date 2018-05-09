package me.yamakaja.rpgpets.v1_11_R1;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.yamakaja.rpgpets.api.NMSHandler;
import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.entity.*;
import me.yamakaja.rpgpets.v1_11_R1.entity.*;
import net.minecraft.server.v1_11_R1.ContainerAnvil;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemFactory;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;

/**
 * Created by Yamakaja on 10.06.17.
 */
@SuppressWarnings("Duplicates")
public class NMSHandler_v1_11_R1 implements NMSHandler {

    private static final String NBT_KEY = "rpgpets";
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
        PetType.MUSHROOM_COW.setEntityClass(PetMushroomCow.class);
        PetType.LLAMA.setEntityClass(PetLlama.class);
        PetType.PIG.setEntityClass(PetPig.class);
        PetType.PIG_ZOMBIE.setEntityClass(PetPigZombie.class);
        PetType.POLAR_BEAR.setEntityClass(PetPolarBear.class);
        PetType.RABBIT.setEntityClass(PetRabbit.class);
        PetType.SHEEP.setEntityClass(PetSheep.class);
        PetType.VILLAGER.setEntityClass(PetVillager.class);
        PetType.WOLF.setEntityClass(PetWolf.class);
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
    public PetDescriptor getPetDescriptor(org.bukkit.entity.Entity entity) {
        Entity nmsEntity = ((CraftEntity) entity).getHandle();

        if (nmsEntity instanceof Pet) {
            return ((Pet) nmsEntity).getPetDescriptor();
        }

        return null;
    }

    @Override
    public void setHeadSkin(SkullMeta meta, PetHead head) {
        GameProfile profile = new GameProfile(head.getUUID(), head.getName());
        profile.getProperties().put("textures", new Property("textures", head.getTexture(), head.getSignature()));

        try {
            skullGameProfile.set(meta, profile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writePetDescriptor(ItemStack item, PetDescriptor petDescriptor) {
        ItemMeta itemMeta = item.getItemMeta();

        NBTTagCompound petTag = new NBTTagCompound();
        petTag.setString("type", petDescriptor.getPetType().name());
        petTag.setInt("level", petDescriptor.getLevel());
        petTag.setFloat("exp", petDescriptor.getExperience());
        petTag.setInt("entityId", petDescriptor.getEntityId());
        petTag.setBoolean("adult", petDescriptor.isAdult());
        petTag.setString("state", petDescriptor.getState().name());
        petTag.setDouble("random", Math.random());

        NMSUtils.setUnhandledTag(itemMeta, NBT_KEY, petTag);
        item.setItemMeta(itemMeta);
    }

    @Override
    public PetDescriptor readPetDescriptor(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        NBTTagCompound tag = (NBTTagCompound) NMSUtils.getUnhandledTag(meta, NBT_KEY);

        if (tag == null)
            return null;

        PetDescriptor petDescriptor = new PetDescriptor(
                PetType.valueOf(tag.getString("type")),
                null,
                meta.getDisplayName(),
                tag.getInt("level"),
                tag.getFloat("exp"),
                tag.getBoolean("adult"));

        petDescriptor.setEntityId(tag.getInt("entityId"));
        petDescriptor.setState(PetState.valueOf(tag.getString("state")));

        petDescriptor.setName(meta.getDisplayName());

        return petDescriptor;
    }

    @Override
    public void writeEntityId(ItemStack stack, int entityId) {
        ItemMeta meta = stack.getItemMeta();
        NBTTagCompound nbtTag = (NBTTagCompound) NMSUtils.getUnhandledTag(meta, NBT_KEY);

        if (nbtTag == null)
            return;

        nbtTag.setInt("entityId", entityId);
        stack.setItemMeta(meta);

    }

    /**
     * Returns the window id of an anvil inventory
     *
     * @param inventory The inventory to get the id of
     * @return The window id
     */
    @Override
    public int getWindowId(AnvilInventory inventory) {
        ContainerAnvil container = NMSUtils.getAnvilContainer(inventory);

        if (container == null)
            throw new RuntimeException("Couldn't get container of anvil inventory?!");

        return container.windowId;
    }

    /**
     * Sets the repair cost of an item
     *
     * @param item The item of which to modify the repair cost
     * @param cost The cost to set
     * @return The modified item
     */
    @Override
    public ItemStack setRepairCost(ItemStack item, int cost) {
        net.minecraft.server.v1_11_R1.ItemStack stack = CraftItemStack.asNMSCopy(item);
        stack.setRepairCost(cost);
        return CraftItemStack.asCraftMirror(stack);
    }

}

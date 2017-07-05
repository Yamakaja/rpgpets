package me.yamakaja.rpgpets.api.item;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigGeneral;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetState;
import me.yamakaja.rpgpets.api.entity.PetType;
import me.yamakaja.rpgpets.api.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Yamakaja on 10.06.17.
 */
public enum RPGPetsItem {
    FOOD(() -> new ItemBuilder(Material.SLIME_BALL).setDisplayName(ConfigMessages.ITEM_FOOD_NAME.get())
            .setLore(Arrays.asList(ConfigMessages.ITEM_FOOD_TOOLTIP.get().split("\n")))),
    EGG(() -> new ItemBuilder(Material.MONSTER_EGG)
            .setDisplayName(ConfigMessages.ITEM_EGG_NAME.get()).setLore(Arrays.asList(
                    ConfigMessages.ITEM_EGG_LORE_REMAINING.get(Integer.toString(ConfigGeneral.HATCH_DISTANCE.getAsInt())),
                    ConfigMessages.ITEM_EGG_LORE_HAND.get(),
                    ChatColor.BLACK.toString() + ChatColor.MAGIC + Integer.toString(ConfigGeneral.HATCH_DISTANCE.getAsInt() * 100) + ":" + Double.toString(Math.random()).substring(2)
            ))),
    PET(() -> RPGPetsItem.getPetCarrier(PetType.getRandomPetType(), ConfigMessages.ITEM_PET_DEFAULTNAME.get(), 0, 0, 1, false, PetState.READY));

    private static RPGPets plugin;
    private Supplier<ItemStack> itemStackSupplier;

    RPGPetsItem(Supplier<ItemStack> itemStackSupplier) {
        this.itemStackSupplier = itemStackSupplier;
    }

    public static void initialize(RPGPets plugin) {
        RPGPetsItem.plugin = plugin;
    }

    /**
     * Gets an item which carries a pet
     *
     * @param type     The type of pet
     * @param name     The pets name
     * @param level    Its level
     * @param exp      The pets experience
     * @param grownUp  Whether it is grown up
     * @param petState The state of the pet
     * @return The carrying item stack
     */
    public static ItemStack getPetCarrier(PetType type, String name, int level, float exp, float requiredExp, boolean grownUp, PetState petState) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        PetDescriptor petDescriptor = new PetDescriptor(type, null, name, level, exp, grownUp);
        petDescriptor.setState(petState);

        plugin.getNMSHandler().writePetDescriptor(head, petDescriptor);

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        RPGPetsItem.plugin.getNMSHandler().setHeadSkin(meta, type.getHead());

        meta.setDisplayName(name);

        List<String> lore = new LinkedList<>();

        lore.add(ConfigMessages.ITEM_PET_LORE_TYPE.get(type.name()));
        lore.add(ConfigMessages.ITEM_PET_LORE_LEVEL.get(Integer.toString(level)) + (type.getMaxLevel() == level ? " " + ConfigMessages.ITEM_PET_LORE_MAXLEVEL.get() : ""));
        lore.add(ConfigMessages.ITEM_PET_LORE_EXP.get(Float.toString((int) (100 * exp / requiredExp))));
        lore.add(ConfigMessages.ITEM_PET_LORE_AGE.get((grownUp ? ConfigMessages.ITEM_PET_LORE_ADULT : ConfigMessages.ITEM_PET_LORE_BABY).get()));
        lore.add(ConfigMessages.ITEM_PET_LORE_STATUS.get(petState == PetState.DEAD ? ConfigMessages.ITEM_PET_LORE_DEAD.get()
                : (petState == PetState.READY ? ConfigMessages.ITEM_PET_LORE_READY.get() : ConfigMessages.ITEM_PET_LORE_SPAWNED.get())));
        meta.setLore(lore);

        head.setItemMeta(meta);
        return plugin.getNMSHandler().setRepairCost(head, ConfigMessages.ITEM_PET_DEFAULTNAME.get().equals(name) ? 29 : Short.MAX_VALUE);
    }

    /**
     * Attempts to decode the {@link ItemStack} into a {@link PetDescriptor}
     * Will return null if the passed item cannot be decoded
     *
     * @param itemStack The item to decode
     * @return The result, possibly null
     */
    public static PetDescriptor decode(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.SKULL_ITEM || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()
                || !itemStack.getItemMeta().hasLore())
            return null;

        return plugin.getNMSHandler().readPetDescriptor(itemStack);

    }

    /**
     * Encodes the entity id into the item <br>
     * <b>Warning:</b> Assumes that the passed item is a valid pet item!
     *
     * @param itemStack The item to modify
     * @param entityId  The entity-id to write into it
     * @return The encoded item
     */
    public static ItemStack encodeSpawned(ItemStack itemStack, int entityId) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();

        lore.set(1, ConfigMessages.ITEM_PET_LORE_LEVEL.get("?"));
        lore.set(2, ConfigMessages.ITEM_PET_LORE_EXP.get("?"));
        lore.set(3, ConfigMessages.ITEM_PET_LORE_AGE.get("?"));
        lore.set(4, ConfigMessages.ITEM_PET_LORE_STATUS.get(ConfigMessages.ITEM_PET_LORE_SPAWNED.get()));

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        plugin.getNMSHandler().writeEntityId(itemStack, entityId);

        return itemStack;
    }

    /**
     * Removes the spawned status from a pet carrier and sets its status it to {@link PetState#DEAD}
     *
     * @param stack The item to modify
     * @return The modified item
     */
    public static ItemStack resetPet(ItemStack stack) {

        PetDescriptor petDescriptor = RPGPetsItem.decode(stack);
        petDescriptor.setEntityId(0);
        petDescriptor.setState(PetState.DEAD);

        return RPGPetsItem.getPetCarrier(petDescriptor);
    }

    /**
     * Wrapper for {@link RPGPetsItem#getPetCarrier(PetType, String, int, float, float, boolean, PetState)}
     */
    public static ItemStack getPetCarrier(PetDescriptor petDescriptor) {
        return RPGPetsItem.getPetCarrier(petDescriptor.getPetType(), petDescriptor.getName(), petDescriptor.getLevel(),
                petDescriptor.getExperience(), petDescriptor.getExperienceRequirement(), petDescriptor.isAdult(), petDescriptor.getState());
    }

    public ItemStack get() {
        return itemStackSupplier.get();
    }
}

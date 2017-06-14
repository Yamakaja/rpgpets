package me.yamakaja.rpgpets.api.item;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigGeneral;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.entity.PetType;
import me.yamakaja.rpgpets.api.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Created by Yamakaja on 10.06.17.
 */
public enum RPGPetsItem {
    FOOD(() -> new ItemBuilder(Material.SLIME_BALL).setDisplayName(ConfigMessages.ITEM_FOOD_NAME.get())),
    EGG(() -> new ItemBuilder(Material.MONSTER_EGG).modifyMeta(meta -> ((SpawnEggMeta) meta).setSpawnedType(null))
            .setDisplayName(ConfigMessages.ITEM_EGG_NAME.get()).setLore(Arrays.asList(
                    ConfigMessages.ITEM_EGG_REMAINING.get(Integer.toString(ConfigGeneral.HATCH_DISTANCE.getAsInt())),
                    ChatColor.BLACK.toString() + ChatColor.MAGIC + Integer.toString(ConfigGeneral.HATCH_DISTANCE.getAsInt() * 100) + ":" + Double.toString(Math.random()).substring(2)
            ))),
    PET(null);

    private static RPGPets plugin;
    private Supplier<ItemStack> itemStackSupplier;

    RPGPetsItem(Supplier<ItemStack> itemStackSupplier) {
        this.itemStackSupplier = itemStackSupplier;
    }

    public static void initialize(RPGPets plugin) {
        RPGPetsItem.plugin = plugin;

        RPGPetsItem.PET.itemStackSupplier = () -> {
            ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwner(PetType.getRandomPetType().getMhfName());

            RPGPetsItem.plugin.getNMSHandler().fillSkullMeta(meta);

            head.setItemMeta(meta);
            return head;
        };
    }

    public ItemStack get() {
        return itemStackSupplier.get();
    }

}

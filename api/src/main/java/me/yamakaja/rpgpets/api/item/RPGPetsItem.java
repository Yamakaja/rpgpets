package me.yamakaja.rpgpets.api.item;

import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.function.Supplier;

/**
 * Created by Yamakaja on 10.06.17.
 */
public enum RPGPetsItem {
    FOOD(() -> new ItemBuilder(Material.SLIME_BALL).setDisplayName(ConfigMessages.ITEM_FOOD_NAME.get())),
    EGG(() -> new ItemBuilder(Material.MONSTER_EGG).modifyMeta(meta -> ((SpawnEggMeta) meta).setSpawnedType(null))
            .setDisplayName(ConfigMessages.ITEM_EGG_NAME.get()));

    private Supplier<ItemStack> itemStackSupplier;

    RPGPetsItem(Supplier<ItemStack> itemStackSupplier) {
        this.itemStackSupplier = itemStackSupplier;
    }

    public ItemStack get() {
        return itemStackSupplier.get();
    }

}

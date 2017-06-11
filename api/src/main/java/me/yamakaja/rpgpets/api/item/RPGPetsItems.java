package me.yamakaja.rpgpets.api.item;

import me.yamakaja.rpgpets.api.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

/**
 * Created by Yamakaja on 10.06.17.
 */
public enum RPGPetsItems {
    FOOD(new ItemBuilder(Material.SLIME_BALL).setDisplayName(ChatColor.GREEN + "Pet Food")),
    EGG(new ItemBuilder(Material.MONSTER_EGG).modifyMeta(meta -> ((SpawnEggMeta)meta).setSpawnedType(null)));

    private ItemStack itemStack;

    RPGPetsItems(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack get() {
        return new ItemStack(this.itemStack);
    }

}

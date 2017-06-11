package me.yamakaja.rpgpets.api.util;

import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Yamakaja on 10.6.2017
 */
public class ItemBuilder extends ItemStack {

    /**
     * Creates a new {@link ItemBuilder} identical to the given {@link ItemStack}
     * @param stack The {@link ItemStack} to clone
     */
    public ItemBuilder(ItemStack stack) {
        super(stack);
    }

    /**
     * Creates a new ItemStack
     *
     * @param type The type of the stack: {@link Material}
     */
    public ItemBuilder(Material type) {
        super(type);
    }

    /**
     * Creates a new ItemStack
     *
     * @param type   {@link Material}
     * @param amount The amount
     * @param damage Its damage
     */
    public ItemBuilder(Material type, int amount, short damage) {
        super(type, amount, damage);
    }

    /**
     * Creats a new ItemStack
     *
     * @param type   {@link Material}
     * @param amount The amount
     */
    public ItemBuilder(Material type, int amount) {
        super(type, amount);
    }

    /**
     * Creates a new player head item with a certain skull owner
     *
     * @param skullOwner The skull owner
     */
    public ItemBuilder(String skullOwner) {
        super(Material.SKULL_ITEM, 1, (short) 3);
        this.modifyMeta(meta -> ((SkullMeta) meta).setOwner(skullOwner));
    }

    /**
     * Used to build banners
     *
     * @param patterns  The banners patterns
     * @param baseColor The base color
     */
    public ItemBuilder(List<Pattern> patterns, DyeColor baseColor) {
        super(Material.BANNER, 1);
        this.modifyMeta(meta -> {
            ((BannerMeta) meta).setPatterns(patterns);
            ((BannerMeta) meta).setBaseColor(baseColor);
        });
    }

    /**
     * Creates a new "written" book
     *
     * @param title  The title
     * @param author The author
     * @param pages  The books contents
     */
    public ItemBuilder(String title, String author, List<String> pages) {
        super(Material.WRITTEN_BOOK);
        this.modifyMeta(meta -> {
            ((BookMeta) meta).setAuthor(author);
            ((BookMeta) meta).setTitle(title);
            ((BookMeta) meta).setPages(pages);
        });
    }

    /**
     * Creates a new firework rocket
     *
     * @param effects The {@link FireworkEffect}s
     * @param power   The rocket's power
     */
    public ItemBuilder(List<FireworkEffect> effects, int power) {
        super(Material.FIREWORK);
        this.modifyMeta(meta -> {
            ((FireworkMeta) meta).addEffects(effects);
            ((FireworkMeta) meta).setPower(power);
        });
    }

    /**
     * Creates colored leather armor
     *
     * @param material The type of leather armor
     * @param color    The color to set
     */
    public ItemBuilder(Material material, Color color) {
        super(material);
        if (!(this.getItemMeta() instanceof LeatherArmorMeta))
            throw new RuntimeException("You can only color leather armor!");
        this.modifyMeta(meta -> ((LeatherArmorMeta) meta).setColor(color));
    }

    public ItemBuilder(PotionData basePotionData, Map<PotionEffect, Boolean> customEffects, boolean splash) {
        super(splash ? Material.SPLASH_POTION : Material.POTION);
        this.modifyMeta(meta -> {
            PotionMeta pMeta = (PotionMeta) meta;
            pMeta.setBasePotionData(basePotionData);
            if (customEffects == null) return;
            customEffects.forEach(pMeta::addCustomEffect);
        });
    }

    /**
     * Sets the lore of the ItemStack
     *
     * @param lore The Lore
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder setLore(List<String> lore) {
        this.modifyMeta(meta -> meta.setLore(lore));
        return this;
    }

    /**
     * Adds lines to the lore
     *
     * @param lines The lines to add to the lore
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder addLore(String... lines) {
        this.modifyMeta(meta -> {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.addAll(Arrays.asList(lines));
            meta.setLore(lore);
        });
        return this;
    }

    /**
     * Returns a specific line of the lore
     *
     * @param line The line to return
     * @return The line
     * @throws IndexOutOfBoundsException
     */
    public String getLore(int line) throws IndexOutOfBoundsException{
        return this.getItemMeta().getLore().get(line);
    }

    /**
     * Clears the {@link ItemMeta}
     *
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder clearMeta() {
        this.setItemMeta(Bukkit.getItemFactory().getItemMeta(this.getType()));
        return this;
    }

    /**
     * Sets the display name
     *
     * @param name The display name to set
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder setDisplayName(String name) {
        this.modifyMeta(meta -> meta.setDisplayName(name));
        return this;
    }

    /**
     * Clears the display name
     *
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder clearDisplayName() {
        setDisplayName("");
        return this;
    }

    /**
     * Adds an enchantment to the item
     *
     * @param enchantment The enchantment to add
     * @param level       It's level
     * @param unsafe      Whether or not to allow unsafe enchantments
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean unsafe) {
        if (unsafe) this.addUnsafeEnchantment(enchantment, level);
        else super.addEnchantment(enchantment, level);
        return this;
    }

    /**
     * Adds multiple enchantments to the item
     *
     * @param enchs  A {@link Map}<{@link Enchantment},{@link Integer}> of enchantments and levels to add
     * @param unsafe Whether or not to allow unsafe enchantments
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchs, boolean unsafe) {
        if (unsafe) this.addUnsafeEnchantments(enchs);
        else this.addEnchantments(enchs);
        return this;
    }

    /**
     * Clears all enchantments from the ItemStack
     *
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder clearEnchantments() {
        this.getEnchantments().forEach((ench, level) -> this.removeEnchantment(ench));
        return this;
    }

    /**
     * Adds {@link ItemFlag}s to the ItemStack
     *
     * @param flags The {@link ItemFlag}s to add
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder addItemFlags(ItemFlag... flags) {
        this.modifyMeta(meta -> meta.addItemFlags(flags));
        return this;
    }

    /**
     * Removes {@link ItemFlag}s from the ItemStack
     *
     * @param flags The {@link ItemFlag}s to remove
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder removeItemFlags(ItemFlag... flags) {
        this.modifyMeta(meta -> meta.removeItemFlags(flags));
        return this;
    }

    /**
     * Allows you to directly modify the meta
     *
     * @param metaConsumer A lambda which may modify the {@link ItemMeta}
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder modifyMeta(Consumer<ItemMeta> metaConsumer) {
        ItemMeta meta = this.hasItemMeta() ? this.getItemMeta() : Bukkit.getItemFactory().getItemMeta(this.getType());
        metaConsumer.accept(meta);
        this.setItemMeta(meta);
        return this;
    }

    /**
     * Allows you to set the stack amount
     *
     * @param amount Stack size
     * @return The {@link ItemBuilder}
     */
    public ItemBuilder setCount(int amount) {
        this.setAmount(amount);
        return this;
    }
}
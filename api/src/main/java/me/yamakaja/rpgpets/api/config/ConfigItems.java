package me.yamakaja.rpgpets.api.config;

import me.yamakaja.rpgpets.api.item.RPGPetsItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;


/**
 * Created by Yamakaja on 5/12/18.
 */
public final class ConfigItems {

    public static Material FOOD_MATERIAL;

    private ConfigItems() {
        throw new UnsupportedOperationException("Utility constructor!");
    }

    public static void initialize(Plugin plugin, YamlConfiguration config) {
        FOOD_MATERIAL = Material.valueOf(config.getString("food.material"));

        if (config.getBoolean("food.recipe.enabled")) {
            ShapedRecipe shapedRecipe = new ShapedRecipe(RPGPetsItem.FOOD.get());
            shapedRecipe.shape(config.getStringList("food.recipe.grid").toArray(new String[3]));

            try {
                for (String key : config.getConfigurationSection("food.recipe.items").getKeys(false))
                    shapedRecipe.setIngredient(key.charAt(0), Material.valueOf(config.getString("food.recipe.items." + key)));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Unknown material in crafting recipe: ", e);
            }

            plugin.getServer().addRecipe(shapedRecipe);
        }

        if (config.getBoolean("egg.recipe.enabled")) {
            ShapedRecipe shapedRecipe = new ShapedRecipe(RPGPetsItem.EGG.get());
            shapedRecipe.shape(config.getStringList("egg.recipe.grid").toArray(new String[3]));

            try {
                for (String key : config.getConfigurationSection("egg.recipe.items").getKeys(false))
                    shapedRecipe.setIngredient(key.charAt(0), Material.valueOf(config.getString("egg.recipe.items." + key)));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Unknown material in crafting recipe: ", e);
            }

            plugin.getServer().addRecipe(shapedRecipe);
        }
    }

}

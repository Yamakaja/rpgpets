package me.yamakaja.rpgpets.api.config;

import me.yamakaja.rpgpets.api.item.RPGPetsItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;


/**
 * Created by Yamakaja on 5/12/18.
 */
public final class ConfigItems implements Listener {

    public static Material FOOD_MATERIAL;
    public static boolean FOOD_GLOWING;

    private static String FOOD_RECIPE_PERMISSION;
    private static String EGG_RECIPE_PERMISSION;

    private static ShapedRecipe FOOD_RECIPE;
    private static ShapedRecipe EGG_RECIPE;

    private static ConfigItems instance;

    private ConfigItems() {
//        throw new UnsupportedOperationException("Utility constructor!");
    }

    public static void initialize(Plugin plugin, YamlConfiguration config) {
        FOOD_MATERIAL = Material.valueOf(config.getString("food.material"));
        FOOD_GLOWING = config.getBoolean("food.glowing");

        FOOD_RECIPE_PERMISSION = config.getString("food.recipe.permission");
        EGG_RECIPE_PERMISSION = config.getString("egg.recipe.permission");

        if (FOOD_RECIPE_PERMISSION != null && FOOD_RECIPE_PERMISSION.isEmpty())
            FOOD_RECIPE_PERMISSION = null;

        if (EGG_RECIPE_PERMISSION != null && EGG_RECIPE_PERMISSION.isEmpty())
            EGG_RECIPE_PERMISSION = null;

        if (instance != null)
            CraftItemEvent.getHandlerList().unregister(instance);

        if (FOOD_RECIPE_PERMISSION != null || EGG_RECIPE_PERMISSION != null)
            plugin.getServer().getPluginManager().registerEvents(instance = new ConfigItems(), plugin);

        if (config.getBoolean("food.recipe.enabled")) {
            FOOD_RECIPE = new ShapedRecipe(RPGPetsItem.FOOD.get());
            FOOD_RECIPE.shape(config.getStringList("food.recipe.grid").toArray(new String[3]));

            try {
                for (String key : config.getConfigurationSection("food.recipe.items").getKeys(false))
                    FOOD_RECIPE.setIngredient(key.charAt(0), Material.valueOf(config.getString("food.recipe.items." + key)));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Unknown material in crafting recipe: ", e);
            }

            plugin.getServer().addRecipe(FOOD_RECIPE);
        }

        if (config.getBoolean("egg.recipe.enabled")) {
            EGG_RECIPE = new ShapedRecipe(RPGPetsItem.EGG.get());
            EGG_RECIPE.shape(config.getStringList("egg.recipe.grid").toArray(new String[3]));

            try {
                for (String key : config.getConfigurationSection("egg.recipe.items").getKeys(false))
                    EGG_RECIPE.setIngredient(key.charAt(0), Material.valueOf(config.getString("egg.recipe.items." + key)));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Unknown material in crafting recipe: ", e);
            }

            plugin.getServer().addRecipe(EGG_RECIPE);
        }
    }

    @EventHandler
    private void onCraft(CraftItemEvent e) {
        Material type = e.getRecipe().getResult().getType();

        if (type == FOOD_MATERIAL && FOOD_RECIPE_PERMISSION != null
                && e.getRecipe().getResult().isSimilar(FOOD_RECIPE.getResult())
                && !e.getWhoClicked().hasPermission(FOOD_RECIPE_PERMISSION)) {
            e.setCancelled(true);
            e.getWhoClicked().sendMessage(ConfigMessages.GENERAL_NOCRAFTPERM.get());
        } else if (type == Material.MONSTER_EGG && EGG_RECIPE_PERMISSION != null
                && e.getRecipe().getResult().isSimilar(EGG_RECIPE.getResult())
                && !e.getWhoClicked().hasPermission(EGG_RECIPE_PERMISSION)) {
            e.setCancelled(true);
            e.getWhoClicked().sendMessage(ConfigMessages.GENERAL_NOCRAFTPERM.get());
        }
    }

}

package me.yamakaja.rpgpets.api.item;

import me.yamakaja.rpgpets.api.RPGPets;
import me.yamakaja.rpgpets.api.config.ConfigMessages;
import me.yamakaja.rpgpets.api.entity.PetDescriptor;
import me.yamakaja.rpgpets.api.entity.PetState;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

/**
 * Created by Yamakaja on 17.06.17.
 */
public class RecipeManager implements Listener {

    private RPGPets plugin;
    private ShapelessRecipe recipe;

    public RecipeManager(RPGPets plugin) {
        this.plugin = plugin;

        recipe = new ShapelessRecipe(new ItemStack(Material.SKULL_ITEM, 1, (short) 3));

        recipe.addIngredient(Material.SLIME_BALL);
        recipe.addIngredient(new MaterialData(Material.SKULL_ITEM, (byte) 3));

        this.plugin.getServer().addRecipe(recipe);
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        MaterialData data = event.getRecipe().getResult().getData();

        if (data.getItemType() != Material.SKULL_ITEM || data.getData() != 3)
            return;

        ItemStack[] matrix = event.getInventory().getMatrix();

        PetDescriptor pet = null;
        ItemStack food = null;

        for (ItemStack iterItem : matrix) {
            if (iterItem == null)
                continue;

            if (iterItem.getType() == Material.SLIME_BALL && iterItem.hasItemMeta() && iterItem.getItemMeta().hasDisplayName()
                    && iterItem.getItemMeta().getDisplayName().equals(ConfigMessages.ITEM_FOOD_NAME.get())) {
                food = iterItem;
            } else {
                PetDescriptor petDescriptor = RPGPetsItem.decode(iterItem, null);
                if (petDescriptor != null)
                    pet = petDescriptor;
            }

            if (pet != null && food != null)
                break;
        }

        if (pet == null || food == null || pet.getState() != PetState.DEAD) {
            event.getInventory().setResult(null);
            return;
        }

        pet.setState(PetState.READY);
        event.getInventory().setResult(RPGPetsItem.getPetCarrier(pet));
    }

}
